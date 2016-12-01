package com.vp6.anish.madguysmarketers;

import android.content.Context;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anish on 14-10-2016.
 */
public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.MyViewHolder> {


    public ArrayList<String> name;
    public ArrayList<String> type;
    public ArrayList<String> id;
    public ArrayList<String> imageurl;
    MyListingFragment myListingFragment;
    AllListingFragment allListingFragment;
    Context mContext;


    public ListingAdapter(Context context, ArrayList<String> imageurl, ArrayList<String>name, ArrayList<String>type, ArrayList<String>id, MyListingFragment myListingFragment) {
        mContext = context;
        this.imageurl = imageurl;
        this.name = name;
        this.id = id;
        this.type = type;
        this.myListingFragment = myListingFragment;
        this.allListingFragment = null;
    }

    public ListingAdapter(Context context, ArrayList<String> imageurl, ArrayList<String>name, ArrayList<String>type, ArrayList<String>id, AllListingFragment myListingFragment) {
        mContext = context;
        this.imageurl = imageurl;
        this.name = name;
        this.id = id;
        this.type = type;
        this.allListingFragment = myListingFragment;
        this.myListingFragment = null;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listing_item, parent, false);

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

//        Glide.with(mContext).load(R.mipmap.ic_launcher)
//                .thumbnail(0.5f)
//                .crossFade()
//                .diskCacheStrategy(DiskCacheStrategy.RESULT)
//                .into(holder.thumbnail);
        if(allListingFragment == null) {
            holder.name.setText(name.get(position));
            holder.type.setText(type.get(position));
            holder.id.setText(id.get(position));
            if (imageurl.get(position).equals(""))
                holder.thumbnail.setImageResource(R.mipmap.ic_launcher);
            else
            {
                Glide.with(mContext).load(imageurl.get(position))
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(holder.thumbnail);
            }
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isNetworkAvailable())
                    myListingFragment.click(id.get(position),type.get(position));
                    else
                        Toast.makeText(mContext, "Connect to Internet and Try again", Toast.LENGTH_LONG).show();

                }
            });
        }
        else if(myListingFragment == null ){
            holder.name.setText(name.get(position));
            holder.type.setText(type.get(position));
            holder.id.setText(id.get(position));
            if (imageurl.get(position).equals(""))
            holder.thumbnail.setImageResource(R.mipmap.ic_launcher);
            else
            {
                Glide.with(mContext).load(imageurl.get(position))
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(holder.thumbnail);
            }
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isNetworkAvailable())
                        allListingFragment.click(id.get(position),type.get(position));
                    else
                        Toast.makeText(mContext, "Connect to Internet and Try again", Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public TextView name;
        public TextView type;
        public TextView id;
        public RelativeLayout item;
        public MyViewHolder(View view) {
            super(view);
            name= (TextView) view.findViewById(R.id.list_item_name);
            thumbnail = (ImageView) view.findViewById(R.id.list_item_image);
            type =(TextView) view.findViewById(R.id.list_item_type);
            id =(TextView)view.findViewById(R.id.list_item_id);
           item =(RelativeLayout)view.findViewById(R.id.list_item);
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
