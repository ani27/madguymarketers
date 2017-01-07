package com.vp6.anish.madguysmarketers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by anish on 14-10-2016.
 */
public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.MyViewHolder> {


    public ArrayList<String> name;
    public ArrayList<String> type;
    public ArrayList<String> id;
    public ArrayList<String> imageurl;
    public ArrayList<String> mData_phone_number;
    public ArrayList<String> mData_status;
    public ArrayList<String> mData_address;
    public ArrayList<String> mData_lat;
    public ArrayList<String> mData_lng;
    MyListingFragment myListingFragment;
    AllListingFragment allListingFragment;
    Context mContext;


    public ListingAdapter(Context context, ArrayList<String> imageurl, ArrayList<String>name, ArrayList<String>type, ArrayList<String>id,ArrayList<String>number,ArrayList<String>status,ArrayList<String>address, MyListingFragment myListingFragment) {
        mContext = context;
        this.imageurl = imageurl;
        this.name = name;
        this.id = id;
        this.type = type;
        this.mData_address= address;
        this.mData_phone_number = number;
        this.mData_status =status;
        this.myListingFragment = myListingFragment;
        this.allListingFragment = null;

    }

    public ListingAdapter(Context context, ArrayList<String> imageurl, ArrayList<String>name, ArrayList<String>type, ArrayList<String>id,ArrayList<String>number,ArrayList<String>status,ArrayList<String>address, AllListingFragment myListingFragment) {
        mContext = context;
        this.imageurl = imageurl;
        this.name = name;
        this.id = id;
        this.type = type;
        this.mData_address= address;
        this.mData_phone_number = number;
        this.mData_status =status;
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
            if(mData_address.get(position).length() > 1)
            holder.address.setText(mData_address.get(position));
            else
                holder.address.setText("Address : N.A.");
            if(mData_phone_number.get(position).length() > 1)
            holder.number.setText(mData_phone_number.get(position));
            else
            holder.number.setText("Number : N.A.");
            holder.status.setText(mData_status.get(position));
            if (imageurl.get(position).equals(""))
                holder.thumbnail.setImageResource(R.drawable.profile);
            else
            {
                Glide.with(mContext).load(imageurl.get(position))
                        .thumbnail(0.5f)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(holder.thumbnail);
                Log.i("ML adapter", imageurl.get(position));
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
            if(mData_address.get(position).length() > 1)
                holder.address.setText(mData_address.get(position));
            else
                holder.address.setText("Address : N.A.");
            if(mData_phone_number.get(position).length() > 1)
                holder.number.setText(mData_phone_number.get(position));
            else
                holder.number.setText("Number : N.A.");
            holder.status.setText(mData_status.get(position));

            if (imageurl.get(position).equals(""))
            holder.thumbnail.setImageResource(R.drawable.profile);
            else
            {
                Glide.with(mContext).load(imageurl.get(position))
                        .thumbnail(0.5f)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(holder.thumbnail);


                Log.i("ALL adapter", imageurl.get(position));
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
        public CircleImageView thumbnail;
        public TextView name;
        public TextView type;
        public TextView id;
        public TextView number;
        public TextView status;
        public TextView address;

        public RelativeLayout item;
        public MyViewHolder(View view) {
            super(view);
            name= (TextView) view.findViewById(R.id.list_item_name);
            thumbnail = (CircleImageView) view.findViewById(R.id.list_item_image);
            type =(TextView) view.findViewById(R.id.list_item_type);
            id =(TextView)view.findViewById(R.id.list_item_id);
            status =(TextView)view.findViewById(R.id.list_item_status);
            number =(TextView)view.findViewById(R.id.list_item_number);
            address =(TextView)view.findViewById(R.id.list_item_address);
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
