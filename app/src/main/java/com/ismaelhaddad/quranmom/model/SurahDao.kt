package com.ismaelhaddad.quranmom.model

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SurahDao {
    @Query("SELECT * FROM surah")
    fun getAll(): List<Surah>
}