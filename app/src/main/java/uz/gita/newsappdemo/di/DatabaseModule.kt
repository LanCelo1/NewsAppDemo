package uz.gita.newsappdemo.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.gita.newsappdemo.data.local.room.NewDao
import uz.gita.newsappdemo.data.local.room.NewDatabase
import uz.gita.newsappdemo.data.repository.NewsRepository
import uz.gita.newsappdemo.data.repository.NewsRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun getNewsDao(appDatabase: NewDatabase): NewDao {
        return appDatabase.newDao()
    }

    @Provides
    @Singleton
    fun appDatabase(@ApplicationContext context: Context): NewDatabase {
        return Room
            .databaseBuilder(context.applicationContext, NewDatabase::class.java, "news_database")
            .build()
    }

}