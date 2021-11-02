package com.example.clean_architecture_mvvm_news_app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.clean_architecture_mvvm_news_app.domain.models.Article

@Database(entities = [Article::class], version = 2)
@TypeConverters(Converters::class)
abstract class ArticleDataBase : RoomDatabase() {

    abstract fun newsArticleDao(): ArticleDao


}