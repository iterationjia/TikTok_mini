package com.example.tiktok_mini;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;



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
        public ImageView play;

        public MyViewHolder(View v) {
            super(v);
            info_brief = v.findViewById(R.id.info_brief);
            cover = v.findViewById(R.id.cover);
            play = v.findViewById(R.id.play);
        }
    }


    public void setData(List<VideoResponse> myDataSet,Context context) {
        this.myDataSet = myDataSet;
        this.context = context;

        //趁渐入渐出的几秒，预加载所有封面（只适用于目前数据量较小的情况）
//        for(int i=0;i<myDataSet.size();i++)
//            Glide.with(context).load(Uri.parse(myDataSet.get(i).feedurl)).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        v.setLayoutParams(lp);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.info_brief.setText(String.format(context.getResources().getString(R.string.brief_info),
                myDataSet.get(position).description, myDataSet.get(position).nickname));
        Glide.with(context)
                .load(Uri.parse(myDataSet.get(position).feedurl))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.cover);

        View.OnClickListener onClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,PlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("feedurl",myDataSet.get(position).feedurl);
                bundle.putString("avatarUrl", myDataSet.get(position).avatar);
                bundle.putInt("likeNum", myDataSet.get(position).likecount);
                intent.putExtra("data",bundle);
                context.startActivity(intent);
            }
        };
        holder.play.setOnClickListener(onClickListener);

        holder.cover.setOnClickListener(onClickListener);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myDataSet == null ? 0 : myDataSet.size();
    }

}