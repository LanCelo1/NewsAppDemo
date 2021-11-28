package uz.gita.newsappdemo.ui.page

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import uz.gita.newsappdemo.MainActivity
import uz.gita.newsappdemo.R
import uz.gita.newsappdemo.data.model.Article
import uz.gita.newsappdemo.databinding.PageNewsBinding
import uz.gita.newsappdemo.databinding.PageSearchBinding
import uz.gita.newsappdemo.ui.adapter.NewsAdapter
import uz.gita.newsappdemo.ui.presenter.NewsViewModel
import uz.gita.newsappdemo.ui.presenter.ResultData

class SearchPage : Fragment(R.layout.page_search) {
    private lateinit var viewModel: NewsViewModel
    private lateinit var binding: PageSearchBinding
    private val newsAdapter: NewsAdapter by lazy { NewsAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= PageSearchBinding.bind(view)
        viewModel = (activity as  MainActivity).viewModel

        binding.apply {

            recyclerView.apply {
                adapter = newsAdapter.apply {
                    this.onItemClickListener {
                        findNavController().navigate(SearchPageDirections.actionSearchPageToArticlePage(it))
                    }
                }
                layoutManager = LinearLayoutManager(this@SearchPage.context)
            }


            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                private var coroutineScope : CoroutineScope = CoroutineScope(Dispatchers.Main)

                private var searchJob : Job? = null

                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchJob?.cancel()
                    searchJob = coroutineScope.launch {
                        newText?.let {
                            delay(500)
                            if (it.isEmpty()){
                                // reset request
                            }
                            else {
                                viewModel.safeSearchNewsCall(it)
                            }
                        }
                    }
                    return false
                }
            })
        }


        viewModel.searchNewsLiveData.observe(this,searchNewsObserver)
    }

    private val searchNewsObserver = Observer<ResultData<List<Article>>> {result ->
        when (result) {
            is ResultData.Success -> {
                newsAdapter.asyncList.submitList(result.data)
                //Toast.makeText(context, "data = ${result.data}",Toast.LENGTH_SHORT).show()
            }
            is ResultData.Error -> {
                Toast.makeText(context, "error = ${result.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}