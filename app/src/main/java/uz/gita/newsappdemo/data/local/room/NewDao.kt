package uz.gita.newsappdemo.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import uz.gita.newsappdemo.data.model.Article

@Dao
interface NewDao {

    @Query("SELECT * FROM articles")
    fun getAll() : List<Article>

    @Delete
    fun delete(article: Article)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNews(article: Article) : Long
}