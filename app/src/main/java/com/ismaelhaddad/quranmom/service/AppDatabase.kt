package com.ismaelhaddad.quranmom.service

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ismaelhaddad.quranmom.model.AyahWord
import com.ismaelhaddad.quranmom.model.AyahWordSegment
import com.ismaelhaddad.quranmom.model.Reciter
import com.ismaelhaddad.quranmom.model.ReciterDao
import com.ismaelhaddad.quranmom.model.Surah
import com.ismaelhaddad.quranmom.model.SurahAudio
import com.ismaelhaddad.quranmom.model.SurahAudioDao
import com.ismaelhaddad.quranmom.model.SurahDao

@Database(entities = [
    Reciter::class,
    Surah::class,
    SurahAudio::class,
    AyahWord::class,
    AyahWordSegment::class],
    version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reciterDao(): ReciterDao
    abstract fun surahDao(): SurahDao
    abstract fun surahAudioDao(): SurahAudioDao
}