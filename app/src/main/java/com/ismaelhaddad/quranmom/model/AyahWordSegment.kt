package com.ismaelhaddad.quranmom.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AyahWordSegment(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "surah_audio_id") val surahAudioId: Int,
    @ColumnInfo(name = "ayah_global_number") val ayahGlobalNumber: Int,
    @ColumnInfo(name = "ayah_number") val ayahNumber: Int,
    @ColumnInfo(name = "timestamp_from") val timestampFrom: Int,
    @ColumnInfo(name = "timestamp_to") val timestampTo: Int,
    @ColumnInfo(name = "ayah_word_id") val ayahWordId: Int,
    @ColumnInfo(name = "segment_start") val segmentStart: Int,
    @ColumnInfo(name = "segment_end") val segmentEnd: Int
)
