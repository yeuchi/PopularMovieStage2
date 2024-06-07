/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ctyeung.popularmoviestage2.views

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ctyeung.popularmoviestage2.R
import com.ctyeung.popularmoviestage2.data.utilities.JSONhelper
import com.ctyeung.popularmoviestage2.data.utilities.MovieHelper
import com.ctyeung.popularmoviestage2.views.MovieGridAdapter
import org.json.JSONArray

/*
 * TODO migrate to Kotlin
 */
class ListAdapter(
    private val mNumberItems: Int,
    private val mClickListener: ListItemClickListener,
    private val mJsonArray: JSONArray,
    isVideo: Boolean
) : RecyclerView.Adapter<ListAdapter.NumberViewHolder>() {
    private var mViewHolderCount = 0
    private var mIsVideo = false

    interface ListItemClickListener {
        fun onListItemClick(clickItemIndex: Int, isVideo: Boolean)
    }

    init {
        mIsVideo = isVideo
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): NumberViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.recycler_list_item
        val inflater = LayoutInflater.from(context)
        val shouldAttachToParentImmediately = false
        val view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately)
        val viewHolder: NumberViewHolder = NumberViewHolder(view)
        val json = JSONhelper.parseJsonFromArray(mJsonArray, mViewHolderCount)
        json?.let {
            val name =
                if (true == mIsVideo) JSONhelper.parseValueByKey(json, MovieHelper.KEY_TYPE) + ":" +
                        JSONhelper.parseValueByKey(
                            json,
                            MovieHelper.KEY_NAME
                        ) else "Author: " + JSONhelper.parseValueByKey(json, MovieHelper.KEY_AUTHOR)
            Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: $mViewHolderCount")

            //String url = (true==mIsReview)?
            //                MovieHelper.BASE_YOUTUBE_URL + JSONhelper.parseValueByKey(json, MovieHelper.KEY_TRAILER):
            //                JSONhelper.parseValueByKey(json, MovieHelper.KEY_REVIEW_URL);

            //String text = "<a href='"+ url +"'> "+name+" </a>";
            viewHolder.button.text = name
            mViewHolderCount++
        }
        return viewHolder
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        Log.d(TAG, "#$position")
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return mNumberItems
    }

    /**
     * Cache of the children views for a list item.
     */
    inner class NumberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var button: TextView

        init {
            button = itemView.findViewById<View>(R.id.btn_advocate) as TextView
            button.setOnClickListener(this)
        }

        /**
         * A method we wrote for convenience. This method will take an integer as input and
         * use that integer to display the appropriate text within a list item.
         * @param listIndex Position of the item in the list
         */
        fun bind(listIndex: Int) {

            //viewHolderName.setText(String.valueOf(listIndex));
        }

        override fun onClick(view: View) {
            val clickPosition = getAdapterPosition()
            mClickListener.onListItemClick(clickPosition, mIsVideo)
        }
    }

    companion object {
        private val TAG = MovieGridAdapter::class.java.getSimpleName()
    }
}
