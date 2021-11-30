package uz.gita.newsappdemo.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uz.gita.newsappdemo.data.repository.NewsRepository
import uz.gita.newsappdemo.data.repository.NewsRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {

    @Binds
    fun bindRepository(repositoryImpl: NewsRepositoryImpl) : NewsRepository
}