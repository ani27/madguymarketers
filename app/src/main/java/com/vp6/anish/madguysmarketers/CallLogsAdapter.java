package com.vp6.anish.madguysmarketers;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.koushikdutta.ion.builder.Builders;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anish on 24-10-2016.
 *
 */

public class CallLogsAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    List<String> number1;
    List<String>time1;
    List<String>duration1;
    List<String>type1;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

   // private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView mRecyclerView;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;



    public CallLogsAdapter(final ArrayList<String>number, final ArrayList<String>time, final ArrayList<String>duration, final ArrayList<String>type, RecyclerView mRecyclerView)
    {
        if(number.size() >= 11) {
            this.number1 = number.subList(0, 10);
            this.time1 = time.subList(0, 10);
            this.duration1 = duration.subList(0, 10);
            this.type1 = type.subList(0, 10);
            this.mRecyclerView = mRecyclerView;
        }
        else
        {
            this.number1 = number;
            this.time1 = time;
            this.duration1 = duration;
            this.type1 = type;
            this.mRecyclerView = mRecyclerView;
        }


        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && totalItemCount < number.size()) {

                    number1.add(null);
                    notifyItemInserted(number1.size() - 1);

                    //Load more data for reyclerview
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("haint", "Load More 2");
                            //Remove loading item
                            number1.remove(number1.size() - 1);
                            notifyItemRemoved(number1.size());
                            //Load data
                            int index = number1.size();

                            int end = index + 10;
                            if (number.size() < end)
                            {
                                end = number.size();
                            }
                            for (int i = index; i < end; i++) {

                                number1.add(number.get(i));
                                type1.add(type.get(i));
                                duration1.add(duration.get(i));
                                time1.add(time.get(i));

                            }
                            notifyDataSetChanged();
                            setLoaded();
                        }
                    }, 5000);
                    isLoading = true;
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_LOADING){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.loading_item, parent, false);

            return new LoadingViewHolder(itemView);
        }
        else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.call_log_item, parent, false);

            return new MyViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {

        if (holder1 instanceof MyViewHolder) {
            MyViewHolder holder = (MyViewHolder) holder1;
            holder.number.setText(number1.get(position));
            int dur = Integer.parseInt(duration1.get(position));
            int minutes = dur / 60;
            int sec = dur % 60;
            int hours = minutes / 60;
            minutes = minutes % 60;
            holder.duration.setText("" + hours + ":" + minutes + ":" + sec + "");

            holder.type.setText(type1.get(position));
            holder.time.setText(time1.get(position));
        }
        else if (holder1 instanceof  LoadingViewHolder)
        {
          LoadingViewHolder holder = (LoadingViewHolder) holder1;
            holder.progressBar.setIndeterminate(true);
        }
    }



    @Override
    public int getItemViewType(int position) {
        return number1.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView type;
        public TextView number;
        public TextView duration;
        public TextView time;

        public RelativeLayout item;
        public MyViewHolder(View view) {
            super(view);
            type= (TextView) view.findViewById(R.id.call_log_type);
            number =(TextView) view.findViewById(R.id.call_log_number);
            duration =(TextView)view.findViewById(R.id.call_log_duration);
            time =(TextView)view.findViewById(R.id.call_log_time);
            item =(RelativeLayout)view.findViewById(R.id.call_log_list);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar_scroll);
        }
    }


    @Override
    public int getItemCount() {
        return number1 == null ? 0 : number1.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

}
