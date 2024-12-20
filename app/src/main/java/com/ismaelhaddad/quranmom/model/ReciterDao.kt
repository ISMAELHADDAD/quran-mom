package com.ismaelhaddad.quranmom.model

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReciterDao {
    @Query("SELECT * FROM reciter")
    fun getAll(): Flow<List<Reciter>>

    @Query("SELECT * FROM reciter WHERE id IN (:reciterIds)")
    fun loadAllByIds(reciterIds: IntArray): List<Reciter>
}