package com.example.clean_architecture_mvvm_news_app.domain.models

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)