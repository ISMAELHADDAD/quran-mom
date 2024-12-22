package com.ismaelhaddad.quranmom.ui.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ismaelhaddad.quranmom.model.AyahWord
import com.ismaelhaddad.quranmom.model.Reciter
import com.ismaelhaddad.quranmom.database.AppDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest

class PageViewModel(private val database: AppDatabase) : ViewModel() {

    private val _selectedSurahNumber = MutableStateFlow(-1)
    private val _selectedReciter = MutableStateFlow<Reciter?>(null)

    private val selectedSurahNumber: StateFlow<Int> = _selectedSurahNumber
    val selectedReciter: StateFlow<Reciter?> = _selectedReciter

    val reciters: Flow<List<Reciter>> = database.reciterDao().getAll()
    @OptIn(ExperimentalCoroutinesApi::class)
    val ayahs: Flow<Map<Int, List<AyahWord>>> = selectedSurahNumber.flatMapLatest { surahNumber ->
        database.surahDao().getAyahs(surahNumber)
    }

    fun setSelectedSurahNumber(surahNumber: Int) {
        _selectedSurahNumber.value = surahNumber
    }

    fun setSelectedReciter(reciter: Reciter) {
        _selectedReciter.value = reciter
    }

    class Factory(private val database: AppDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PageViewModel::class.java)) {
                return PageViewModel(database) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}