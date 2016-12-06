package com.vp6.anish.madguysmarketers;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import com.google.maps.android.clustering.algo.Algorithm;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, MyListingFragment.OnFragmentInteractionListener, AllListingFragment.OnFragmentInteractionListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public ArrayList<String> mData_name;
    public ArrayList<String> mData_type;
    public ArrayList<String> mData_id;
    public ArrayList<String> mData_imageurl;
    public ArrayList<String> allData_name;
    public ArrayList<String> allData_type;
    public ArrayList<String> allData_id;
    public ArrayList<String> allData_imageurl;
    MyListingFragment myListingFragment;
    AllListingFragment allListingFragment;
    ArrayList<ArrayList>mData;
    ArrayList<ArrayList>allData;
    ProgressBar progressBar;
    TextView name;
    TextView number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Listings");

        progressBar = (ProgressBar)findViewById(R.id.progressBar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        name = (TextView)headerview.findViewById(R.id.user_name_header);
        number = (TextView)headerview.findViewById(R.id.user_number_header);
        name.setText(SessionManager.getName(ListingActivity.this));
        number.setText(SessionManager.getphonenumber(ListingActivity.this));
        navigationView.setNavigationItemSelectedListener(this);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        Intent intent = new Intent(this, LocationService.class);
        startService(intent);

    }




    private void setupViewPager(ViewPager viewPager) {
        ListingPageAdapter adapter = new ListingPageAdapter(getSupportFragmentManager());
        adapter.addFragment(myListingFragment, "My Listings");
        adapter.addFragment(allListingFragment, "All Listings");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.listing, menu);
//        return true;
        getMenuInflater().inflate(R.menu.options_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(ListingActivity.this);

        return true;
    }

   @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement the filter logic
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_members) {
            // Handle the camera action
            if (isNetworkAvailable()) {
                Intent intent = new Intent(ListingActivity.this, UserListActivity.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(ListingActivity.this, "Connect to Internet and Try again", Toast.LENGTH_LONG).show();

            }


        } else if (id == R.id.nav_admin) {
            if (isNetworkAvailable()) {
                if (SessionManager.getIsAdmin(ListingActivity.this)) {
                    Intent intent = new Intent(ListingActivity.this, UserAuthenticateActivity.class);
                    startActivity(intent);
                } else
                    Toast.makeText(this, "You are not an admin", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(ListingActivity.this, "Connect to Internet and Try again", Toast.LENGTH_LONG).show();

            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public ArrayList<ArrayList> getData() {
        return mData;
    }

    @Override
    public ArrayList<ArrayList> alldatalist() {
        return allData;
    }

     @Override
    public void onResume()
     {

         progressBar.setVisibility(View.VISIBLE);
         myListingFragment = new MyListingFragment();
         allListingFragment = new AllListingFragment();
         mData = new ArrayList<>();
         mData_name = new ArrayList<>();
         mData_type = new ArrayList<>();
         mData_id = new ArrayList<>();
         mData_imageurl = new ArrayList<>();

         allData = new ArrayList<>();
         allData_name = new ArrayList<>();
         allData_type = new ArrayList<>();
         allData_id = new ArrayList<>();
         allData_imageurl = new ArrayList<>();
         //Log.i("JWT", SessionManager.getjwt(this));


         if (isNetworkAvailable()) {
             JsonObject json = new JsonObject();
             json.addProperty("token", SessionManager.getjwt(ListingActivity.this));
             Ion.with(this)
                     .load("GET", getString(R.string.url).concat("all/"))
                     .setHeader("x-access-token", SessionManager.getjwt(ListingActivity.this))
                     .setJsonObjectBody(json)
                     .asJsonObject()
                     .setCallback(new FutureCallback<JsonObject>() {
                         @Override
                         public void onCompleted(Exception e, JsonObject result) {
                             // do stuff with the result or error
                             try {

                                 JsonArray listings = result.get("my_listings").getAsJsonArray();
                                 JsonArray all_listings = result.get("listings").getAsJsonArray();

                                 // int i = listings.size();

                                 for (int i = 0; i < listings.size(); i++) {
                                     JsonObject listing = listings.get(i).getAsJsonObject();
                                     mData_name.add(listing.get("name").getAsString());
                                     mData_id.add(listing.get("_id").getAsString());
                                     mData_type.add(listing.get("listing_type").getAsString());
                                     if (!listing.get("photo").getAsString().equals(""))
                                         mData_imageurl.add(getString(R.string.media_url).concat(listing.get("photo").getAsString()));
                                     else
                                         mData_imageurl.add("");
                                 }
                                 mData.add(mData_name);
                                 mData.add(mData_type);
                                 mData.add(mData_id);
                                 mData.add(mData_imageurl);

                                 for (int i = 0; i < all_listings.size(); i++) {
                                     JsonObject listing = all_listings.get(i).getAsJsonObject();
                                     allData_name.add(listing.get("name").getAsString());
                                     allData_id.add(listing.get("_id").getAsString());
                                     allData_type.add(listing.get("listing_type").getAsString());
                                     if (!listing.get("photo").getAsString().equals("")) {
                                         allData_imageurl.add(getString(R.string.media_url).concat(listing.get("photo").getAsString()));
                                      //Log.i("Imageurl",getString(R.string.media_url).concat(listing.get("photo").getAsString()));
                                     }
                                     else
                                         allData_imageurl.add("");

                                 }

                                 allData.add(allData_name);
                                 allData.add(allData_type);
                                 allData.add(allData_id);
                                 allData.add(allData_imageurl);

                                 setupViewPager(viewPager);
                                 tabLayout.setupWithViewPager(viewPager);
                                 progressBar.setVisibility(View.GONE);
                             } catch (NullPointerException e1) {

                                 Toast.makeText(ListingActivity.this, "Failed, Try again later", Toast.LENGTH_LONG).show();
                                 progressBar.setVisibility(View.GONE);
                                 Log.i("EXCEPTION", e1.getMessage());

                             }

                         }
                     });
         }
         else {
             Toast.makeText(ListingActivity.this, "Connect to Internet and Try again", Toast.LENGTH_LONG).show();
             progressBar.setVisibility(View.GONE);
         }
         super.onResume();


     }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}



class ListingPageAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ListingPageAdapter(FragmentManager manager) {
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