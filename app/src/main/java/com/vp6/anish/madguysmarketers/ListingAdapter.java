package com.vp6.anish.madguysmarketers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by anish on 14-10-2016.
 */
public class ListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {


    public ArrayList<String> name;
    public ArrayList<String> type;
    public ArrayList<String> id;
    public ArrayList<ListItem> allData;
    public ArrayList<ListItem> filterList;
    public ArrayList<ListItem> scrollList;
    MyListingFragment myListingFragment;
    AllListingFragment allListingFragment;
    Context mContext;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    // private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView mRecyclerView;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;



    public ListingAdapter(Context context, AllListingFragment allListingFragment, final ArrayList<ListItem> allData, RecyclerView recyclerView) {
        this.mContext = context;
        this.allData = allData;
        this.allListingFragment = allListingFragment;
        this.myListingFragment = null;
        this.filterList = new ArrayList<>();
        this.filterList.addAll(this.allData);
        // we copy the original list to the filter list and use it for setting row values
        this.scrollList = new ArrayList<>();
        if (allData.size() >= 11)
            this.scrollList.addAll(this.allData.subList(0,9));
        else
            this.scrollList.addAll(this.allData);
        this.mRecyclerView = recyclerView;


        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && totalItemCount < filterList.size()) {

                    scrollList.add(null);
                    notifyItemInserted(scrollList.size() - 1);

                    //Load more data for reyclerview
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                scrollList.remove(scrollList.size() - 1);
                            }catch (Exception e){
                                Log.i("Inside Catch","Exception");
                            }
                            notifyItemRemoved(scrollList.size());
                            //Load data
                            int index = scrollList.size();

