package com.vp6.anish.madguysmarketers;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by anish on 21-10-2016.
 */
public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.MyViewHolder> {


    public ArrayList<String> name;
    public ArrayList<String> number;
    public ArrayList<String> id;
    String result = "";
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
        holder.last_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                String date = sdf.format(System.currentTimeMillis());

                JsonObject json = new JsonObject();
                json.addProperty("date", date);

                Ion.with(userListActivity)
                        .load("POST", userListActivity.getString(R.string.url).concat("getlocation/"+id.get(position)+"/"))
                        .setHeader("x-access-token", SessionManager.getjwt(userListActivity))
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {

                                JsonArray locations = result.get("locations").getAsJsonArray();

                                if(locations.size() > 0) {



                                        JsonObject location = locations.get(locations.size()-1).getAsJsonObject();
                                        //String datetime = location.get("datetime").getAsString();
                                        String lat = location.get("lat").getAsString();
                                        String lng = location.get("lng").getAsString();
                                        //LatLng loc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                                        Intent intent = new Intent(userListActivity,LocationDisplayActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("lat",lat);
                                        intent.putExtra("lng",lng);
                                        userListActivity.startActivity(intent);


                                }
                                else{
                                    Toast.makeText(userListActivity,"No Location is available for this date",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


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
        public TextView added;
        public TextView last_location;
        public RelativeLayout item;
        public MyViewHolder(View view) {
            super(view);
            name= (TextView) view.findViewById(R.id.users_name);
            number =(TextView) view.findViewById(R.id.users_number);
            id =(TextView)view.findViewById(R.id.users_id);
            added =(TextView)view.findViewById(R.id.users_added);
            last_location = (TextView)view.findViewById(R.id.tv_last_location);
            item =(RelativeLayout)view.findViewById(R.id.user_list);
        }
    }


    public String getAddressFromLocation(final double latitude, final double longitude, final Context context) {


        userListActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());

                try {
                    List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address1 = addressList.get(0);
                        //StringBuilder sb = new StringBuilder();

                        result = address1.getLocality();

                    }
                } catch (IOException e) {
                    Log.e("GeoCoder", "Unable connect to Geocoder", e);
                }


            }
        });
        return result;
    }


}
