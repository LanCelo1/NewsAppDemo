package uz.gita.newsappdemo.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uz.gita.newsappdemo.App
import uz.gita.newsappdemo.data.model.Article
import uz.gita.newsappdemo.utils.TypeConventors

@Database(entities = [Article::class], version = 1)
@TypeConverters(TypeConventors::class)
abstract class RoomDatabase : RoomDatabase() {
    abstract fun newDao() : NewDao

    companion object{
        @Volatile
        lateinit var INSTANCE : RoomDatabase

        fun getInstance(app: App) : RoomDatabase{
            return INSTANCE ?: synchronized(this){
                var temp = Room.databaseBuilder(app.applicationContext,RoomDatabase::class.java,"news_database")
                    .build()
                INSTANCE = temp
                temp
            }
        }
    }
}