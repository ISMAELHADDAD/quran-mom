package com.ismaelhaddad.quranmom.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SurahAudio(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "reciter_id") val surahNumber: Int,
    @ColumnInfo(name = "surah_number") val ayahNumber: Int,
    @ColumnInfo(name = "audio_url") val audioUrl: String,
    @ColumnInfo(name = "path_file") val pathFile: String
)
