package uz.gita.newsappdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.gita.newsappdemo.databinding.ActivityMainBinding
import uz.gita.newsappdemo.R.id.host_fragment_container

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  //  lateinit var viewModel :NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        var binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * Configure viewModel
         * */
        /*var db = NewDatabase.getInstance(App.instance)
        val repository = NewsRepository(db)
        viewModel = ViewModelProvider(this,ViewModelProviderFactory(repository))[NewsViewModel::class.java]
*/

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