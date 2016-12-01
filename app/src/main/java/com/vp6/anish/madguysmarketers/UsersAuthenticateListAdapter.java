package com.vp6.anish.madguysmarketers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by anish on 21-10-2016.
 */
public class UsersAuthenticateListAdapter extends RecyclerView.Adapter<UsersAuthenticateListAdapter.MyViewHolder> {


    public ArrayList<String> name;
    public ArrayList<String> number;
    public ArrayList<String> id;

    Context mContext;
    UserAuthenticateActivity userAuthenticateActivity;


    public UsersAuthenticateListAdapter(UserAuthenticateActivity userAuthenticateActivity,  ArrayList<String>name, ArrayList<String>number, ArrayList<String>id) {
        this.userAuthenticateActivity = userAuthenticateActivity;
        this.name = name;
        this.id = id;
        this.number = number;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.authenticate_user, parent, false);

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.name.setText(name.get(position));
        holder.number.setText(number.get(position));
        holder.id.setText(id.get(position));
        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userAuthenticateActivity.userresult(id.get(position), true);
                delete(position);
            }
        });

        holder.cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userAuthenticateActivity.userresult(id.get(position), false);
                delete(position);
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
        public ImageButton check;
        public ImageButton cross;
        public MyViewHolder(View view) {
            super(view);
            name= (TextView) view.findViewById(R.id.users_name_authenticate);
            number =(TextView) view.findViewById(R.id.users_number_authenticate);
            id =(TextView)view.findViewById(R.id.users_id_authenticate);
            item =(RelativeLayout)view.findViewById(R.id.user_list_authenticate);
            check = (ImageButton)view.findViewById(R.id.check);
            cross = (ImageButton)view.findViewById(R.id.cross);
        }
    }


    public void delete(int position) {

        name.remove(position);
        number.remove(position);
        id.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, name.size());

    }

}

