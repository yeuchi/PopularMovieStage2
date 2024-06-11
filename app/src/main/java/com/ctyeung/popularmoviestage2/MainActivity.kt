package com.ctyeung.popularmoviestage2

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
 *  d. Trailer/Review data classes ?
 *  e. msg when no favorites has been selected - blank list
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var mAdapter: MovieGridAdapter? = null

    private var trailerString: String? = null
    private var reviewString: String? = null
    private var mSortMethod = MovieHelper.SORT_POPULAR
    private var scrollY = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
    }

    override fun onResume() {
        super.onResume()
        initRecyclerList()
        listen4Events()
        viewModel.request(MovieHelper.SORT_POPULAR)
    }

    private fun listen4Events() {
        viewModel.event.asLiveData().observeForever {
            when (it) {
                is MainViewEvent.Favorites -> onMovies(it.list)
                is MainViewEvent.Movies -> onMovies(it.list)
                is MainViewEvent.Trailers -> onTrailers(it.str)
                is MainViewEvent.Reviews -> onReviews(it.str)
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

    private fun onTrailers(str: String?) {
        trailerString = str
        viewModel.selectedMovie?.let {
            val id = it.id.toString()
            viewModel.requestReviews(it, id)
        }
    }

    private fun onReviews(str: String?) {
        reviewString = str
        launchDetail()
    }

    private fun launchDetail() {
        if (viewModel.selectedMovie != null && trailerString != null && reviewString != null) {
            val intent = Intent(this, DetailActivity::class.java)
            val mergeString: String = viewModel.selectedMovie!!.toJson() + "_sep_" +
                    trailerString + "_sep_" +
                    reviewString
            intent.putExtra(Intent.EXTRA_TEXT, mergeString)
            startActivity(intent)
        }
    }

    private fun initRecyclerList() {
        val layoutManager = GridLayoutManager(this, numberColumns())
        binding.rvMovie.setLayoutManager(layoutManager)

        // set scroll event listener
        binding.rvMovie.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                scrollY += dy
            }
        })
    }

    private fun numberColumns(): Int {
        val orientation: Int = getResources().getConfiguration().orientation
        return when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> 3
            Configuration.ORIENTATION_PORTRAIT -> 2
            else -> 2
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = getMenuInflater()
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        mSortMethod = when (itemId) {
            R.id.sort_popular -> MovieHelper.SORT_POPULAR
            R.id.sort_top_rated -> MovieHelper.SORT_TOP_RATED
            R.id.sort_favorite -> MovieHelper.SORT_FAVORITE

            /* no such thing */
            else ->
                return super.onOptionsItemSelected(item)
        }

        /*
         * TODO remove when sort methdos implemented
         */
        mSortMethod = MovieHelper.SORT_POPULAR
        scrollY = 0
        viewModel.request(mSortMethod)
        return true
    }

    private val onListItemClick: (clickItemIndex: Int) -> Unit = { i ->
        viewModel.apply {
            selectedMovie = if (mSortMethod == MovieHelper.SORT_FAVORITE) {
                favorites[i]
            } else {
                movies[i]
            }

            selectedMovie?.let {
                val id = it.id.toString()
                requestTrailers(it, id)
            }
        }
    }
}
