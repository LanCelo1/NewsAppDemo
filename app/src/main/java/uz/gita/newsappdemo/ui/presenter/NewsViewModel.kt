package uz.gita.newsappdemo.ui.presenter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import uz.gita.newsappdemo.data.model.Article
import uz.gita.newsappdemo.data.model.ResponseNews
import uz.gita.newsappdemo.data.repository.NewsRepository
import java.lang.Exception

class NewsViewModel(
    private var repository: NewsRepository,
) : ViewModel() {

    init {
        getCommonNews("us")
        Timber.tag("TTT").d("viewMOdel is work  ")
    }

    var _getNewsLiveData: MutableLiveData<ResultData<List<Article>>> = MutableLiveData()
    val getNewsLiveData: LiveData<ResultData<List<Article>>> get() = _getNewsLiveData

    fun getCommonNews(country: String) {
        repository.getAllNews(country).onEach { call ->
            call.enqueue(object : Callback<ResponseNews> {
                override fun onResponse(
                    call: Call<ResponseNews>,
                    response: Response<ResponseNews>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            Timber.tag("TTT").d("viewMOdel is work = ${it.articles} ")
                            _getNewsLiveData.postValue(ResultData.Success(it.articles))
                        }
                    }else {
                        try {
                            _getNewsLiveData.postValue(ResultData.Error(data = null,
                                response.message()))
                        } catch (e : Exception){
                            Timber.d("error = ${e.message}")
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseNews>, t: Throwable) {
                    try {
                        _getNewsLiveData.postValue(ResultData.Error(data = null,t.message ?: "eror"))
                    } catch (e : Exception){
                        Timber.d("error = ${e.message}")
                    }
                }

            })

        }.launchIn(viewModelScope)
    }
}

sealed class ResultData<T>(
    val data: T? = null,
    val message: String? = null,
) {
    class Success<T>(data: T) : ResultData<T>(data)
    class Error<T>(data : T? = null, message: String) : ResultData<T>(data,message)
}