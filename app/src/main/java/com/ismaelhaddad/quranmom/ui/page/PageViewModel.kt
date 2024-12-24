package com.ismaelhaddad.quranmom.ui.page

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import com.ismaelhaddad.quranmom.model.Reciter
import com.ismaelhaddad.quranmom.database.AppDatabase
import com.ismaelhaddad.quranmom.model.AyahWordWithSegment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.File

class PageViewModel(private val application: Application, private val database: AppDatabase) : ViewModel() {

    private val _selectedSurahNumber = MutableStateFlow(-1)
    private val _selectedReciter = MutableStateFlow<Reciter?>(null)
    private val _currentWord = MutableStateFlow<AyahWordWithSegment?>(null)
    private val _player = ExoPlayer.Builder(application).build()

    private val selectedSurahNumber: StateFlow<Int> = _selectedSurahNumber
    val selectedReciter: StateFlow<Reciter?> = _selectedReciter
    val currentWord: StateFlow<AyahWordWithSegment?> = _currentWord
    private val player: ExoPlayer
        get() = _player

    private val wordHighlighter = WordHighlighter(player) { highlightedWord ->
        _currentWord.value = highlightedWord
    }

    val reciters: Flow<List<Reciter>> = database.reciterDao().getAll()

    @OptIn(ExperimentalCoroutinesApi::class)
    val ayahs: Flow<Map<Int, List<AyahWordWithSegment>>> = combine(
        selectedSurahNumber,
        selectedReciter
    ) { surahNumber, reciter ->
        flow {
            if (surahNumber != -1 && reciter != null) {
                emit(
                    database.surahDao().getAyahs(surahNumber, reciter.id).first()
                )
            } else {
                emit(emptyMap())
            }
        }
    }.flatMapLatest { it }

    @OptIn(ExperimentalCoroutinesApi::class)
    val audioFilePath: Flow<String?> = combine(
        selectedSurahNumber,
        selectedReciter
    ) { surahNumber, reciter ->
        flow {
            if (surahNumber != -1 && reciter != null) {
                emit(
                    database.surahAudioDao().getSurahAudioPathFileByReciterId(surahNumber, reciter.id).first()
                )
            } else {
                emit(null)
            }
        }
    }.flatMapLatest { it }

    init {
        viewModelScope.launch {
            audioFilePath.collect { path ->
                if (path != null) {
                    loadAudioFile(path)
                }
            }
        }

        viewModelScope.launch {
            ayahs.collect {
                wordHighlighter.setWordSegments(it.flatMap { ayah -> ayah.value })
            }
        }
    }

    private fun loadAudioFile(filePath: String) {
        val file = File(application.dataDir, "audio$filePath")
        val mediaItem = MediaItem.Builder()
            .setUri(Uri.fromFile(file))
            .build()
        _player.setMediaItem(mediaItem)
        _player.prepare()
    }

    fun playSegment(startMs: Long, endMs: Long) {
        _player.seekTo(startMs)
        _player.play()
        viewModelScope.launch {
            delay(endMs - startMs)
            _player.pause()
        }
    }

    fun playFrom(startMs: Long) {
        _player.seekTo(startMs)
        _player.play()
    }

    fun setSelectedSurahNumber(surahNumber: Int) {
        _selectedSurahNumber.value = surahNumber
    }

    fun setSelectedReciter(reciter: Reciter) {
        _selectedReciter.value = reciter
    }

    override fun onCleared() {
        wordHighlighter.release()
        _player.release()
        super.onCleared()
    }

    class Factory(private val application: Application, private val database: AppDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PageViewModel::class.java)) {
                return PageViewModel(application, database) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}