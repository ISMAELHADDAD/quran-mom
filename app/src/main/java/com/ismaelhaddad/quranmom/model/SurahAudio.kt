package com.ismaelhaddad.quranmom.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "surah_audio",
    foreignKeys = [
        ForeignKey(
            entity = Surah::class,
            parentColumns = ["number"],
            childColumns = ["surah_number"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = Reciter::class,
            parentColumns = ["id"],
            childColumns = ["reciter_id"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class SurahAudio(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "reciter_id") val reciterId: Int,
    @ColumnInfo(name = "surah_number") val surahNumber: Int,
    @ColumnInfo(name = "audio_url") val audioUrl: String,
    @ColumnInfo(name = "path_file") val pathFile: String
)
