package uz.gita.newsappdemo.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import uz.gita.newsappdemo.data.model.Article

@Dao
interface NewDao {

    @Query("SELECT * FROM articles")
    fun getAll() : LiveData<List<Article>>

    @Delete
    fun delete(article: Article)
}