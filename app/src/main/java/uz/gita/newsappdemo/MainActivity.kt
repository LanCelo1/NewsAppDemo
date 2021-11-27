package uz.gita.newsappdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import uz.gita.newsappdemo.databinding.ActivityMainBinding
import uz.gita.newsappdemo.R.id.host_fragment_container
import uz.gita.newsappdemo.data.repository.NewsRepository
import uz.gita.newsappdemo.ui.presenter.NewsViewModel
import uz.gita.newsappdemo.ui.presenter.ViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    lateinit var viewModel :NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        var binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * Configure viewModel
         * */
        val repository = NewsRepository()
        viewModel = ViewModelProvider(this,ViewModelProviderFactory(repository))[NewsViewModel::class.java]


        /**
         * Configure nav bottomView
         * */

        val navHostFragment =
            supportFragmentManager.findFragmentById(host_fragment_container) as NavHostFragment?

        if (navHostFragment != null) {
            val navController = navHostFragment.navController

            binding.apply {
                bottomNavigationView.setupWithNavController(navController)
            }
        }
    }
}