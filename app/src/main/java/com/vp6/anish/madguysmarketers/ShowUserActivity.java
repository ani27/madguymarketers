package com.vp6.anish.madguysmarketers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class ShowUserActivity extends AppCompatActivity {

    String name;
    String number;
    String id;

    TextView name_;
    TextView number_;

   private ProgressBar progressBar;
    RecyclerView recyclerView;
    CallLogsAdapter callLogsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);
        name = getIntent().getExtras().getString("name");
        number = getIntent().getExtras().getString("number");
        id= getIntent().getExtras().getString("id");
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name_ = (TextView)findViewById(R.id.user_name);
        number_ = (TextView)findViewById(R.id.user_number);
        name_.setText(name);
        number_.setText(number);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView = (RecyclerView)findViewById(R.id.user_call_logs);


        if (isNetworkAvailable()) {
            Ion.with(this)
                    .load("GET", getString(R.string.url).concat("call/" + id + "/"))
                    .setHeader("x-access-token", SessionManager.getjwt(ShowUserActivity.this))
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            // do stuff with the result or error
                            try {

                                ArrayList<String> number = new ArrayList<String>();
                                ArrayList<String> time = new ArrayList<String>();
                                ArrayList<String> duration = new ArrayList<String>();
                                ArrayList<String> type = new ArrayList<String>();

                                JsonArray call_logs = result.get("calls").getAsJsonArray();
                                //JsonArray all_listings = result.get("listings").getAsJsonArray();
                                for (int i = 0; i < call_logs.size(); i++) {
                                    JsonObject call_log = call_logs.get(i).getAsJsonObject();
                                    number.add(call_log.get("phone").getAsString());
                                    time.add(call_log.get("datetime").getAsString());
                                    duration.add(call_log.get("duration").getAsString());
                                    type.add(call_log.get("call_type").getAsString());
                                }


                                callLogsAdapter = new CallLogsAdapter(number, time, duration, type, recyclerView);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ShowUserActivity.this);
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(callLogsAdapter);
                                // int i = listings.size();
                                progressBar.setVisibility(View.GONE);
                            } catch (NullPointerException e1) {

                                Log.i("Exception", e1.getMessage());
                                Toast.makeText(ShowUserActivity.this, "Failed, Try again later", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }

                        }
                    });

        }
        else
        {
                progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Connect to Internet", Toast.LENGTH_SHORT).show();
        }


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



    public void openpath(View view) {
        if (isNetworkAvailable()) {
            Intent intent = new Intent(ShowUserActivity.this, UserLocationActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(ShowUserActivity.this, "Connect to Internet and Try again", Toast.LENGTH_LONG).show();

        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
