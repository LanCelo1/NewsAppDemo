package uz.gita.newsappdemo.ui.page

import android.os.Bundle
import android.view.SearchEvent
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import uz.gita.newsappdemo.MainActivity
import uz.gita.newsappdemo.R
import uz.gita.newsappdemo.data.model.Article
import uz.gita.newsappdemo.data.model.ResponseNews
import uz.gita.newsappdemo.databinding.PageSearchBinding
import uz.gita.newsappdemo.ui.adapter.NewsAdapter
import uz.gita.newsappdemo.ui.presenter.NewsPageViewModel
import uz.gita.newsappdemo.ui.presenter.NewsViewModel
import uz.gita.newsappdemo.ui.presenter.ResultData
import uz.gita.newsappdemo.ui.presenter.SearchNewsViewModel
import uz.gita.newsappdemo.utils.Commons

@AndroidEntryPoint
class SearchPage : Fragment(R.layout.page_search) {
    private var _binding: PageSearchBinding? = null
    private val binding: PageSearchBinding get() = _binding!!
    private val newsAdapter: NewsAdapter by lazy { NewsAdapter() }
    private val viewModel: SearchNewsViewModel by viewModels()
    private var text: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = PageSearchBinding.bind(view)

        binding.apply {

            recyclerView.apply {
                adapter = newsAdapter.apply {
                    this.onItemClickListener {
                        findNavController().navigate(SearchPageDirections.actionSearchPageToArticlePage(
                            it))
                    }
                }
                layoutManager = LinearLayoutManager(this@SearchPage.context)
                addOnScrollListener(scrollListener)
            }

            var searchJob: Job? = null
            searchView.addTextChangedListener { editable ->
                searchJob?.cancel()
                searchJob = MainScope().launch {
                    delay(500)
                    editable?.let {
                        //Toast.makeText(context,"editable = ${it.toString()}",Toast.LENGTH_SHORT).show()
                        if (editable.toString().isNotEmpty()) {
                            viewModel.getSafeSearchNews(editable.toString())
                        }
                    }
                }
            }
        }

           /* searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                private var coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)



                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchJob?.cancel()
                    searchJob = coroutineScope.launch {
                        newText?.let {
                            delay(500)
                            Timber.tag("TTTT").d("newText = $it")
                            if (it.isEmpty()) {
                                // reset request
                            } else {
                                text = it
                                viewModel.getSafeSearchNews(it)
                            }
                        }
                    }
                    return false
                }
            })
        }
*/

        viewModel.searchNewsLiveData.observe(this, searchNewsObserver)
    }

    var isLoading = false
    var isLastPage = false
    var isScroling = false

    var scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScroling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            var isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isLastItem = firstVisibleItemPosition + visibleCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Commons.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScroling
            if (shouldPaginate) {
                viewModel.getSafeSearchNews(binding.searchView.text.toString())
                isScroling = false
            }
        }
    }

    private val searchNewsObserver = Observer<ResultData<ResponseNews>> { result ->
        when (result) {
            is ResultData.Success -> {
                newsAdapter.asyncList.submitList(result.data?.articles)
                var totalResults = 0
                result.data?.let {
                    totalResults = it.totalResults
                }
                val totalPage = totalResults / Commons.QUERY_PAGE_SIZE + 2
                isLastPage = viewModel.searchNewspage == totalPage
                if (isLastPage){
                    binding.recyclerView.setPadding(0,0,0,0)
                }
                //Toast.makeText(context, "data = ${result.data}",Toast.LENGTH_SHORT).show()
            }
            is ResultData.Error -> {
                Toast.makeText(context, "error = ${result.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}