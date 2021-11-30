package uz.gita.newsappdemo.data.remote.retrofit

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.gita.newsappdemo.App
import uz.gita.newsappdemo.utils.Commons
import uz.gita.newsappdemo.utils.Commons.BASE_URl

object ApiService {
    val httpLoggingInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY}

    val httpClient = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .build()

    val cacheSize = (5 * 1024 * 1024).toLong()
    val myCache = Cache(App.instance.applicationContext.cacheDir, cacheSize)

    val okHttpClient = OkHttpClient.Builder()
        .cache(myCache)
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor { chain ->
            var request = chain.request()
            request = if (hasNetwork(App.instance.applicationContext)!!)
                request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
            else
                request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build()
            chain.proceed(request)
        }
        .build()

    var retrofit = Retrofit.Builder()
        .baseUrl(BASE_URl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun hasNetwork(context: Context): Boolean? {
        var isConnected: Boolean? = false // Initial Value
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected)
            isConnected = true
        return isConnected
    }
}
