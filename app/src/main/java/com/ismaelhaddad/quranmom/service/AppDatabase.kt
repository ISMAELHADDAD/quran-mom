package com.ismaelhaddad.quranmom.service

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ismaelhaddad.quranmom.model.Reciter
import com.ismaelhaddad.quranmom.model.ReciterDao

@Database(entities = [Reciter::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reciterDao(): ReciterDao
}