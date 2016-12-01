package com.vp6.anish.madguysmarketers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by anish on 21-10-2016.
 */
public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.MyViewHolder> {


    public ArrayList<String> name;
    public ArrayList<String> number;
    public ArrayList<String> id;

    Context mContext;
    UserListActivity userListActivity;


    public UsersListAdapter(UserListActivity userListActivity,  ArrayList<String>name, ArrayList<String>number, ArrayList<String>id) {
        this.userListActivity = userListActivity;
        this.name = name;
        this.id = id;
        this.number = number;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.name.setText(name.get(position));
        holder.number.setText(number.get(position));
        holder.id.setText(id.get(position));
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             userListActivity.selecteduser(name.get(position),number.get(position),id.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView number;
        public TextView id;
        public RelativeLayout item;
        public MyViewHolder(View view) {
            super(view);
            name= (TextView) view.findViewById(R.id.users_name);
            number =(TextView) view.findViewById(R.id.users_number);
            id =(TextView)view.findViewById(R.id.users_id);
            item =(RelativeLayout)view.findViewById(R.id.user_list);
        }
    }

}
