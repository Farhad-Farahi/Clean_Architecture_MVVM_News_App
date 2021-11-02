package com.example.clean_architecture_mvvm_news_app.domain.repository

import com.example.clean_architecture_mvvm_news_app.data.api.NewsAPI
import com.example.clean_architecture_mvvm_news_app.data.db.ArticleDataBase
import com.example.clean_architecture_mvvm_news_app.domain.models.Article
import javax.inject.Inject


class NewsRepository @Inject constructor(
    private val newsApi: NewsAPI,
    private val newsArticleDb: ArticleDataBase
) {


    //Retrofit
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        newsApi.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        newsApi.searchForNews(searchQuery, pageNumber)


    //DataBase
    suspend fun upsert(article: Article) = newsArticleDb.newsArticleDao().upsert(article)

    fun getSavedNews() = newsArticleDb.newsArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) =
        newsArticleDb.newsArticleDao().deleteArticle(article)


}