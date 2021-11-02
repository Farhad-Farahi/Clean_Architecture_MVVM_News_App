package com.example.clean_architecture_mvvm_news_app.data.api

import com.example.clean_architecture_mvvm_news_app.domain.models.NewsResponse
import com.example.clean_architecture_mvvm_news_app.utils.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET(EndPoints.HEAD_LINES)
    suspend fun getBreakingNews(
        @Query("country") countryCode: String = "us",
        @Query("page") pageNumber: Int = 1,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<NewsResponse> // return type


    @GET(EndPoints.SEARCH_IN_EVERYTHING)
    suspend fun searchForNews(
        @Query("q") searchQuery: String,
        @Query("page") pageNumber: Int = 1,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<NewsResponse>


}