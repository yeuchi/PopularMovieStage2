package com.ctyeung.popularmoviestage2.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.ctyeung.popularmoviestage2.R
import com.ctyeung.popularmoviestage2.data.utilities.JSONhelper
import com.ctyeung.popularmoviestage2.data.utilities.MovieHelper
import com.ctyeung.popularmoviestage2.databinding.ActivityDetailBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels()

    private var _toast: Toast? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

        val reviewManager = GridLayoutManager(this, 1)

        binding.rvReviews.setLayoutManager(reviewManager)
        val trailerManager = GridLayoutManager(this, 1)
        binding.rvTrailers.setLayoutManager(trailerManager)
        if(parseJSONContent()) {
            initializeElements()
            setScrollPosition()
        }
        else {
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

    private fun onTrailer() {
        initializeTrailer()
        viewModel.requestReviews()
    }

    private fun onReview() {
        initializeReview()
    }

    private fun setScrollPosition() {
//        scrollView.post(new Runnable()
//        {
//            public void run()
//            {
//                int pos = sharedPrefUtility.getScrollPos(sharedPrefUtility.DETAIL_SCROLL);
//                int pos = 0;
//                if(pos > 0)
//                {
//                    scrollView.smoothScrollTo(0,pos);
//                }
//                else
//                {
//                    //default at top ... unless we have a previous position
//                    scrollView.fullScroll(View.FOCUS_UP);
//                }
//            }
//        });
    }

    private fun parseJSONContent():Boolean {
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
                val mTrailerAdapter = ListAdapter(size, onListItemClick, it, true)
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
                val mReviewAdapter = ListAdapter(size, onListItemClick, it, false)

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
            binding.tvRating.text = getString(R.string.vote_average) + voteAverage
            binding.tvReleaseDate.text = getString(R.string.date) + releaseDate
            binding.tvPlot.text = plot
            binding.tvOriginalTitle.text = getString(R.string.title) + title

            // label button pending on query result
            binding.btnFavorite.setOnClickListener(buttonListener)
            setBtnFavoriteText()
            val url = movie?.posterDetailPath()
            Picasso.get() //.load("http://i.imgur.com/DvpvklR.png") example
                .load(url)
                .placeholder(R.drawable.placeholder) // optional
                .error(R.drawable.placeholder) // optional
                .into(binding.ivPosterImage)
        }
    }

    private fun setBtnFavoriteText() {
        val stringIndex =
            if (viewModel.isFavorite) R.string.remove_favorite else R.string.mark_as_favorite
        binding.btnFavorite.text = getString(stringIndex)
    }

    private val buttonListener = View.OnClickListener {
        viewModel.selectFavorite()
        setBtnFavoriteText();
//            if(uri != null)
//                Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
    }

    private val onListItemClick: (
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

    override fun onDestroy() {
//        int pos = scrollView.getScrollY();
//        sharedPrefUtility.setScroll(sharedPrefUtility.DETAIL_SCROLL, pos);
        super.onDestroy()
    }
}
