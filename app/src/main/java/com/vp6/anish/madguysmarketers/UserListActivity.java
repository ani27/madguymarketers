package com.vp6.anish.madguysmarketers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

public class UserListActivity extends AppCompatActivity {

    ArrayList<String>users_name;
    ArrayList<String>users_number;
    ArrayList<String>users_id;
    UsersListAdapter usersListAdapter;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Members");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = (ProgressBar)findViewById(R.id.progressBarUsers);
        recyclerView  = (RecyclerView)findViewById(R.id.users_list_recyclerview);
        progressBar.setVisibility(View.VISIBLE);
        users_name=new ArrayList<>();
        users_number = new ArrayList<>();
        users_id = new ArrayList<>();

        JsonObject json = new JsonObject();
        json.addProperty("token", SessionManager.getjwt(UserListActivity.this));
        Ion.with(this)
                .load("GET", getString(R.string.url).concat("members/"))
                .setHeader("x-access-token",SessionManager.getjwt(UserListActivity.this))
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {
                            JsonArray members = result.get("members").getAsJsonArray();
                            //JsonArray all_listings = result.get("listings").getAsJsonArray();
                            for (int i = 0; i < members.size(); i++)
                            {
                                JsonObject member = members.get(i).getAsJsonObject();
                                users_name.add(member.get("name").getAsString());
                                users_number.add(member.get("phone").getAsString());
                                users_id.add(member.get("_id").getAsString());
                            }

                            usersListAdapter = new UsersListAdapter(UserListActivity.this,users_name,users_number,users_id);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(UserListActivity.this);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(usersListAdapter);
                            // int i = listings.size();
                            progressBar.setVisibility(View.GONE);
                        }
                        catch (NullPointerException e1)
                        {

                            Log.i("Exception", e1.getMessage());
                            Toast.makeText(UserListActivity.this,"Failed, Try again later", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });

    }


    public void selecteduser(String name, String number, String id){
        Intent intent = new Intent(UserListActivity.this,MemberProfile.class);
        intent.putExtra("name", name);
        intent.putExtra("number", number);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    public void memberloc(View view){
        Intent intent = new Intent(UserListActivity.this,AllMembersLocationActivity.class);
        startActivity(intent);
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

}
