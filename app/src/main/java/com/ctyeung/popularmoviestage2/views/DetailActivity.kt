package com.ctyeung.popularmoviestage2.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ctyeung.popularmoviestage2.R
import com.ctyeung.popularmoviestage2.data.utilities.JSONhelper
import com.ctyeung.popularmoviestage2.data.utilities.MovieHelper
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject

/*
* TODO use data-binding
*/
@AndroidEntryPoint
class DetailActivity : AppCompatActivity(), ListAdapter.ListItemClickListener {
    private var scrollView: ScrollView? = null
    private var mTrailerAdapter: ListAdapter? = null
    private var mReviewAdapter: ListAdapter? = null
    private var mTrailerList: RecyclerView? = null
    private var mReviewList: RecyclerView? = null
    private var _toast: Toast? = null
    private var mListener: ListAdapter.ListItemClickListener = this
    private var tvTitle: TextView? = null
    private var ivPoster: ImageView? = null
    private var tvPlot: TextView? = null
    private var tvRating: TextView? = null
    private var tvReleaseDate: TextView? = null
    private var mTrailerJsonArray: JSONArray? = null
    private var mReviewJsonArray: JSONArray? = null
    private var id: String? = null
    private var json: JSONObject? = null
    private var title: String? = null
    private val isFavorite = false

    //    private SharedPrefUtility sharedPrefUtility;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

//        sharedPrefUtility = new SharedPrefUtility(getApplicationContext());
        scrollView = findViewById<View>(R.id.sv_scroller) as ScrollView
        tvTitle = findViewById<View>(R.id.tv_original_title) as TextView
        ivPoster = findViewById<View>(R.id.iv_poster_image) as ImageView
        tvPlot = findViewById<View>(R.id.tv_plot) as TextView
        tvRating = findViewById<View>(R.id.tv_rating) as TextView
        tvReleaseDate = findViewById<View>(R.id.tv_release_date) as TextView
        val reviewManager = GridLayoutManager(this, 1)
        mReviewList = findViewById<View>(R.id.rv_reviews) as RecyclerView
        mReviewList!!.setLayoutManager(reviewManager)
        val trailerManager = GridLayoutManager(this, 1)
        mTrailerList = findViewById<View>(R.id.rv_trailers) as RecyclerView
        mTrailerList!!.setLayoutManager(trailerManager)
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
        json = JSONhelper.parseJson(list[0])
        mTrailerJsonArray = JSONhelper.getJsonArray(list[1], "results")
        mReviewJsonArray = JSONhelper.getJsonArray(list[2], "results")
    }

    private fun initializeAdvocateList() {
        var size = 0
        mTrailerJsonArray?.let {
            size = mTrailerJsonArray!!.length()
            mTrailerAdapter = ListAdapter(size, mListener, it, true)
            mTrailerList!!.setAdapter(mTrailerAdapter)
            mTrailerList!!.layoutParams.height = size * 180
            mTrailerList!!.setHasFixedSize(true)
            val header = findViewById<View>(R.id.tv_trailer_header) as TextView
            header.text = size.toString() + " " + getString(R.string.trailer_header)
        }
        mReviewJsonArray?.let {
            size = mReviewJsonArray!!.length()
            mReviewAdapter = ListAdapter(size, mListener, it, false)
            mReviewList!!.setAdapter(mReviewAdapter)
            mReviewList!!.setHasFixedSize(true)
            val header = findViewById<View>(R.id.tv_review_header) as TextView
            header.text = size.toString() + " " + getString(R.string.review_header)
        }
    }

    private fun initializeElements() {
        json?.let {
            id = JSONhelper.parseValueByKey(it, MovieHelper.KEY_ID)
            val voteAverage = JSONhelper.parseValueByKey(it, MovieHelper.KEY_VOTE_AVERAGE)
            tvRating!!.text = getString(R.string.vote_average) + voteAverage
            val date = JSONhelper.parseValueByKey(it, MovieHelper.KEY_RELEASE_DATE)
            tvReleaseDate!!.text = getString(R.string.date) + date
            val plot = JSONhelper.parseValueByKey(it, MovieHelper.KEY_PLOT)
            tvPlot!!.text = plot
            title = JSONhelper.parseValueByKey(it, MovieHelper.KEY_ORIGINAL_TITLE)
            tvTitle!!.text = getString(R.string.title) + title

            // query db -- isFavorite if exists
            val columns = arrayOf("title")
            val args = arrayOf(title)
            //        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
//                columns,
//                "title=?",
//                args,
//                "title DESC");

//        this.isFavorite = (0==cursor.getCount())? false : true;

            // label button pending on query result
            val button = findViewById<View>(R.id.btnFavorite) as TextView
            button.setOnClickListener(buttonListener)
            setBtnFavoriteText()
            val context = applicationContext
            val url = MovieHelper.BASE_POSTER_URL +
                    MovieHelper.getSizeByIndex(MovieHelper.INDEX_DETAIL) +
                    JSONhelper.parseValueByKey(it, MovieHelper.KEY_POSTER_PATH)
            Picasso.get() //.load("http://i.imgur.com/DvpvklR.png") example
                .load(url)
                .placeholder(R.drawable.placeholder) // optional
                .error(R.drawable.placeholder) // optional
                .into(ivPoster)
        }
    }

    private fun setBtnFavoriteText() {
        val button = findViewById<View>(R.id.btnFavorite) as Button
        val stringIndex = if (isFavorite) R.string.remove_favorite else R.string.mark_as_favorite
        button.text = getString(stringIndex)
    }

    private val buttonListener = View.OnClickListener {
        // Perform action on click -- favorite movie selected !
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
//            contentValues.put(MovieContract.MovieEntry.COLUMN_JSON_DETAIL, json.toString());
//
//            Uri uri = null;
//
//            if(isFavorite)
//            {
//                // delete
//                getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, title, null);
//            }
//            else
//            {
//                // Insert the content values via a ContentResolver
//                uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
//            }
//
//            isFavorite = !isFavorite;
//            setBtnFavoriteText();
//
//            if(uri != null)
//                Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
    }

    override fun onListItemClick(
        clickItemIndex: Int,
        isVideo: Boolean
    ) {
        if (_toast != null) _toast!!.cancel()

        // launch detail activity
        val jsonArray = if (isVideo) mTrailerJsonArray else mReviewJsonArray
        jsonArray?.let {
            val json = JSONhelper.parseJsonFromArray(jsonArray, clickItemIndex)
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
            getString(R.string.item_no) + " " + clickItemIndex + " " + getString(R.string.clicked)
        _toast = Toast.makeText(this, toastmessage, Toast.LENGTH_LONG)
        _toast?.show()
    }

    override fun onDestroy() {
//        int pos = scrollView.getScrollY();
//        sharedPrefUtility.setScroll(sharedPrefUtility.DETAIL_SCROLL, pos);
        super.onDestroy()
    }
}
