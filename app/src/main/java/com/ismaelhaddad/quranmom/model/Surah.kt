package com.ismaelhaddad.quranmom.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surah")
data class Surah(
    @PrimaryKey @ColumnInfo(name = "number") val number: Int,
    @ColumnInfo(name = "name") val name: String,
)
