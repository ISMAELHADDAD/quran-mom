package com.ismaelhaddad.quranmom.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "ayah_word",
    foreignKeys = [
        ForeignKey(
            entity = Surah::class,
            parentColumns = ["number"],
            childColumns = ["surah_number"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class AyahWord(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "surah_number") val surahNumber: Int,
    @ColumnInfo(name = "ayah_global_number") val ayahGlobalNumber: Int,
    @ColumnInfo(name = "ayah_number") val ayahNumber: Int,
    @ColumnInfo(name = "word_order") val wordOrder: Int,
    @ColumnInfo(name = "word_text") val wordText: String
)