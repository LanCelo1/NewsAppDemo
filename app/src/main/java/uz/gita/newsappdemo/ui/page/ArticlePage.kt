package uz.gita.newsappdemo.ui.page

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import uz.gita.newsappdemo.MainActivity
import uz.gita.newsappdemo.R
import uz.gita.newsappdemo.databinding.PageArticleBinding
import uz.gita.newsappdemo.ui.presenter.ArticleViewModel
import uz.gita.newsappdemo.ui.presenter.NewsViewModel

@AndroidEntryPoint
class ArticlePage : Fragment(R.layout.page_article) {

    private val argument: ArticlePageArgs by navArgs()
    private lateinit var binding: PageArticleBinding
    private val viewModel : ArticleViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = PageArticleBinding.bind(view)
        //viewModel = (activity as MainActivity).viewModel
        var url = argument.navArgs.url
        if (url != null)
            binding.webView.loadUrl(url!!)

        binding.saveItemDatabase.setOnClickListener {
            viewModel.insertNews(argument.navArgs)
            Snackbar.make(view, "Article add successfully!", Snackbar.LENGTH_SHORT).show()
        }
    }
}