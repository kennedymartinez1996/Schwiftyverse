package com.portfolio.schwiftyverse.di

import android.content.Context
import com.portfolio.schwiftyverse.BuildConfig
import com.portfolio.schwiftyverse.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @BaseUrl
    fun provideBaseUrl(): String = BuildConfig.BASE_URL
    @Provides
    @DefaultUnknown
    fun provideDefaultUnknown(@ApplicationContext ctx: Context): String =
        ctx.getString(R.string.default_unknown)

    @Provides
    @DefaultImage
    fun provideDefaultImage(@ApplicationContext ctx: Context): String =
        ctx.getString(R.string.default_image_url)
}