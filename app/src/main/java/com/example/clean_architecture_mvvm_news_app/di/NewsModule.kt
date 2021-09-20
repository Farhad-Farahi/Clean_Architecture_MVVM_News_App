package com.example.clean_architecture_mvvm_news_app.di

import android.app.Application
import androidx.room.Room
import com.example.clean_architecture_mvvm_news_app.api.NewsAPI
import com.example.clean_architecture_mvvm_news_app.db.ArticleDataBase
import com.example.clean_architecture_mvvm_news_app.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NewsModule {

    @Provides
    @Singleton
    fun provideRetrofitInstance(): NewsAPI =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsAPI::class.java)


    @Provides
    @Singleton
    fun provideDatabase(app: Application): ArticleDataBase =
        Room.databaseBuilder(app, ArticleDataBase::class.java, "news_article_database")
            .fallbackToDestructiveMigration()
            .build()


}