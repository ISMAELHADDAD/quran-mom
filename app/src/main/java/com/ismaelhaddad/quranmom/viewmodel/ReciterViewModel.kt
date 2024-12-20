package com.ismaelhaddad.quranmom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ismaelhaddad.quranmom.model.Reciter
import com.ismaelhaddad.quranmom.service.AppDatabase
import kotlinx.coroutines.flow.Flow

class ReciterViewModel(private val database: AppDatabase) : ViewModel() {

    // Fetch data from the database as a Flow
    val reciters: Flow<List<Reciter>> = database.reciterDao().getAll()

    // Factory to create the ViewModel with the database dependency
    class Factory(private val database: AppDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReciterViewModel::class.java)) {
                return ReciterViewModel(database) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}