package uz.gita.newsappdemo.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import timber.log.Timber
import uz.gita.newsappdemo.data.local.room.NewDatabase
import uz.gita.newsappdemo.data.model.Article
import uz.gita.newsappdemo.data.model.ResponseNews
import uz.gita.newsappdemo.data.remote.retrofit.ApiInterface
import uz.gita.newsappdemo.data.remote.retrofit.ApiService
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private var db : NewDatabase
) : NewsRepository {
    private var api = ApiService.retrofit.create(ApiInterface::class.java)

    override fun getAllNews(country: String, pages : Int): Flow<Call<ResponseNews>> = flow {
        Timber.tag("TTT").d("repository is work ")
        emit(api.getTopHeadlineNews(country, pages))
    }


    override fun searchNews(search: String,pages: Int): Flow<Call<ResponseNews>> = flow {
        emit(api.searchNews(search,pages))
    }

    override fun addNews(article: Article) =
        db.newDao().insertNews(article)

    override fun getSavedNews()  = db.newDao().getAll()

    override fun delete(article : Article) =db.newDao().delete(article)
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