package com.vp6.anish.madguysmarketers;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class MemberProfile extends AppCompatActivity implements  ProfileCallLogsFragment.OnFragmentInteractionListener{

    String name;
    String number;
    String id;
    TextView name_;
    TextView number_;
    ArrayList<ArrayList> callLogs;
    ArrayList<ArrayList> meetings;
    TabLayout tabLayout;
    ViewPager viewPager;
    ProgressBar progressBar;
    Button status;
    FloatingActionButton profile_edit;
    ProfileCallLogsFragment profileCallLogsFragment;
   // ProfileMeetingsFragment profileMeetingsFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        name = getIntent().getExtras().getString("name");
        number = getIntent().getExtras().getString("number");
        id= getIntent().getExtras().getString("id");
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        name_ = (TextView)findViewById(R.id.user_name);
        number_ = (TextView)findViewById(R.id.user_number);
        name_.setText(name);
        number_.setText(number);
        callLogs = new ArrayList<>();
        meetings = new ArrayList<>();
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        profileCallLogsFragment = new ProfileCallLogsFragment();
        Log.i("Error check", "Flag one");
        status = (Button)findViewById(R.id.status);
        profile_edit = (FloatingActionButton)findViewById(R.id.profile_edit);
        profile_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MemberProfile.this, EditMemberProfileActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                intent.putExtra("number", number);
                startActivity(intent);
            }
        });
        if(!SessionManager.getIsAdmin(MemberProfile.this))
        {
            status.setVisibility(View.GONE);
        }

//        if (id.equals(SessionManager.getjwt(MemberProfile.this)))
//        {
//            profile_edit.setVisibility(View.VISIBLE);
//        }
        if (isNetworkAvailable()) {
            Log.i("Error check", "Flag two");
            Ion.with(this)
                    .load("GET", getString(R.string.url).concat("call/" + id + "/"))
                    .setHeader("x-access-token", SessionManager.getjwt(MemberProfile.this))
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            // do stuff with the result or error
                            try {

                                Log.i("Error check", "Flag three");
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
                                callLogs.add(number);
                                callLogs.add(time);
                                callLogs.add(duration);
                                callLogs.add(type);
                                setupViewPager(viewPager);
                                tabLayout.setupWithViewPager(viewPager);
                                tabLayout.setTabTextColors(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                                progressBar.setVisibility(View.GONE);
                                Log.i("Error check", "Flag four");

                            } catch (NullPointerException e1) {

                                Log.i("Exception", e1.getMessage());
                                Toast.makeText(MemberProfile.this, "Failed, Try again later", Toast.LENGTH_LONG).show();
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

    private void setupViewPager(ViewPager viewPager) {
        MemberProfileAdapter adapter = new MemberProfileAdapter(getSupportFragmentManager());
       // adapter.addFragment(profileMeetingsFragment, "Meetings");
        adapter.addFragment(profileCallLogsFragment, "Call Logs");
        viewPager.setAdapter(adapter);
    }

    public void openpath(View view) {
        if (isNetworkAvailable()) {
            Intent intent = new Intent(MemberProfile.this, UserLocationActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(MemberProfile.this, "Connect to Internet and Try again", Toast.LENGTH_LONG).show();

        }
    }



    private boolean isNetworkAvailable() {
        Log.i("Error check", "Flag network one");
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        Log.i("Error check", "Flag network two");
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public ArrayList<ArrayList> getCallLogs() {
        return callLogs;
    }

//    @Override
//    public ArrayList<ArrayList> getMeetings() {
//        return meetings;
//    }
}


class MemberProfileAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public MemberProfileAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}
