package uz.gita.newsappdemo.ui.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uz.gita.newsappdemo.data.repository.NewsRepository

class ViewModelProviderFactory(var repository: NewsRepository) : ViewModelProvider.Factory  {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(repository) as T
    }

}