package com.cube.cubeacademy.lib.di

import android.app.Application
import androidx.room.Room
import com.cube.cubeacademy.BuildConfig
import com.cube.cubeacademy.lib.api.ApiService
import com.cube.cubeacademy.lib.api.AuthTokenInterceptor
import com.cube.cubeacademy.lib.db.NominationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideApi(): ApiService = OkHttpClient().newBuilder().apply {
        addInterceptor(AuthTokenInterceptor())
    }.build().let {
        Retrofit.Builder()
            .client(it)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.API_URL).build().create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNominationDatabase(app: Application): NominationDatabase {
        return Room.databaseBuilder(
            app, NominationDatabase::class.java, "nomination_db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideRepository(api: ApiService, db:NominationDatabase): Repository = Repository(api, db.nominationDao)
}