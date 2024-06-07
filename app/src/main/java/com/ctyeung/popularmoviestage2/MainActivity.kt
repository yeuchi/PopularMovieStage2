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
import com.ctyeung.popularmoviestage2.databinding.ActivityMainBinding
import com.ctyeung.popularmoviestage2.data.utilities.MovieHelper
import com.ctyeung.popularmoviestage2.views.DetailActivity
import com.ctyeung.popularmoviestage2.views.MainViewEvent
import com.ctyeung.popularmoviestage2.views.MainViewModel
import com.ctyeung.popularmoviestage2.views.MovieGridAdapter
import dagger.hilt.android.AndroidEntryPoint

/*
 * Picasso has issue with version 9 API 28
 * https://github.com/square/picasso/issues/2019
 *
 * version 9 onward does requires secure network traffic by default
 * set manifest-clear-text option for debugging.
 *
 * https://stackoverflow.com/questions/51902629/how-to-allow-all-network-connection-types-http-and-https-in-android-9-pie
 */

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MovieGridAdapter.ListItemClickListener {
    private lateinit var mBinding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private var mAdapter: MovieGridAdapter? = null
    private val mToast: Toast? = null
    private var mListener: MovieGridAdapter.ListItemClickListener = this
    private var movies = emptyList<Movie>()

    //    var tvNetworkErrorDisplay: TextView? = null

    private var trailerString: String? = null
    private var reviewString: String? = null
    private var mSortMethod = MovieHelper.SORT_POPULAR
    private var scrollY = 0
    private var selectedMovie: Movie? = null

//    var sharedPrefUtility: SharedPrefUtility? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        mLoadingIndicator = findViewById<View>(R.id.pb_display_progress) as ProgressBar?
//        tvNetworkErrorDisplay = findViewById<View>(R.id.tv_network_error_display) as TextView?
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
        movies = list
        mAdapter = MovieGridAdapter(movies, mListener)
        mBinding.rvMovie.apply {
            setAdapter(mAdapter)
            setHasFixedSize(true)
        }
    }

    private fun onTrailers(str: String?) {
        trailerString = str
        selectedMovie?.let {
            val id = it.id.toString()
            viewModel.requestReviews(it, id)
        }
    }

    private fun onReviews(str: String?) {
        reviewString = str
        launchDetail()
    }

    private fun launchDetail() {
        if (selectedMovie != null && trailerString != null && reviewString != null) {
            val intent = Intent(this, DetailActivity::class.java)
            val mergeString: String = selectedMovie!!.toJson() + "_sep_" +
                    trailerString + "_sep_" +
                    reviewString
            intent.putExtra(Intent.EXTRA_TEXT, mergeString)
            startActivity(intent)
        }
    }

    private fun initRecyclerList() {
        val layoutManager = GridLayoutManager(this, numberColumns())
        mBinding.rvMovie.setLayoutManager(layoutManager)
//        sharedPrefUtility = SharedPrefUtility(getApplicationContext())
//        requestMovies(sharedPrefUtility!!.sortMethod)

        // scroll to expected position
//        scrollY = sharedPrefUtility!!.getScrollPos(SharedPrefUtility.MAIN_SCROLL)
//        mBinding.rvMovie.smoothScrollToPosition(scrollY)

        // set scroll event listener
        mBinding.rvMovie.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                scrollY += dy
//                sharedPrefUtility!!.setScroll(SharedPrefUtility.MAIN_SCROLL, scrollY)
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

//        switch (itemId) {
//            case R.id.sort_popular:
//                mSortMethod = MovieHelper.SORT_POPULAR;
//                break;
//
//            case R.id.sort_top_rated:
//                mSortMethod = MovieHelper.SORT_TOP_RATED;
//                break;
//
//            case R.id.sort_favorite:
//                mSortMethod = MovieHelper.SORT_FAVORITE;
//                break;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
        mSortMethod = MovieHelper.SORT_POPULAR
        scrollY = 0
//        sharedPrefUtility!!.setScroll(SharedPrefUtility.MAIN_SCROLL, scrollY)
//        sharedPrefUtility!!.setSortMethod(mSortMethod)
//        requestMovies(mSortMethod)
        return true
    }

    override fun onListItemClick(clickItemIndex: Int) {
        mToast?.cancel()
        // launch detail activity
        selectedMovie = movies[clickItemIndex]
        selectedMovie?.let {
            val id = it.id.toString()
            viewModel.requestTrailers(it, id)
        }
    }
}
