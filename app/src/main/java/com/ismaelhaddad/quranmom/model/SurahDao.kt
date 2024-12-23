package com.ismaelhaddad.quranmom.model

import androidx.room.Dao
import androidx.room.MapColumn
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SurahDao {
    @Query("SELECT * FROM surah")
    fun getAll(): Flow<List<Surah>>

    @Query("""
        SELECT aw.ayah_number, aw.word_order, aw.word_text, aws.segment_start, aws.segment_end
        FROM surah_audio sa
        INNER JOIN ayah_word aw ON sa.surah_number = aw.surah_number
        LEFT JOIN ayah_word_segment aws ON aw.id = aws.ayah_word_id AND aws.surah_audio_id = sa.id 
        WHERE sa.surah_number = :surahNumber AND sa.reciter_id = :reciterId
        ORDER BY aw.ayah_number, aw.word_order
    """)
    fun getAyahs(surahNumber: Int, reciterId: Int): Flow<Map<@MapColumn(columnName = "ayah_number") Int, List<AyahWordWithSegment>>>
}