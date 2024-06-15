package com.ctyeung.popularmoviestage2

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ctyeung.popularmoviestage2.data.room.Movie
import com.ctyeung.popularmoviestage2.data.utilities.MovieHelper
import com.ctyeung.popularmoviestage2.databinding.ActivityMainBinding
import com.ctyeung.popularmoviestage2.views.DetailActivity
import com.ctyeung.popularmoviestage2.views.MainViewEvent
import com.ctyeung.popularmoviestage2.views.MainViewModel
import com.ctyeung.popularmoviestage2.views.MovieGridAdapter
import dagger.hilt.android.AndroidEntryPoint

/*
 * TODO
 *  a. progress UI
 *  b. error handling (db, network, etc)
 *  c. ScrollY ?
 *  d. msg when no favorites has been selected - blank list
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var mAdapter: MovieGridAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
    }

    override fun onResume() {
        super.onResume()
        initRecyclerList()
        listen4Events()
        viewModel.apply {
            request()
        }
    }

    private fun listen4Events() {
        viewModel.apply {
            event.asLiveData().observeForever {
                when (it) {
                    is MainViewEvent.Favorites -> {
                        if(it.list.isEmpty()){
                            Toast.makeText(this@MainActivity, "No Favorites available", Toast.LENGTH_LONG).show()
                            request()
                        }
                        else {
                            onMovies(it.list)
                        }
                    }
                    is MainViewEvent.Movies -> onMovies(it.list)
                }
            }
        }
    }

    private fun onMovies(list: List<Movie>) {
        mAdapter = MovieGridAdapter(list, onListItemClick)
        binding.rvMovie.apply {
            setAdapter(mAdapter)
            setHasFixedSize(true)
        }
    }

    private fun initRecyclerList() {
        val layoutManager = GridLayoutManager(this, numberColumns())
        binding.rvMovie.setLayoutManager(layoutManager)

        // set scroll event listener
        binding.rvMovie.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                /* scroll to a specific selection? */
            }
        })
    }

    private fun numberColumns(): Int {
        val orientation: Int = getResources().configuration.orientation
        return when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> 3
            Configuration.ORIENTATION_PORTRAIT -> 2
            else -> 2
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        viewModel.apply {
            sortMethod = when (itemId) {
                R.id.sort_popular -> MovieHelper.SORT_POPULAR
                R.id.sort_top_rated -> MovieHelper.SORT_TOP_RATED
                R.id.sort_favorite -> MovieHelper.SORT_FAVORITE

                /* no such thing */
                else ->
                    return super.onOptionsItemSelected(item)
            }
            request()
        }
        return true
    }

    private val onListItemClick: (movie: Movie) -> Unit = { movie ->
        val intent = Intent(this@MainActivity, DetailActivity::class.java)
        intent.putExtra(Intent.EXTRA_TEXT, movie.toJson())
        startActivity(intent)
    }
}
