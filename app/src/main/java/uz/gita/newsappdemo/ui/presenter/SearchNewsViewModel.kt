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
import okhttp3.internal.userAgent
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
class SearchNewsViewModel @Inject constructor(
    private var repository: NewsRepository
) : ViewModel() {
    var _searchNewsLiveData: MutableLiveData<ResultData<ResponseNews>> = MutableLiveData()
    val searchNewsLiveData: LiveData<ResultData<ResponseNews>> get() = _searchNewsLiveData
    var searchNewspage = 1
    var searchResponse : ResponseNews? = null
    var lastText = ""

    private fun searchNews(search: String) {
        if (lastText.isEmpty()) lastText = search.trim()
        repository.searchNews(search,searchNewspage).onEach { result ->
            result.enqueue(object : Callback<ResponseNews> {
                override fun onResponse(
                    call: Call<ResponseNews>,
                    response: Response<ResponseNews>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                          /*  searchNewspage++
                            if (searchNewspage != 2 && search.trim() == lastText ){
                                var oldArticle =searchResponse?.articles
                                var article =  it.articles
                                oldArticle?.addAll(article)
                            }
                            if (search.trim() != lastText ){
                                searchNewspage = 1
                            }*/
//                            if (searchResponse == null){
//                                searchResponse = it
//                            }else{
//
//                            }
                            _searchNewsLiveData.postValue(ResultData.Success(searchResponse ?: it))
                        }
                    } else {
                        try {
                            _searchNewsLiveData.postValue(ResultData.Error(data = null,
                                response.message()))
                        } catch (e: Exception) {
                            Timber.d("error = ${e.message}")
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseNews>, t: Throwable) {
                    try {
                        _searchNewsLiveData.postValue(ResultData.Error(data = null,
                            t.message ?: "eror"))
                    } catch (e: Exception) {
                        Timber.d("error = ${e.message}")
                    }
                }

            })
        }.launchIn(viewModelScope)
    }

    fun getSafeSearchNews(searchQuery: String) {
        try {
            if(hasInternetConnection()) {
                searchNews(searchQuery)
            } else {
                _searchNewsLiveData.postValue(ResultData.Error(null,"NO internet  connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> {
                    _searchNewsLiveData.postValue(ResultData.Error(null,"Network failure"))
                }
                else -> {
                    _searchNewsLiveData.postValue(ResultData.Error(null,"Conversion error"))
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
