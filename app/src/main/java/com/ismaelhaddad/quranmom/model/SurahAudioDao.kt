package com.ismaelhaddad.quranmom.model

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SurahAudioDao {
    @Query("SELECT * FROM surah_audio")
    fun getAll(): List<SurahAudio>

    @Query("SELECT path_file FROM surah_audio WHERE surah_number = (:surahNumber) and reciter_id = (:reciterId)")
    fun getSurahAudioPathFileByReciterId(surahNumber: Int, reciterId: Int): Flow<String>
}