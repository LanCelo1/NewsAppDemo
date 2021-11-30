package uz.gita.newsappdemo.data.model

data class ResponseNews(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)