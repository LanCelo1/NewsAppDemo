package uz.gita.newsappdemo.ui.presenter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import uz.gita.newsappdemo.data.model.Article
import uz.gita.newsappdemo.data.repository.NewsRepository
import uz.gita.newsappdemo.data.repository.NewsRepositoryImpl
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private var repository: NewsRepository
) : ViewModel() {
    var _insertNewLiveData: MutableLiveData<Long> = MutableLiveData()
    val insertNewLiveData: LiveData<Long> get() = _insertNewLiveData

    fun insertNews(article: Article) = viewModelScope.launch (Dispatchers.IO){
        try {
            _insertNewLiveData.postValue(repository.addNews(article))
        } catch (e : Exception){
            Timber.tag("TTT").d("error insertNew ${e.message}")
        }
    }
}