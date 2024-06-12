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

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ctyeung.popularmoviestage2.R
import com.ctyeung.popularmoviestage2.data.room.Movie
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

/*
 * TODO migrate to Kotlin
 */
class MovieGridAdapter(
    private val _movies: List<Movie>,
    private val itemClick: (movie:Movie)->Unit,
) : RecyclerView.Adapter<MovieGridAdapter.NumberViewHolder>() {
    private var _viewHolderCount = 0
    private var _context: Context? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): NumberViewHolder {
        _context = viewGroup.context
        val layoutIdForListItem = R.layout.recyclerview_item
        val inflater = LayoutInflater.from(_context)
        val shouldAttachToParentImmediately = false
        val view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately)
        val viewHolder: NumberViewHolder = NumberViewHolder(view)
        _viewHolderCount++
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
        return _movies.size
    }

    /**
     * Cache of the children views for a list item.
     */
    inner class NumberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var viewHolderName: TextView
        var viewHolderImage: ImageView
        var movie:Movie?=null
        init {
            viewHolderName = itemView.findViewById<View>(R.id.txt_movie) as TextView
            viewHolderImage = itemView.findViewById<View>(R.id.iv_movie) as ImageView
            itemView.setOnClickListener(this)
        }

        /**
         * A method we wrote for convenience. This method will take an integer as input and
         * use that integer to display the appropriate text within a list item.
         *
         * @param position Position of the item in the list
         */
        fun bind(position: Int) {
            movie = _movies[position]
            movie?.let {
                viewHolderName.text = it.title
                Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: $_viewHolderCount")

                // http://image.tmdb.org/t/p/w780//udDclJoHjfjb8Ekgsd4FDteOkCU.jpg  example
                Picasso.get()
                    .load(it.posterThumbPath()) //.placeholder(R.drawable.placeholder)   // optional
                    //.error(R.drawable.placeholder)      // optional
                    .into(viewHolderImage, object : Callback {
                        override fun onSuccess() {}
                        override fun onError(e: Exception) {}
                    })
            }
        }

        override fun onClick(view: View) {
//            val clickPosition = getAdapterPosition()
            movie?.let{
                itemClick(it)
            }
        }
    }

    companion object {
        private val TAG = MovieGridAdapter::class.java.getSimpleName()
    }
}
