package com.mindera.alfie.data.di

import com.mindera.alfie.data.repository.GitHubRepositoryImpl
import com.mindera.alfie.domain.repository.RepoRepository
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
    abstract fun bindRepoRepository(
        gitHubRepositoryImpl: GitHubRepositoryImpl
    ): RepoRepository
}
