package com.vp6.anish.madguysmarketers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class UserAuthenticateActivity extends AppCompatActivity {

    ArrayList<String> users_name;
    ArrayList<String>users_number;
    ArrayList<String>users_id;
    UsersAuthenticateListAdapter usersAuthenticateListAdapter;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_authenticate);
        getSupportActionBar().setTitle("Authorize Members");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressBar = (ProgressBar)findViewById(R.id.progressBar_authenticate);
        recyclerView  = (RecyclerView)findViewById(R.id.recycler_view_authorize);
        progressBar.setVisibility(View.VISIBLE);
        users_name=new ArrayList<>();
        users_number = new ArrayList<>();
        users_id = new ArrayList<>();

        JsonObject json = new JsonObject();
        json.addProperty("token", SessionManager.getjwt(UserAuthenticateActivity.this));
        Ion.with(this)
                .load("GET",getString(R.string.url).concat("tempusers/"))
                .setHeader("x-access-token",SessionManager.getjwt(UserAuthenticateActivity.this))
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {
                            JsonArray members = result.get("tempusers").getAsJsonArray();
                            //JsonArray all_listings = result.get("listings").getAsJsonArray();

                            for (int i = 0; i < members.size(); i++)
                            {
                                JsonObject member = members.get(i).getAsJsonObject();
                                users_name.add(member.get("name").getAsString());
                                users_number.add(member.get("phone").getAsString());
                                users_id.add(member.get("_id").getAsString());
                            }

                            usersAuthenticateListAdapter = new UsersAuthenticateListAdapter(UserAuthenticateActivity.this,users_name,users_number,users_id);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(UserAuthenticateActivity.this);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(usersAuthenticateListAdapter);
                            // int i = listings.size();
                            progressBar.setVisibility(View.GONE);
                        }
                        catch (NullPointerException e1)
                        {

                            progressBar.setVisibility(View.GONE);
                            Log.i("Exception", e1.getMessage());
                            Toast.makeText(UserAuthenticateActivity.this,"Failed, Try again later", Toast.LENGTH_LONG).show();

                        }

                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void userresult(String id, Boolean result){


        if (isNetworkAvailable()) {
            JsonObject json = new JsonObject();
            // json.addProperty("_id", id);
            json.addProperty("result", result);
            Ion.with(this)
                    .load("POST", getString(R.string.url).concat("authorize/" + id + "/"))
                    .setHeader("x-access-token", SessionManager.getjwt(UserAuthenticateActivity.this))
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result_j) {
                            // do stuff with the result or error

                            try {
                                boolean done = result_j.get("done").getAsBoolean();
                                if (done) {
                                    Toast.makeText(UserAuthenticateActivity.this, "Done", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UserAuthenticateActivity.this, "Failed, try later", Toast.LENGTH_SHORT).show();
                                }
                            } catch (NullPointerException e1) {
                                Toast.makeText(UserAuthenticateActivity.this, "Failed, try later", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(UserAuthenticateActivity.this, "Connect to Internet and Try again", Toast.LENGTH_LONG).show();

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
