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
package com.ctyeung.popularmoviestage2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;
import com.ctyeung.popularmoviestage2.utilities.JSONhelper;
import com.ctyeung.popularmoviestage2.utilities.MovieHelper;

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.NumberViewHolder> {

    private static final String TAG = MovieGridAdapter.class.getSimpleName();
    final private ListItemClickListener mOnClickListener;


    private static int viewHolderCount;
    private int mNumberItems;
    private JSONArray mJsonArray;

    public interface ListItemClickListener
    {
        void onListItemClick(int clickItemIndex);
    }

    public MovieGridAdapter(int numberOfItems, ListItemClickListener listener, JSONArray jsonArray) {
        mJsonArray = jsonArray;
        mNumberItems = numberOfItems;
        mOnClickListener = listener;
        viewHolderCount = 0;
    }

    @Override
    public NumberViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recyclerview_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        NumberViewHolder viewHolder = new NumberViewHolder(view);

        JSONObject json = JSONhelper.parseJsonFromArray(mJsonArray, viewHolderCount);
        String title = JSONhelper.parseValueByKey(json, MovieHelper.KEY_TITLE);

        viewHolder.viewHolderName.setText(title);

        Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: " + viewHolderCount);

        String url = MovieHelper.BASE_POSTER_URL +
                MovieHelper.getSizeByIndex(MovieHelper.INDEX_THUMBNAIL) +
                JSONhelper.parseValueByKey(json, MovieHelper.KEY_POSTER_PATH);
        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.placeholder)   // optional
                .error(R.drawable.placeholder)      // optional
                .into(viewHolder.viewHolderImage, new Callback() {
                @Override
                public void onSuccess() {
                    //Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError() {
                    //Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
                }
        });

        viewHolderCount++;

        return viewHolder;
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(NumberViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }


    @Override
    public int getItemCount() {
        return mNumberItems;
    }

    /**
     * Cache of the children views for a list item.
     */
    class NumberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView viewHolderName;
        ImageView viewHolderImage;

        public NumberViewHolder(View itemView) {
            super(itemView);

            viewHolderName = (TextView) itemView.findViewById(R.id.txt_movie);
            viewHolderImage = (ImageView) itemView.findViewById(R.id.iv_movie);
            itemView.setOnClickListener(this);
        }

        /**
         * A method we wrote for convenience. This method will take an integer as input and
         * use that integer to display the appropriate text within a list item.
         * @param listIndex Position of the item in the list
         */
        void bind(int listIndex)
        {

            //viewHolderName.setText(String.valueOf(listIndex));
        }

        @Override
        public void onClick(View view)
        {
            int clickPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickPosition);
        }

    }
}
