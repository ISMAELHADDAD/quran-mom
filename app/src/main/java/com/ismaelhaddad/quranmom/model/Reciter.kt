package com.ismaelhaddad.quranmom.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Reciter(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String
)
