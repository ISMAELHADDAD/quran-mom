package com.ismaelhaddad.quranmom.service

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking

object DatabaseService {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    // Coroutine scope for database operations
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Function to get the database instance
    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            // Block calling thread until database is initialized
            runBlocking {
                val instance = initializeDatabase(context)
                INSTANCE = instance
                instance
            }
        }
    }

    // Initialize the database asynchronously
    private suspend fun initializeDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "Sample.db"
        )
            .createFromAsset("last_juz.sqlite")
            .build()
    }
}
