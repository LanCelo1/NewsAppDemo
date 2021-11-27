package uz.gita.newsappdemo.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import uz.gita.newsappdemo.data.model.Article
import uz.gita.newsappdemo.data.model.ResponseNews
import uz.gita.newsappdemo.data.remote.retrofit.ApiInterface
import uz.gita.newsappdemo.data.remote.retrofit.ApiService

class NewsRepository()  {
    var api = ApiService.retrofit.create(ApiInterface::class.java)

    fun getAllNews(country : String) : Flow<Call<ResponseNews>> = flow {
        Timber.tag("TTT").d("repository is work ")
        emit(api.getTopHeadlineNews(country))
    }
}




/*
* .enqueue(object : Callback<ResponseNews>{
            override fun onResponse(call: Call<ResponseNews>, response: Response<ResponseNews>) {
                if (response.isSuccessful){
                    response.body()?.let {
                        emit(ResultData.Success(it.articles))
                    }
                }
            }

            override fun onFailure(call: Call<ResponseNews>, t: Throwable) {
                emit(ResultData.Error(t.message!!))
            }

        })
* */