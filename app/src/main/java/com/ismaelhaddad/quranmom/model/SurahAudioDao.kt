package com.ismaelhaddad.quranmom.model

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SurahAudioDao {
    @Query("SELECT * FROM surah_audio")
    fun getAll(): List<SurahAudio>
}