package com.cebola.rekords.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcher
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainDispatcher
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class UnconfinedDispatcher
@Module
@InstallIn(SingletonComponent::class)
object CoroutineModule {
    
    @ApplicationScope
    @Singleton
    @Provides
    fun provideApplicationScope(
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
    ): CoroutineScope {
  
              return CoroutineScope(SupervisorJob() + defaultDispatcher)
    }
    
    @DefaultDispatcher
    @Provides
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
    
    @IoDispatcher
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
    
    @MainDispatcher
    @Provides
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
    
    @UnconfinedDispatcher
    @Provides
    fun provideUnconfinedDispatcher(): CoroutineDispatcher = Dispatchers.Unconfined
}