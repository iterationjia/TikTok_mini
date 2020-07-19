package com.example.tiktok_mini;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import static android.provider.MediaStore.Video.Thumbnails.MINI_KIND;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<VideoResponse> myDataSet;
    Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView info_brief;
        public ImageView cover;

        public MyViewHolder(View v) {
            super(v);
            info_brief = v.findViewById(R.id.info_brief);
            cover = v.findViewById(R.id.cover);
        }
    }


    public void setData(List<VideoResponse> myDataSet,Context context) {
        this.myDataSet = myDataSet;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.info_brief.setText(String.format(context.getResources().getString(R.string.brief_info),
                myDataSet.get(position).description, myDataSet.get(position).nickname));
        Glide.with(context).load(Uri.parse(myDataSet.get(position).feedurl)).into(holder.cover);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myDataSet == null ? 0 : myDataSet.size();
    }

}