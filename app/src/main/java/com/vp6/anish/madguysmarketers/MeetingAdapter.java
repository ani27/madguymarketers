package com.vp6.anish.madguysmarketers;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.koushikdutta.ion.builder.Builders;

import java.util.ArrayList;

/**
 * Created by anish on 23-10-2016.
 */
public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MyViewHolder> {

    ArrayList<String> description;
    ArrayList<String>lat;
    ArrayList<String>lng;
    ArrayList<ArrayList>imageurl;
    ArrayList<String>id;
    ArrayList<String>creator;
    ArrayList<String>created;
    MeetingActivity meetingActivity;
    public  MeetingAdapter (MeetingActivity meetingActivity,ArrayList<String>description, ArrayList<String>lat, ArrayList<String>lng, ArrayList<ArrayList>imageurl, ArrayList<String>id, ArrayList<String>creator, ArrayList<String>created)
    {
        this.description = description;
        this.lat = lat;
        this.lng= lng;
        this.imageurl  = imageurl;
        this.id = id;
        this.meetingActivity = meetingActivity;
        this.creator = creator;
        this.created = created;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView id;
        TextView description;
        TextView creator;
        TextView created;

        RecyclerView recyclerView;
        Button button;

        public MyViewHolder(View view) {
            super(view);

            id = (TextView)view.findViewById(R.id.meeting_id);
            description = (TextView)view.findViewById(R.id.meeting_details);
            creator = (TextView)view.findViewById(R.id.meeting_creator);
            created = (TextView)view.findViewById(R.id.meeting_created);
            recyclerView = (RecyclerView)view.findViewById(R.id.meeting_images_recyclerview);
            button = (Button)view.findViewById(R.id.meeting_location);


        }
    }
    @Override
    public MeetingAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meeting_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MeetingAdapter.MyViewHolder holder, final int position) {

        holder.description.setText(description.get(position));
        holder.id.setText(id.get(position));
        holder.created.setText(created.get(position));
        holder.creator.setText(creator.get(position));
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                meetingActivity.showmeetinglocation(lat.get(position),lng.get(position));
            }
        });
        if (imageurl.get(position).size() > 0)
        {
            HorizontalAdapter horizontalAdapter = new HorizontalAdapter(meetingActivity, imageurl.get(position));
            final LinearLayoutManager horizontalLayoutManagaer
                    = new LinearLayoutManager(meetingActivity, LinearLayoutManager.HORIZONTAL, false);
            holder.recyclerView.setLayoutManager(horizontalLayoutManagaer);
            holder.recyclerView.setAdapter(horizontalAdapter);
            holder.recyclerView.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.recyclerView.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return description.size();
    }

    public void insert(String description, String creator , String created, String id, ArrayList<String> imageurl, String lat, String lng )
    {
        this.description.add(description);
        this.creator.add(creator);
        this.created.add(created);
        this.id.add(id);
        this.imageurl.add(imageurl);
        this.lat.add(lat);
        this.lng.add(lng);
        notifyDataSetChanged();
    }

}
