package com.cebola.rekords.di

import android.content.Context
import androidx.room.Room
import com.cebola.rekords.data.database.AppDatabase
import com.cebola.rekords.data.database.TrackDao
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "cebolarekords_database"
        ).fallbackToDestructiveMigration()
         .build()
    }
    @Provides
 
       fun provideTrackDao(appDatabase: AppDatabase): TrackDao {
        return appDatabase.trackDao()
    }
}