package uz.gita.newsappdemo.data.remote.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import uz.gita.newsappdemo.data.model.ResponseNews

interface ApiInterface {

    /**
     * Get news for newsPage
     * */

    @GET("top-headlines")
    fun getTopHeadlineNews(
        @Query("country") country: String,
        @Query("apiKey") apiKey: String = "3347d1feb4fb4ac6a56a1f4af963e73c",
    ): Call<ResponseNews>


    /**
     * Search news
     * */

    @GET("everything")
    fun searchNews(
        @Query("q") search: String,
        @Query("apiKey") apiKey: String = "3347d1feb4fb4ac6a56a1f4af963e73c",
    ): Call<ResponseNews>
}