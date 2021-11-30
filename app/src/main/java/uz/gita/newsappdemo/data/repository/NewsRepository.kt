package uz.gita.newsappdemo.data.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import uz.gita.newsappdemo.data.model.Article
import uz.gita.newsappdemo.data.model.ResponseNews

interface NewsRepository {

    fun getAllNews(country : String,page : Int) : Flow<Call<ResponseNews>>

    fun addNews(article: Article) : Long

    fun delete(article: Article)

    fun searchNews(search :String, pages :Int) : Flow<Call<ResponseNews>>

    fun getSavedNews() : List<Article>
}