package com.vp6.anish.madguysmarketers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anish on 16-10-2016.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

    private List<String> images;
    private Context mContext;
    SparseBooleanArray mSparseBooleanArray;
    int pics;
    int count =0;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public SquareLayout gallerylayout;
        public MyViewHolder(View view) {
            super(view);
            gallerylayout= (SquareLayout) view.findViewById(R.id.gallery_layout);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);

        }
    }

    public GalleryAdapter(Context context, List<String>imageurl,int numberofpics){
        mContext = context;
        this.images = imageurl;
        this.mSparseBooleanArray = new SparseBooleanArray();
        this.pics = numberofpics;

    }


//    public GalleryAdapter(Context context, List<String> imageurl) {
//        mContext = context;
//        this.images = imageurl;
//        this.mSparseBooleanArray = new SparseBooleanArray();
//        this.pics = 0;
//    }


    public ArrayList<String> getCheckedItems() {
        ArrayList<String> mTempArry = new ArrayList<String>();

        for(int i=0;i<images.size();i++) {
            if(mSparseBooleanArray.get(i)) {
                mTempArry.add(images.get(i));
            }
        }

        return mTempArry;
    }




    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_thumbnail, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final int temp =position;
        Glide.with(mContext).load(images.get(position))
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(holder.thumbnail);

        if(mSparseBooleanArray.get(temp))
        {
            holder.thumbnail.setColorFilter(R.color.green_500);
        }
        else
        {
            holder.thumbnail.setColorFilter(null);
        }
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mSparseBooleanArray.get(temp)) {

                    if (count != pics) {
                        count++;
                        mSparseBooleanArray.put(temp, !mSparseBooleanArray.get(temp));
                           if (mSparseBooleanArray.get(temp)) {
                            holder.thumbnail.setColorFilter(R.color.green_500);
                           } else {
                           holder.thumbnail.setColorFilter(null);
                           }
                }
                    else {
                        Toast.makeText(mContext, "You can only select "+pics+" pic for uploading.", Toast.LENGTH_SHORT ).show();
                }
                }
                else {
                    count--;
                    mSparseBooleanArray.put(temp, !mSparseBooleanArray.get(temp));
                    if (mSparseBooleanArray.get(temp)) {
                        holder.thumbnail.setColorFilter(R.color.green_500);
                    } else {
                        holder.thumbnail.setColorFilter(null);
                    }
                }
            }
//                    else
//                    {
//
//                    }


            });


           }
    @Override
    public int getItemCount() {
        return images.size();
    }


}

