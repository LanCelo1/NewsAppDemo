package uz.gita.newsappdemo.ui.presenter

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.media.audiofx.DynamicsProcessing
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.util.Config
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import uz.gita.newsappdemo.App
import uz.gita.newsappdemo.MainActivity
import uz.gita.newsappdemo.data.model.Article
import uz.gita.newsappdemo.data.model.ResponseNews
import uz.gita.newsappdemo.data.repository.NewsRepository
import java.io.IOException
import java.lang.Exception

class NewsViewModel(
    private var repository: NewsRepository,
) : ViewModel() {

    init {
        getCommonNews("us")
        getSavedNews()
        Timber.tag("TTT").d("viewMOdel is work  ")
    }

    var _getNewsLiveData: MutableLiveData<ResultData<List<Article>>> = MutableLiveData()
    val getNewsLiveData: LiveData<ResultData<List<Article>>> get() = _getNewsLiveData

    var _searchNewsLiveData: MutableLiveData<ResultData<List<Article>>> = MutableLiveData()
    val searchNewsLiveData: LiveData<ResultData<List<Article>>> get() = _searchNewsLiveData

    var _saveNewsLiveData: MutableLiveData<List<Article>> = MutableLiveData()
    var saveNewsLiveData: LiveData<List<Article>> = MutableLiveData()

    var _insertNewLiveData: MutableLiveData<Long> = MutableLiveData()
    val insertNewLiveData: LiveData<Long> get() = _insertNewLiveData

    var _deleteNewLiveData: MutableLiveData<Unit> = MutableLiveData()
    val deleteNewLiveData: LiveData<Unit> get() = _deleteNewLiveData

    private fun getCommonNews(country: String) {
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
                    } else {
                        try {
                            _getNewsLiveData.postValue(ResultData.Error(data = null,
                                response.message()))
                        } catch (e: Exception) {
                            Timber.d("error = ${e.message}")
                        }
                    }
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

    private fun searchNews(search: String) {
        repository.searchNews(search).onEach { result ->
            result.enqueue(object : Callback<ResponseNews> {
                override fun onResponse(
                    call: Call<ResponseNews>,
                    response: Response<ResponseNews>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            Timber.tag("TTT").d("viewMOdel is work = ${it.articles} ")
                            _searchNewsLiveData.postValue(ResultData.Success(it.articles))
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


    fun getSavedNews() = viewModelScope.launch(Dispatchers.IO) {
        try{
            saveNewsLiveData = repository.getSavedNews()
        } catch (e : Exception){
            Timber.tag("TTT").d("error getSavedNews ${e.message}")
        }
    }

    fun insertNews(article: Article) = viewModelScope.launch (Dispatchers.IO){
       try {
           _insertNewLiveData.postValue(repository.addNews(article))
       } catch (e : Exception){
           Timber.tag("TTT").d("error insertNew ${e.message}")
       }
    }

    fun delete(article: Article) = viewModelScope.launch(Dispatchers.IO) {
        try {
            repository.delete(article)
            _deleteNewLiveData.postValue(Unit)
        } catch (e : Exception){
            Timber.tag("TTT").d("error delete ${e.message}")
        }
    }

    fun safeSearchNewsCall(searchQuery: String) {
      //  newSearchQuery = searchQuery
//        _searchNewsLiveData.postValue(ResultData.Loading())
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

    fun safeBreakingNewsCall(countryCode: String) {
        try {
            if(hasInternetConnection()) {
               getCommonNews("us")
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
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}

sealed class ResultData<T>(
    val data: T? = null,
    val message: String? = null,
) {
    class Success<T>(data: T) : ResultData<T>(data)
    class Error<T>(data: T? = null, message: String) : ResultData<T>(data, message)
}