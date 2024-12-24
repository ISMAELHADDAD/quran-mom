package com.ismaelhaddad.quranmom.model

import androidx.room.ColumnInfo

data class AyahWordWithSegment(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "ayah_number") val ayahNumber: Int,
    @ColumnInfo(name = "word_order") val wordOrder: Int,
    @ColumnInfo(name = "word_text") val wordText: String,
    @ColumnInfo(name = "segment_start") val segmentStart: Long,
    @ColumnInfo(name = "segment_end") val segmentEnd: Long
)