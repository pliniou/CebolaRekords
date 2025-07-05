package com.cebolarekords.player.di

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.cebolarekords.player.player.PlaybackService
import com.google.common.util.concurrent.ListenableFuture
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {

    @Provides
    @Singleton
    fun provideSessionToken(@ApplicationContext context: Context): SessionToken {
        return SessionToken(context, ComponentName(context, PlaybackService::class.java))
    }

    @Provides
    @Singleton
    fun provideControllerFuture(
        @ApplicationContext context: Context,
        sessionToken: SessionToken
    ): ListenableFuture<MediaController> {
        return MediaController.Builder(context, sessionToken).buildAsync()
    }
}