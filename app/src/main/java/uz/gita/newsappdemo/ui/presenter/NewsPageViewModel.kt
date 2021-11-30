package uz.gita.newsappdemo.ui.presenter

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import uz.gita.newsappdemo.App
import uz.gita.newsappdemo.data.model.Article
import uz.gita.newsappdemo.data.model.ResponseNews
import uz.gita.newsappdemo.data.repository.NewsRepository
import uz.gita.newsappdemo.data.repository.NewsRepositoryImpl
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class NewsPageViewModel @Inject constructor(
    private var repository: NewsRepository
) : ViewModel() {
    var _getNewsLiveData: MutableLiveData<ResultData<ResponseNews>> = MutableLiveData()
    val getNewsLiveData: LiveData<ResultData<ResponseNews>> get() = _getNewsLiveData
    var newspage = 1
    var newsResponse : ResponseNews? = null


    init {
        getSafeNews("us")
    }

    private fun getCommonNews(country: String) {
        repository.getAllNews(country,newspage).onEach { call ->
            call.enqueue(object : Callback<ResponseNews> {
                override fun onResponse(
                    call: Call<ResponseNews>,
                    response: Response<ResponseNews>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            newspage++
                            if (newsResponse == null){
                                newsResponse = it
                            }else {
                                val oldArticle = newsResponse?.articles
                                val newArticle = it.articles
                                oldArticle?.addAll(newArticle)
                            }
                            _getNewsLiveData.postValue(ResultData.Success(newsResponse ?: it))
                        }
                    }/* else {
                        try {
                            _getNewsLiveData.postValue(ResultData.Error(data = null,
                                response.message()))
                        } catch (e: Exception) {
                            Timber.d("error = ${e.message}")
                        }
                    }*/
                }

                override fun onFailure(call: Call<ResponseNews>, t: Throwable) {
                    try {
                        _getNewsLiveData.postValue(ResultData.Error(data = null,
                            t.message ?: "eror"))
                    } catch (e: Exception) {
                        Timber.d("error = ${e.message}")
                    }
                }

            })

        }.launchIn(viewModelScope)
    }


    fun getSafeNews(countryCode: String) {
        try {
            if(hasInternetConnection()) {
                _getNewsLiveData.postValue(ResultData.Loading(null,null))
                getCommonNews(countryCode)
            } else {
                _getNewsLiveData.postValue(ResultData.Error(null,"No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> {
                    _getNewsLiveData.postValue(ResultData.Error(null,"Network Failure"))
                }
                else -> {
                    _getNewsLiveData.postValue(ResultData.Error(null,"Conversion error"))
                }
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = App.instance.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}
