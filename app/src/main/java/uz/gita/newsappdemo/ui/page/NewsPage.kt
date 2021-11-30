package uz.gita.newsappdemo.ui.page

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import uz.gita.newsappdemo.MainActivity
import uz.gita.newsappdemo.R
import uz.gita.newsappdemo.data.model.Article
import uz.gita.newsappdemo.data.model.ResponseNews
import uz.gita.newsappdemo.databinding.PageNewsBinding
import uz.gita.newsappdemo.ui.adapter.NewAdapter2
import uz.gita.newsappdemo.ui.adapter.NewsAdapter
import uz.gita.newsappdemo.ui.presenter.NewsPageViewModel
import uz.gita.newsappdemo.ui.presenter.NewsViewModel
import uz.gita.newsappdemo.ui.presenter.ResultData
import uz.gita.newsappdemo.utils.Commons.QUERY_PAGE_SIZE
import uz.gita.newsappdemo.utils.WifiBroadCast

@AndroidEntryPoint
class NewsPage : Fragment(R.layout.page_news) {

   // private lateinit var viewModel: NewsViewModel
    private lateinit var binding: PageNewsBinding
    private val newsAdapter: NewAdapter2 by lazy { NewAdapter2() }
//    private val newsAdapter: NewsAdapter by lazy { NewsAdapter() }
    private val myWifiChanged : WifiBroadCast by lazy { WifiBroadCast() }
    private val viewModel : NewsPageViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = PageNewsBinding.bind(view)

        binding.apply {
            recyclerView.apply {
                adapter = newsAdapter.apply {
                    this.onItemClickListener {
                        findNavController().navigate(NewsPageDirections.actionNewsPageToArticlePage(it))
                    }
                }
                layoutManager = LinearLayoutManager(this@NewsPage.context)
                addOnScrollListener(scrollListener)
            }
        }
      /*  try {
            activity?.registerReceiver(
                myWifiChanged,
                IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)
            )
        } catch (e: Exception) {
            showMessage(e.message ?: "Xatolik WiFi")
        }*/

       // checkInternetConnection()

        viewModel.getNewsLiveData.observe(this, getNewsObserver)
    }

    var isLoading = false
    var isLastPage = false
    var isScroling = false

    var scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
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
            val isNotAtBeginning = firstVisibleItemPosition >=0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScroling
            if (shouldPaginate){
                viewModel.getSafeNews("us")
                isScroling = false
            }
        }
    }


    fun checkInternetConnection() {
        myWifiChanged.setOnChangeListener {
            if (it){
                viewModel.getSafeNews("us")
                binding.imgNoConnection.visibility = View.GONE
            } else {
                binding.imgNoConnection.visibility = View.VISIBLE
            }
        }

    }


    fun showMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private val getNewsObserver = Observer<ResultData<ResponseNews>> { result ->
        when (result) {
            is ResultData.Success -> {
                hideProgressBar()
                result.data?.let {
                    newsAdapter.submitList(it.articles.toList())
                    val totalPage = it.totalResults / QUERY_PAGE_SIZE + 2
                    isLastPage = viewModel.newspage == totalPage
                    if (isLastPage){
                        binding.recyclerView.setPadding(0,0,0,0)
                    }
                }
                //Toast.makeText(context, "data = ${result.data}",Toast.LENGTH_SHORT).show()
            }
            is ResultData.Error -> {
                hideProgressBar()
                Toast.makeText(context, "error = ${result.message}", Toast.LENGTH_SHORT).show()
            }
            is ResultData.Loading ->{
                showProgressBar()
            }
        }
    }
    fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        isLoading = true
    }

    fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        isLoading = false
    }
}