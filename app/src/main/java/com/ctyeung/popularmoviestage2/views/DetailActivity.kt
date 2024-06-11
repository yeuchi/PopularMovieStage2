package com.ctyeung.popularmoviestage2.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.ctyeung.popularmoviestage2.R
import com.ctyeung.popularmoviestage2.data.room.Movie
import com.ctyeung.popularmoviestage2.data.utilities.JSONhelper
import com.ctyeung.popularmoviestage2.data.utilities.MovieHelper
import com.ctyeung.popularmoviestage2.databinding.ActivityDetailBinding
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject

/*
* TODO use data-binding
*/
@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels()

    private var _toast: Toast? = null
    private var mTrailerJsonArray: JSONArray? = null
    private var mReviewJsonArray: JSONArray? = null


    //    private SharedPrefUtility sharedPrefUtility;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

//        sharedPrefUtility = new SharedPrefUtility(getApplicationContext());
        val reviewManager = GridLayoutManager(this, 1)

        binding.rvReviews.setLayoutManager(reviewManager)
        val trailerManager = GridLayoutManager(this, 1)
        binding.rvTrailers.setLayoutManager(trailerManager)
        parseJSONContent()
        initializeElements()
        initializeAdvocateList()
        setScrollPosition()
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

    private fun parseJSONContent() {
        val str = this.intent.getStringExtra(Intent.EXTRA_TEXT)
        val list = str!!.split("_sep_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        viewModel.parseJson(list[0])
        mTrailerJsonArray = JSONhelper.getJsonArray(list[1], "results")
        mReviewJsonArray = JSONhelper.getJsonArray(list[2], "results")
    }

    private fun initializeAdvocateList() {
        var size = 0
        mTrailerJsonArray?.let {
            size = mTrailerJsonArray!!.length()
            val mTrailerAdapter = ListAdapter(size, onListItemClick, it, true)
            binding.rvTrailers.apply {
                setAdapter(mTrailerAdapter)
                layoutParams.height = size * 180
                setHasFixedSize(true)
            }
            binding.tvTrailerHeader.text =
                size.toString() + " " + getString(R.string.trailer_header)
        }

        mReviewJsonArray?.let {
            size = mReviewJsonArray!!.length()
            val mReviewAdapter = ListAdapter(size, onListItemClick, it, false)

            binding.rvReviews.apply {
                setAdapter(mReviewAdapter)
                setHasFixedSize(true)
            }
            binding.tvReviewHeader.text = size.toString() + " " + getString(R.string.review_header)
        }
    }

    private fun initializeElements() {

        viewModel.apply {
            val voteAverage = movie?.voteAverage
            binding.tvRating.text = getString(R.string.vote_average) + voteAverage
            val date = movie?.releaseDate
            binding.tvReleaseDate.text = getString(R.string.date) + date
            val plot = movie?.overview
            binding.tvPlot.text = plot
            title = movie?.originalTitle
            binding.tvOriginalTitle.text = getString(R.string.title) + title

            // query db -- isFavorite if exists
            val columns = arrayOf("title")
            val args = arrayOf(title)
            //        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
//                columns,
//                "title=?",
//                args,
//                "title DESC");

            // label button pending on query result
            binding.btnFavorite.setOnClickListener(buttonListener)
            setBtnFavoriteText()
            val context = applicationContext
            val url = movie?.posterDetailPath()
            Picasso.get() //.load("http://i.imgur.com/DvpvklR.png") example
                .load(url)
                .placeholder(R.drawable.placeholder) // optional
                .error(R.drawable.placeholder) // optional
                .into(binding.ivPosterImage)
        }
    }

    private fun setBtnFavoriteText() {
        val stringIndex = if (viewModel.isFavorite) R.string.remove_favorite else R.string.mark_as_favorite
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

        val jsonArray = if (isVideo) mTrailerJsonArray else mReviewJsonArray
        jsonArray?.let {
            val json = JSONhelper.parseJsonFromArray(jsonArray, index)
            json?.let {
                // launch web
                val url = if (isVideo) MovieHelper.BASE_YOUTUBE_URL + JSONhelper.parseValueByKey(
                    json,
                    MovieHelper.KEY_TRAILER
                ) else JSONhelper.parseValueByKey(json, MovieHelper.KEY_REVIEW_URL)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(Uri.parse(url))
                startActivity(intent)
            }
        }
        // toast
        val toastmessage =
            getString(R.string.item_no) + " " + index + " " + getString(R.string.clicked)
        _toast = Toast.makeText(this, toastmessage, Toast.LENGTH_LONG)
        _toast?.show()
    }

    override fun onDestroy() {
//        int pos = scrollView.getScrollY();
//        sharedPrefUtility.setScroll(sharedPrefUtility.DETAIL_SCROLL, pos);
        super.onDestroy()
    }
}
