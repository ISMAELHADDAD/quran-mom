package com.ismaelhaddad.quranmom.model

import androidx.room.Dao
import androidx.room.MapColumn
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SurahDao {
    @Query("SELECT * FROM surah")
    fun getAll(): Flow<List<Surah>>

    @Query("SELECT * FROM ayah_word WHERE surah_number = (:surahId) ORDER BY ayah_number, word_order")
    fun getAyahs(surahId: Int): Flow<Map<@MapColumn(columnName = "ayah_number") Int, List<AyahWord>>>

}