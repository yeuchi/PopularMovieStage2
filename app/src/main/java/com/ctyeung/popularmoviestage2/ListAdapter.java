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
import android.widget.Button;
import android.text.Html;
import android.content.res.Resources;

import org.json.JSONArray;
import org.json.JSONObject;
import com.ctyeung.popularmoviestage2.utilities.JSONhelper;
import com.ctyeung.popularmoviestage2.utilities.MovieHelper;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.NumberViewHolder> {

    private static final String TAG = MovieGridAdapter.class.getSimpleName();
    final private ListItemClickListener mClickListener;


    private int mViewHolderCount;
    private int mNumberItems;
    private JSONArray mJsonArray;
    private boolean mIsVideo = false;

    public interface ListItemClickListener
    {
        void onListItemClick(int clickItemIndex, boolean isVideo);
    }

    public ListAdapter(int numberOfItems,
                       ListItemClickListener listener,
                       JSONArray jsonArray,
                       boolean isVideo) {

        mJsonArray = jsonArray;
        mNumberItems = numberOfItems;
        mClickListener = listener;
        mViewHolderCount = 0;
        mIsVideo = isVideo;
    }

    @Override
    public NumberViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recycler_list_item;

        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        NumberViewHolder viewHolder = new NumberViewHolder(view);

        JSONObject json = JSONhelper.parseJsonFromArray(mJsonArray, mViewHolderCount);

        String name = (true==mIsVideo)?
                        JSONhelper.parseValueByKey(json, MovieHelper.KEY_TYPE) + ":" +
                        JSONhelper.parseValueByKey(json, MovieHelper.KEY_NAME) :
                        "Author: " + JSONhelper.parseValueByKey(json, MovieHelper.KEY_AUTHOR);


        Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: " + mViewHolderCount);

        //String url = (true==mIsReview)?
        //                MovieHelper.BASE_YOUTUBE_URL + JSONhelper.parseValueByKey(json, MovieHelper.KEY_TRAILER):
        //                JSONhelper.parseValueByKey(json, MovieHelper.KEY_REVIEW_URL);

        //String text = "<a href='"+ url +"'> "+name+" </a>";

        viewHolder.button.setText(name);

        mViewHolderCount++;
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


        Button button;

        public NumberViewHolder(View itemView) {
            super(itemView);

            button = (Button) itemView.findViewById(R.id.btn_advocate);
            button.setOnClickListener(this);
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
            mClickListener.onListItemClick(clickPosition, mIsVideo);
        }

    }
}
