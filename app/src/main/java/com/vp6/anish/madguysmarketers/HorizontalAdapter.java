package com.vp6.anish.madguysmarketers;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

/**
 * Created by anish on 17-10-2016.
 */
public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

    private ArrayList<String> horizontalList;
    String type;
    CafeDisplayActivity cafeDisplayActivity;
    CoachingDisplayActivity coachingDisplayActivity;
    MeetingActivity meetingActivity;
    ProfileMeetingsFragment profileMeetingsFragment;
    private ArrayList<Boolean> uploadcheck;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
      //  public ImageButton close;
        public ProgressBar progressBar;

        public MyViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.horizontal_thumbnail);
           // close = (ImageButton) view.findViewById(R.id.close_button);
            progressBar = (ProgressBar)view.findViewById(R.id.progressBar_images);

        }
    }


    public HorizontalAdapter(CafeDisplayActivity cafeDisplayActivity, ArrayList<String> horizontalList, String type, ArrayList<Boolean>checkupload) {
        this.cafeDisplayActivity = cafeDisplayActivity;
        this.horizontalList = horizontalList;
        this.type = type;
        this.coachingDisplayActivity = null;
        this.uploadcheck = checkupload;
    }
    public HorizontalAdapter(CoachingDisplayActivity coachingDisplayActivity, ArrayList<String> horizontalList, String type, ArrayList<Boolean>uploadcheck) {
        this.coachingDisplayActivity = coachingDisplayActivity;
        this.horizontalList = horizontalList;
        this.type = type;
        this.cafeDisplayActivity = null;
        this.uploadcheck = uploadcheck;
    }
    public HorizontalAdapter(MeetingActivity meetingActivity, ArrayList<String> horizontalList) {
        this.coachingDisplayActivity = null;
        this.horizontalList = horizontalList;
        this.type = null;
        this.cafeDisplayActivity = null;
        this.meetingActivity = meetingActivity;
    }
    public HorizontalAdapter(ProfileMeetingsFragment profileMeetingsFragment, ArrayList<String> horizontalList) {
        this.coachingDisplayActivity = null;
        this.horizontalList = horizontalList;
        this.type = null;
        this.cafeDisplayActivity = null;
        this.meetingActivity = null;
        this.profileMeetingsFragment = profileMeetingsFragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.horizontal_thumbnail, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if(coachingDisplayActivity == null && cafeDisplayActivity != null) {
            Glide.with(cafeDisplayActivity).load(horizontalList.get(position))
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(holder.imageView);


            Log.i("Images", horizontalList.get(position));

        }
        else if (cafeDisplayActivity == null && coachingDisplayActivity != null)
        {
            Glide.with(coachingDisplayActivity).load(horizontalList.get(position))
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(holder.imageView);

            Log.i("Images", horizontalList.get(position));


        }

        else if(cafeDisplayActivity == null && coachingDisplayActivity == null && meetingActivity != null){
           // holder.close.setVisibility(View.GONE);
            Glide.with(meetingActivity).load(horizontalList.get(position))
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(holder.imageView);


        }
        else{
            Glide.with(profileMeetingsFragment).load(horizontalList.get(position))
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(holder.imageView);

        }
    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }

    public void delete(int position) {

        horizontalList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, horizontalList.size());
        if (horizontalList.size() <= 0) {
            if(coachingDisplayActivity== null)
            cafeDisplayActivity.lastphoto(type);
            else
            coachingDisplayActivity.lastphoto(type);
        }

    }
    public void uploadcomplete(int position)
    {
        uploadcheck.add(position,true);
        notifyDataSetChanged();
    }


}
