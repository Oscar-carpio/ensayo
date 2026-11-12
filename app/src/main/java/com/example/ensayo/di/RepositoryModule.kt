package com.example.ensayo.di

import com.example.ensayo.data.repository.OfflineSongRepository
import com.example.ensayo.domain.repository.SongRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindSongRepository(impl: OfflineSongRepository): SongRepository
}
