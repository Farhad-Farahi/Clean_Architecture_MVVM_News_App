package com.example.clean_architecture_mvvm_news_app.utils

import com.example.clean_architecture_mvvm_news_app.BuildConfig

object Constants {

    const val BASE_URL = "https://newsapi.org"

    const val API_KEY = BuildConfig.news_api_access_key

    const val SEARCH_NEWS_TIME_DELAY = 500L
    const val QUERY_PAGE_SIZE = 20

    const val TYPE_SEARCH_AND_BREAKING_FRAGMENT = 1
    const val TYPE_SAVED_FRAGMENT = 2


}