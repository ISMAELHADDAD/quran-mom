package com.ismaelhaddad.quranmom.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ismaelhaddad.quranmom.QURANMOM_AUDIO_DIR
import com.ismaelhaddad.quranmom.model.Reciter
import com.ismaelhaddad.quranmom.service.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

class ReciterSurahAudioViewModel(private val database: AppDatabase) : ViewModel() {

    val reciters: Flow<List<Reciter>> = database.reciterDao().getAll()

    // Factory to create the ViewModel with the database dependency
    class Factory(private val database: AppDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReciterSurahAudioViewModel::class.java)) {
                return ReciterSurahAudioViewModel(database) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    // Function to check and download audios
    fun checkAndDownloadAudios(context: Context, progressCallback: (Int, Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val surahsAudios = database.surahAudioDao().getAll()
            var progress = 0
            val total = surahsAudios.size
            Log.v("DownloadService", "AAAA! total $total")

            for (surahAudio in surahsAudios) {
                val file = File(surahAudio.pathFile)
                if (!file.exists()) {
                    downloadAudio(context, surahAudio.audioUrl, surahAudio.pathFile)
                }
                progress++
                withContext(Dispatchers.Main) {
                    progressCallback(progress, total)
                }
            }
        }
    }

    // Function to download the audio and save to the specified path
    private fun downloadAudio(context: Context, url: String, pathFile: String): String? {
        return try {
            // Define the target file path in internal storage
            val targetFile = File(context.filesDir, "$QURANMOM_AUDIO_DIR$pathFile")

            // Ensure the parent directory exists
            targetFile.parentFile?.mkdirs()

            // Download the file
            URL(url).openStream().use { input ->
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            targetFile.absolutePath // Return the file path if download is successful
        } catch (e: Exception) {
            e.printStackTrace()
            null // Return null if the download fails
        }
    }
}