                            int end = index + 10;
                            if (filterList.size() < end)
                            {
                                end = filterList.size();
                            }
                            for (int i = index; i < end; i++) {
                                scrollList.add(filterList.get(i));
                            }
                            notifyDataSetChanged();
                            setLoaded();
                        }
                    },1000);
                    isLoading = true;
                }
            }
        });
    }

    public ListingAdapter(Context context, MyListingFragment myListingFragment, final ArrayList<ListItem> allData, RecyclerView recyclerView) {
        this.mContext = context;
        this.allData = allData;
        this.allListingFragment = null;
        this.myListingFragment = myListingFragment;
        this.filterList = new ArrayList<ListItem>();
        this.filterList.addAll(this.allData);
        // we copy the original list to the filter list and use it for setting row values
        this.scrollList = new ArrayList<>();
        if (allData.size() >= 11)
            this.scrollList.addAll(this.allData.subList(0,9));
        else
            this.scrollList.addAll(this.allData);
        this.mRecyclerView = recyclerView;


        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && totalItemCount < filterList.size()) {

                    scrollList.add(null);
                    notifyItemInserted(scrollList.size() - 1);

                    //Load more data for reyclerview
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                scrollList.remove(scrollList.size() - 1);
                            }catch (Exception e){
                                Log.i("Inside Catch","Exception");
                            }
                            notifyItemRemoved(scrollList.size());
                            //Load data
                            int index = scrollList.size();

                            int end = index + 10;
                            if (filterList.size() < end)
                            {
                                end = filterList.size();
                            }
                            for (int i = index; i < end; i++) {
                                scrollList.add(filterList.get(i));
                            }
                            notifyDataSetChanged();
                            setLoaded();
                        }
                    }, 1000);
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
                    .inflate(R.layout.listing_item, parent, false);

            return new MyViewHolder(itemView);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return scrollList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, final int position) {

        if (holder1 instanceof MyViewHolder) {
            MyViewHolder holder = (MyViewHolder) holder1;
            holder.name.setText(scrollList.get(position).name);
            holder.type.setText(scrollList.get(position).type);
            holder.id.setText(scrollList.get(position).id);
            if (scrollList.get(position).address.length() > 1)
                holder.address.setText(scrollList.get(position).address);
            else
                holder.address.setText("Address : N.A.");
            if (scrollList.get(position).phone.length() > 1)
                holder.number.setText(scrollList.get(position).phone);
            else
                holder.number.setText("Number : N.A.");
            holder.status.setText(scrollList.get(position).status);

            if (scrollList.get(position).image_url.equals(""))
                holder.thumbnail.setImageResource(R.drawable.profile);
            else {
                Glide.with(mContext).load(scrollList.get(position).image_url)
                        .thumbnail(0.5f)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(holder.thumbnail);
            }

            if (allListingFragment == null) {
                holder.item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isNetworkAvailable())
                            myListingFragment.click(scrollList.get(position).id, scrollList.get(position).type);
                        else
                            Toast.makeText(mContext, "Connect to Internet and Try again", Toast.LENGTH_LONG).show();

                    }
                });
            } else if (myListingFragment == null) {
                holder.item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isNetworkAvailable())
                            allListingFragment.click(scrollList.get(position).id, scrollList.get(position).type);
                        else
                            Toast.makeText(mContext, "Connect to Internet and Try again", Toast.LENGTH_LONG).show();

                    }
                });
            }
        }
        else if (holder1 instanceof CallLogsAdapter.LoadingViewHolder)
        {
            CallLogsAdapter.LoadingViewHolder holder = (CallLogsAdapter.LoadingViewHolder) holder1;
            holder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return scrollList == null ? 0 : scrollList.size();
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
            name = (TextView) view.findViewById(R.id.list_item_name);
            thumbnail = (CircleImageView) view.findViewById(R.id.list_item_image);
            type = (TextView) view.findViewById(R.id.list_item_type);
            id = (TextView) view.findViewById(R.id.list_item_id);
            status = (TextView) view.findViewById(R.id.list_item_status);
            number = (TextView) view.findViewById(R.id.list_item_number);
            address = (TextView) view.findViewById(R.id.list_item_address);
            item = (RelativeLayout) view.findViewById(R.id.list_item);
        }

    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar_scroll);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void setLoaded() {
        isLoading = false;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }


    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filterList.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                filterList.addAll(allData);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();

                for (final ListItem item : allData) {
                    if (item.name.toLowerCase().trim().contains(filterPattern) || item.address.toLowerCase().trim().contains(filterPattern)) {
                        filterList.add(item);
                        Log.i("Count", "add");
                    }
                }
            }
            results.values = filterList;
            results.count = filterList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            scrollList.clear();
            if(filterList.size() > 10)
            scrollList.addAll(filterList.subList(0,9));
            else
            scrollList.addAll(filterList);
            notifyDataSetChanged();
        }

    };

    public void sortlistnameasc(){

        Collections.sort(allData, new Comparator<ListItem>() {
            @Override
            public int compare(ListItem o1, ListItem o2) {
                return o1.name.toLowerCase().compareTo(o2.name.toLowerCase());
            }
        });
        Log.i("inside compare", "check");

    }

    public void sortlistnamedes(){

        Collections.sort(allData, new Comparator<ListItem>() {
            @Override
            public int compare(ListItem o1, ListItem o2) {
                return o2.name.toLowerCase().compareTo(o1.name.toLowerCase());
            }
        });
        Log.i("inside compare", "check");

    }

    public void sortlisttypeasc(){

        Collections.sort(allData, new Comparator<ListItem>() {
            @Override
            public int compare(ListItem o1, ListItem o2) {
                return o1.type.toLowerCase().compareTo(o2.type.toLowerCase());
            }
        });
        Log.i("inside compare", "check");

    }

    public void sortlisttypedes(){

        Collections.sort(allData, new Comparator<ListItem>() {
            @Override
            public int compare(ListItem o1, ListItem o2) {
                return o2.type.toLowerCase().compareTo(o1.type.toLowerCase());
            }
        });
        Log.i("inside compare", "check");

    }
    public void sortliststatusasc(){

        Collections.sort(allData, new Comparator<ListItem>() {
            @Override
            public int compare(ListItem o1, ListItem o2) {
                return o1.status.toLowerCase().compareTo(o2.status.toLowerCase());
            }
        });
        Log.i("inside compare", "check");

    }

    public void sortliststatusdes(){

        Collections.sort(allData, new Comparator<ListItem>() {
            @Override
            public int compare(ListItem o1, ListItem o2) {
                return o2.status.toLowerCase().compareTo(o1.status.toLowerCase());
            }
        });
        Log.i("inside compare", "check");

    }
}
