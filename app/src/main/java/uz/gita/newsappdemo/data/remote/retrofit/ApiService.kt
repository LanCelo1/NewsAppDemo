package uz.gita.newsappdemo.data.remote.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.gita.newsappdemo.utils.Commons
import uz.gita.newsappdemo.utils.Commons.BASE_URl

object ApiService {
    val httpLoggingInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY}

    val httpClient = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .build()

    var retrofit = Retrofit.Builder()
        .baseUrl(BASE_URl)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}