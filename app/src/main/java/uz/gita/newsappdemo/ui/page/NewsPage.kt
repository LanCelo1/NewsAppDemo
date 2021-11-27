package uz.gita.newsappdemo.ui.page

import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import uz.gita.newsappdemo.MainActivity
import uz.gita.newsappdemo.R
import uz.gita.newsappdemo.data.model.Article
import uz.gita.newsappdemo.databinding.PageNewsBinding
import uz.gita.newsappdemo.ui.adapter.NewsAdapter
import uz.gita.newsappdemo.ui.presenter.NewsViewModel
import uz.gita.newsappdemo.ui.presenter.ResultData
import uz.gita.newsappdemo.utils.WifiBroadCast

class NewsPage : Fragment(R.layout.page_news) {

    private lateinit var viewModel: NewsViewModel
    private lateinit var binding: PageNewsBinding
    private val newsAdapter: NewsAdapter by lazy { NewsAdapter() }
    private val myWifiChanged : WifiBroadCast by lazy { WifiBroadCast() }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = (activity as MainActivity).viewModel
        binding = PageNewsBinding.bind(view)



        binding.apply {
            recyclerView.apply {
                adapter = newsAdapter
                layoutManager = LinearLayoutManager(this@NewsPage.context)
            }
        }
        try {
            activity?.registerReceiver(
                myWifiChanged,
                IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)
            )
        } catch (e: Exception) {
            showMessage(e.message ?: "Xatolik WiFi")
        }

        checkInternetConnection()

        viewModel.getCommonNews("us")

        viewModel.getNewsLiveData.observe(this, getNewsObserver)
    }

    fun checkInternetConnection() {
        myWifiChanged.setOnChangeListener {
            if (it){
                viewModel.getCommonNews("us")
                binding.imgNoConnection.visibility = View.GONE
            } else {
                binding.imgNoConnection.visibility = View.VISIBLE
            }
        }

    }


    fun showMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private val getNewsObserver = Observer<ResultData<List<Article>>> { result ->
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