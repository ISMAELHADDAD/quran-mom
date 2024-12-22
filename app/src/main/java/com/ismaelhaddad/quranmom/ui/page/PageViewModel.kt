package com.ismaelhaddad.quranmom.ui.page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ismaelhaddad.quranmom.model.Reciter
import com.ismaelhaddad.quranmom.service.AppDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class PageViewModel(private val database: AppDatabase) : ViewModel() {

    private val _selectedSurahNumber = MutableStateFlow(-1)
    private val _selectedReciter = MutableStateFlow<Reciter?>(null)

    val reciters: Flow<List<Reciter>> = database.reciterDao().getAll()

    val selectedSurahNumber: StateFlow<Int> = _selectedSurahNumber
    val selectedReciter: StateFlow<Reciter?> = _selectedReciter

    val combinedText: StateFlow<String> = combine(
        selectedSurahNumber,
        selectedReciter
    ) { surahNumber, reciter ->
        if (surahNumber != -1 && reciter != null) {
            "Surah: $surahNumber - Reciter: ${reciter.name}"
        } else {
            "Select Surah and Reciter"
        }
    }.stateIn(viewModelScope,
        SharingStarted.WhileSubscribed(5000), "Select Surah and Reciter")

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