package uz.gita.newsappdemo.data.model

data class ResponseNews(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)