package com.example.clean_architecture_mvvm_news_app.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.clean_architecture_mvvm_news_app.domain.models.Article


@Dao
interface ArticleDao {



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long  //upsert = update(if it is already in database) or insert if its not

    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)

}