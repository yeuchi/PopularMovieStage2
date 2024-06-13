package com.ctyeung.popularmoviestage2.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.ctyeung.popularmoviestage2.MainActivity
import com.ctyeung.popularmoviestage2.R
import com.ctyeung.popularmoviestage2.data.utilities.JSONhelper
import com.ctyeung.popularmoviestage2.data.utilities.MovieHelper
import com.ctyeung.popularmoviestage2.databinding.ActivityDetailBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

/*
 * TODO
 *  a. progress UI
 *  b. error handling (db, network, etc)
 *  c. ScrollY ?
 *  d. Trailer/Review data classes ?
 */
@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels()
    private var _toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val reviewManager = GridLayoutManager(this, 1)
        binding.rvReviews.setLayoutManager(reviewManager)

        val trailerManager = GridLayoutManager(this, 1)
        binding.rvTrailers.setLayoutManager(trailerManager)

        if (parseJSONContent()) {
            initializeElements()
        } else {
            /* back press */
        }
    }

    override fun onResume() {
        super.onResume()
        listen4Events()
        viewModel.requestTrailers()
    }

    private fun listen4Events() {
        viewModel.event.asLiveData().observeForever {
            when (it) {
                is DetailViewEvent.Trailers -> onTrailer()
                is DetailViewEvent.Reviews -> onReview()
                else -> {}
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId) {
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
            }

            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun onTrailer() {
        initializeTrailer()
        viewModel.requestReviews()
    }

    private fun onReview() {
        initializeReview()
    }

    private fun parseJSONContent(): Boolean {
        this.intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            return viewModel.parseJson(it)
        }
        return false
    }

    private fun initializeTrailer() {
        var size = 0
        viewModel.apply {
            mTrailerJsonArray?.let {
                size = it.length()
                val mTrailerAdapter = ListAdapter(size, onLaunchTrailer, it, true)
                binding.rvTrailers.apply {
                    setAdapter(mTrailerAdapter)
                    layoutParams.height = size * 180
                    setHasFixedSize(true)
                }
                binding.tvTrailerHeader.text =
                    size.toString() + " " + getString(R.string.trailer_header)
            }
        }
    }

    private fun initializeReview() {
        var size = 0
        viewModel.apply {
            mReviewJsonArray?.let {
                size = mReviewJsonArray!!.length()
                val mReviewAdapter = ListAdapter(size, onLaunchTrailer, it, false)

                binding.rvReviews.apply {
                    setAdapter(mReviewAdapter)
                    setHasFixedSize(true)
                }
                binding.tvReviewHeader.text =
                    size.toString() + " " + getString(R.string.review_header)
            }
        }
    }

    private fun initializeElements() {
        viewModel.apply {
            binding.let {
                it.tvRating.text = getString(R.string.vote_average) + voteAverage
                it.tvReleaseDate.text = getString(R.string.date) + releaseDate
                it.tvPlot.text = plot
                it.toolbar.title = getString(R.string.title) + title

                // label button pending on query result
                it.btnFavorite.setOnClickListener {
                    setBtnFavoriteText(!isFavorite)
                    viewModel.selectFavorite()
                }

                setBtnFavoriteText(isFavorite)
                val url = movie?.posterDetailPath()
                Picasso.get() //.load("http://i.imgur.com/DvpvklR.png") example
                    .load(url)
                    .placeholder(R.drawable.placeholder) // optional
                    .error(R.drawable.placeholder) // optional
                    .into(it.ivPosterImage)
            }
        }
    }

    private fun setBtnFavoriteText(isFavorite: Boolean) {
        val stringIndex =
            if (isFavorite) R.string.remove_favorite else R.string.mark_as_favorite
        binding.btnFavorite.text = getString(stringIndex)
    }

    /**
     * Description: trailer
     */
    private val onLaunchTrailer: (
        clickItemIndex: Int,
        isVideo: Boolean
    ) -> Unit = { index, isVideo ->
        if (_toast != null) _toast!!.cancel()

        viewModel.apply {
            val jsonArray = if (isVideo) mTrailerJsonArray else mReviewJsonArray
            jsonArray?.let {
                val json = JSONhelper.parseJsonFromArray(jsonArray, index)
                json?.let {
                    // launch web
                    val url =
                        if (isVideo) MovieHelper.BASE_YOUTUBE_URL + JSONhelper.parseValueByKey(
                            json,
                            MovieHelper.KEY_TRAILER
                        ) else JSONhelper.parseValueByKey(json, MovieHelper.KEY_REVIEW_URL)
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse(url))
                    startActivity(intent)
                }
            }
            // toast
//            val toastmessage =
//                getString(R.string.item_no) + " " + index + " " + getString(R.string.clicked)
//            _toast = Toast.makeText(this, toastmessage, Toast.LENGTH_LONG)
//            _toast?.show()
        }
    }
}
