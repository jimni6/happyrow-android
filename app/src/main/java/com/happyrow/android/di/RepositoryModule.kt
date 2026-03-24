package com.happyrow.android.di

import com.happyrow.android.data.repository.ContributionRepositoryImpl
import com.happyrow.android.data.repository.EventRepositoryImpl
import com.happyrow.android.data.repository.ParticipantRepositoryImpl
import com.happyrow.android.data.repository.ResourceRepositoryImpl
import com.happyrow.android.domain.repository.ContributionRepository
import com.happyrow.android.domain.repository.EventRepository
import com.happyrow.android.domain.repository.ParticipantRepository
import com.happyrow.android.domain.repository.ResourceRepository
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
    abstract fun bindEventRepository(impl: EventRepositoryImpl): EventRepository

    @Binds
    @Singleton
    abstract fun bindParticipantRepository(impl: ParticipantRepositoryImpl): ParticipantRepository

    @Binds
    @Singleton
    abstract fun bindResourceRepository(impl: ResourceRepositoryImpl): ResourceRepository

    @Binds
    @Singleton
    abstract fun bindContributionRepository(impl: ContributionRepositoryImpl): ContributionRepository
}
