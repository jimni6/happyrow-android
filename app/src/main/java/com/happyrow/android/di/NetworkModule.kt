package com.happyrow.android.di

import com.happyrow.android.BuildConfig
import com.happyrow.android.data.remote.AuthInterceptor
import com.happyrow.android.data.remote.api.ContributionApi
import com.happyrow.android.data.remote.api.EventApi
import com.happyrow.android.data.remote.api.ParticipantApi
import com.happyrow.android.data.remote.api.ResourceApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(): AuthInterceptor {
        return AuthInterceptor { null }
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL.trimEnd('/') + "/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideEventApi(retrofit: Retrofit): EventApi =
        retrofit.create(EventApi::class.java)

    @Provides
    @Singleton
    fun provideParticipantApi(retrofit: Retrofit): ParticipantApi =
        retrofit.create(ParticipantApi::class.java)

    @Provides
    @Singleton
    fun provideResourceApi(retrofit: Retrofit): ResourceApi =
        retrofit.create(ResourceApi::class.java)

    @Provides
    @Singleton
    fun provideContributionApi(retrofit: Retrofit): ContributionApi =
        retrofit.create(ContributionApi::class.java)
}
