package com.example.ensayo.di

import android.content.Context
import androidx.room.Room
import com.example.ensayo.data.local.EnsayoDatabase
import com.example.ensayo.data.local.SongDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): EnsayoDatabase =
        Room.databaseBuilder(context, EnsayoDatabase::class.java, "ensayo.db").build()

    @Provides
    fun provideSongDao(database: EnsayoDatabase): SongDao = database.songDao()
}
