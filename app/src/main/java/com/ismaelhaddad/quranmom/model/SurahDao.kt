package com.ismaelhaddad.quranmom.model

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SurahDao {
    @Query("SELECT * FROM surah")
    fun getAll(): Flow<List<Surah>>
}