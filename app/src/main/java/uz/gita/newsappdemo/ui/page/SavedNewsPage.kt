package uz.gita.newsappdemo.ui.page

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchUIUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import uz.gita.newsappdemo.MainActivity
import uz.gita.newsappdemo.R
import uz.gita.newsappdemo.data.model.Article
import uz.gita.newsappdemo.databinding.PageNewsBinding
import uz.gita.newsappdemo.databinding.PageSavedNewsBinding
import uz.gita.newsappdemo.ui.adapter.NewsAdapter
import uz.gita.newsappdemo.ui.presenter.NewsViewModel

class SavedNewsPage : Fragment(R.layout.page_saved_news) {
    private lateinit var viewModel: NewsViewModel
    private lateinit var binding: PageSavedNewsBinding
    private val newsAdapter: NewsAdapter by lazy { NewsAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = PageSavedNewsBinding.bind(view)
        viewModel = (activity as MainActivity).viewModel

        binding.apply {
            recyclerView.apply {
                adapter = newsAdapter.apply {
                    this.onItemClickListener {
                        val bundle = Bundle().apply {
                            putSerializable("article", it)
                        }
                        findNavController().navigate(SavedNewsPageDirections.actionSavedNewsPageToArticlePage(it))
                    }
                }
                layoutManager = LinearLayoutManager(this@SavedNewsPage.context)
            }
        }
        viewModel.saveNewsLiveData.observe(viewLifecycleOwner,saveNewsObserver)
        viewModel.deleteNewLiveData.observe(viewLifecycleOwner,deleteNewsObserver)

        val itemTouchHelperCallback = object  : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var position = viewHolder.adapterPosition
                var article = newsAdapter.asyncList.currentList[position]
                viewModel.delete(article)
                Snackbar.make(view,"Successfully deleted article", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewModel.insertNews(article)
                    }
                    show()
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.recyclerView)
        }
    }

    private var saveNewsObserver = Observer<List<Article>>{
        newsAdapter.asyncList.submitList(it)
    }
    private var deleteNewsObserver = Observer<Unit>{

    }
}