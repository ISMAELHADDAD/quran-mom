package com.ismaelhaddad.quranmom.service

import android.content.Context
import android.util.Log
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object DatabaseService {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    // Coroutine scope for database operations
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Function to get the database instance
    fun getDatabase(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
        INSTANCE ?: initializeDatabase(context).also { INSTANCE = it }
    }

    // Initialize the database asynchronously
    private fun initializeDatabase(context: Context): AppDatabase {
        return try {
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "quran_mom.db"
            )
                .createFromAsset("last_juz.db")
                .build()
        } catch (e: Exception) {
            Log.e("DatabaseService", "Error initializing database: ${e.message}", e)
            throw Exception(e)
        }
    }
}
