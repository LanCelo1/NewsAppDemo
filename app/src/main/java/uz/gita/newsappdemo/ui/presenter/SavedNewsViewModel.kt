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
class SavedNewsViewModel @Inject constructor(
    private var repository: NewsRepository
): ViewModel() {

    init {
        Timber.tag("TTT").d("init getSavedNews in SavedNewsViewModel")
        getSavedNews()
    }

    var _saveNewsLiveData: MutableLiveData<List<Article>> = MutableLiveData()
    val saveNewsLiveData: LiveData<List<Article>> get() = _saveNewsLiveData

    var _insertNewLiveData: MutableLiveData<Long> = MutableLiveData()
    val insertNewLiveData: LiveData<Long> get() = _insertNewLiveData

    var _deleteNewLiveData: MutableLiveData<Unit> = MutableLiveData()
    val deleteNewLiveData: LiveData<Unit> get() = _deleteNewLiveData


    fun getSavedNews() = viewModelScope.launch(Dispatchers.IO) {
        Timber.tag("TTT").d("func  getSavedNews in SavedNewsViewModel")
        try{
            _saveNewsLiveData.postValue(repository.getSavedNews())
        } catch (e : Exception){
            Timber.tag("TTT").d("error getSavedNews ${e.message}")
        }
    }

    fun insertNews(article: Article) = viewModelScope.launch (Dispatchers.IO){
        try {
            _insertNewLiveData.postValue(repository.addNews(article))
            _saveNewsLiveData.postValue(repository.getSavedNews())
        } catch (e : Exception){
            Timber.tag("TTT").d("error insertNew ${e.message}")
        }
    }

    fun delete(article: Article) = viewModelScope.launch(Dispatchers.IO) {
        try {
            repository.delete(article)
            _saveNewsLiveData.postValue(repository.getSavedNews())
        } catch (e : Exception){
            Timber.tag("TTT").d("error delete ${e.message}")
        }
    }

}