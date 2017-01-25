package com.vp6.anish.madguysmarketers;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
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
import java.util.List;

public class ListingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, MyListingFragment.OnFragmentInteractionListener, AllListingFragment.OnFragmentInteractionListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public ArrayList<String> mData_name;
    public ArrayList<String> mData_type;
    public ArrayList<String> mData_id;
    public ArrayList<String> mData_phone_number;
    public ArrayList<String> mData_status;
    public ArrayList<String> mData_address;
    public ArrayList<String> mData_lat;
    public ArrayList<String> mData_lng;
    public ArrayList<String> mData_imageurl;

    public ArrayList<String> allData_name;
    public ArrayList<String> allData_type;
    public ArrayList<String> allData_id;
    public ArrayList<String> allData_phone_number;
    public ArrayList<String> allData_status;
    public ArrayList<String> allData_address;
    public ArrayList<String> allData_lat;
    public ArrayList<String> allData_lng;
    public ArrayList<String> allData_imageurl;
    MyListingFragment myListingFragment;
    AllListingFragment allListingFragment;
    ArrayList<ArrayList> mData;
    ArrayList<ArrayList> allData;
    ProgressBar progressBar;
    TextView name;
    TextView number;
    SQLiteDatabase database;
    //    id TEXT , name TEXT, type TEXT, number TEXT, status TEXT, lat TEXT, lng TEXT, address TEXT, imageurl TEXT

    String query1 = "INSERT INTO mylisting( id,name,type,number,status,lat,lng,address,imageurl) VALUES (?,?,?,?,?,?,?,?,?)";
    String query3 = "INSERT INTO allisting( id,name,type,number,status,lat,lng,address,imageurl) VALUES (?,?,?,?,?,?,?,?,?)";
    String query2 = "UPDATE mylisting set name = ? ,type = ?,number = ?,status = ?,lat = ?,lng = ?,address = ?,imageurl = ? where id = ?";
    String query4 = "UPDATE allisting set name = ? ,type = ?,number = ?,status = ?,lat = ?,lng = ?,address = ?,imageurl = ? where id = ?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Listings");
        database = DatabaseHandler.getInstance(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        name = (TextView) headerview.findViewById(R.id.user_name_header);
        number = (TextView) headerview.findViewById(R.id.user_number_header);
        name.setText(SessionManager.getName(ListingActivity.this));
        number.setText(SessionManager.getphonenumber(ListingActivity.this));
        navigationView.setNavigationItemSelectedListener(this);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        Intent intent = new Intent(this, LocationService.class);
        startService(intent);

        myListingFragment = new MyListingFragment();
        allListingFragment = new AllListingFragment();

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
        Log.i("here", "query changed");
        myListingFragment.filter_results(query);
        allListingFragment.filter_results(query);
        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {

        Log.i("here", "Query submit");
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
            } else {
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
        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(ListingActivity.this, HelpActivity.class);
            startActivity(intent);
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
    public void onResume() {

        super.onResume();
        progressBar.setVisibility(View.VISIBLE);

        mData = new ArrayList<>();
        mData_name = new ArrayList<>();
        mData_type = new ArrayList<>();
        mData_id = new ArrayList<>();
        mData_lat = new ArrayList<>();
        mData_lng = new ArrayList<>();
        mData_address = new ArrayList<>();
        mData_phone_number = new ArrayList<>();
        mData_status = new ArrayList<>();
        mData_imageurl = new ArrayList<>();

        allData = new ArrayList<>();
        allData_name = new ArrayList<>();
        allData_type = new ArrayList<>();
        allData_id = new ArrayList<>();
        allData_lat = new ArrayList<>();
        allData_lng = new ArrayList<>();
        allData_address = new ArrayList<>();
        allData_phone_number = new ArrayList<>();
        allData_status = new ArrayList<>();
        allData_imageurl = new ArrayList<>();
        //Log.i("JWT", SessionManager.getjwt(this));

        Cursor cursor = database.rawQuery("Select * from allisting", null);
        if (cursor.getCount() == 0){
            addtodb();
            cursor.close();
        }
        else{
            //    id TEXT , name TEXT, type TEXT, number TEXT, status TEXT, lat TEXT, lng TEXT, address TEXT, imageurl TEXT

            showdb();
            mData.add(mData_name);
            mData.add(mData_type);
            mData.add(mData_id);
            mData.add(mData_imageurl);
            mData.add(mData_status);
            mData.add(mData_address);
            mData.add(mData_phone_number);
            mData.add(mData_lat);
            mData.add(mData_lng);

            allData.add(allData_name);
            allData.add(allData_type);
            allData.add(allData_id);
            allData.add(allData_imageurl);
            allData.add(allData_status);
            allData.add(allData_address);
            allData.add(allData_phone_number);
            allData.add(allData_lat);
            allData.add(allData_lng);

            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
            progressBar.setVisibility(View.GONE);

        }

        updatedb();




    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void addtodb() {


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
                                    mData_status.add(listing.get("status").getAsString());
                                    mData_lat.add(listing.get("lat").getAsString());
                                    mData_lng.add(listing.get("lng").getAsString());
                                    mData_phone_number.add(listing.get("phone").getAsString());
                                    mData_address.add(listing.get("address").getAsString());
                                    if (!listing.get("photo").getAsString().equals(""))
                                        mData_imageurl.add(getString(R.string.media_url).concat(listing.get("photo").getAsString()));
                                    else
                                        mData_imageurl.add("");

                                    try {
                                        //    id TEXT , name TEXT, type TEXT, number TEXT, status TEXT, lat TEXT, lng TEXT, address TEXT, imageurl TEXT

                                        SQLiteStatement stmt = database.compileStatement(query1);
                                        stmt.bindString(1, mData_id.get(i) );
                                        stmt.bindString(2, mData_name.get(i));
                                        stmt.bindString(3, mData_type.get(i));
                                        stmt.bindString(4, mData_phone_number.get(i));
                                        stmt.bindString(5, mData_status.get(i));
                                        stmt.bindString(6, mData_lat.get(i));
                                        stmt.bindString(7, mData_lng.get(i));
                                        stmt.bindString(8, mData_address.get(i));
                                        stmt.bindString(9, mData_imageurl.get(i));
                                        stmt.execute();
                                        stmt.clearBindings();

                                    } catch (Exception e0) {

                                        try {
                                            SQLiteStatement stmt = database.compileStatement(query2);
                                            stmt.bindString(1, mData_name.get(i));
                                            stmt.bindString(2, mData_type.get(i));
                                            stmt.bindString(3, mData_phone_number.get(i));
                                            stmt.bindString(4, mData_status.get(i));
                                            stmt.bindString(5, mData_lat.get(i));
                                            stmt.bindString(6, mData_lng.get(i));
                                            stmt.bindString(7, mData_address.get(i));
                                            stmt.bindString(8, mData_imageurl.get(i));
                                            stmt.bindString(9, mData_id.get(i));
                                            stmt.execute();
                                            stmt.clearBindings();
                                        }catch (Exception e2){
                                            e2.printStackTrace();
                                        }

                                    }
                                }


                                //mData.add(mData_imageurl);

                                for (int i = 0; i < all_listings.size(); i++) {
                                    JsonObject listing = all_listings.get(i).getAsJsonObject();
                                    allData_name.add(listing.get("name").getAsString());
                                    allData_id.add(listing.get("_id").getAsString());
                                    allData_type.add(listing.get("listing_type").getAsString());
                                    allData_status.add(listing.get("status").getAsString());
                                    allData_lat.add(listing.get("lat").getAsString());
                                    allData_lng.add(listing.get("lng").getAsString());
                                    allData_phone_number.add(listing.get("phone").getAsString());
                                    allData_address.add(listing.get("address").getAsString());

                                    if (!listing.get("photo").getAsString().equals("")) {
                                        allData_imageurl.add(getString(R.string.media_url).concat(listing.get("photo").getAsString()));
                                        //Log.i("Imageurl",getString(R.string.media_url).concat(listing.get("photo").getAsString()));
                                    } else
                                        allData_imageurl.add("");


                                    try {
                                        //    id TEXT , name TEXT, type TEXT, number TEXT, status TEXT, lat TEXT, lng TEXT, address TEXT, imageurl TEXT

                                        SQLiteStatement stmt = database.compileStatement(query3);
                                        stmt.bindString(1, allData_id.get(i));
                                        stmt.bindString(2, allData_name.get(i));
                                        stmt.bindString(3, allData_type.get(i));
                                        stmt.bindString(4, allData_phone_number.get(i));
                                        stmt.bindString(5, allData_status.get(i));
                                        stmt.bindString(6, allData_lat.get(i));
                                        stmt.bindString(7, allData_lng.get(i));
                                        stmt.bindString(8, allData_address.get(i));
                                        stmt.bindString(9, allData_imageurl.get(i));
                                        stmt.execute();
                                        stmt.clearBindings();

                                    } catch (Exception e0) {

                                        try {
                                            SQLiteStatement stmt = database.compileStatement(query4);
                                            stmt.bindString(1, allData_name.get(i));
                                            stmt.bindString(2, allData_type.get(i));
                                            stmt.bindString(3, allData_phone_number.get(i));
                                            stmt.bindString(4, allData_status.get(i));
                                            stmt.bindString(5, allData_lat.get(i));
                                            stmt.bindString(6, allData_lng.get(i));
                                            stmt.bindString(7, allData_address.get(i));
                                            stmt.bindString(8, allData_imageurl.get(i));
                                            stmt.bindString(9, allData_id.get(i));
                                            stmt.execute();
                                            stmt.clearBindings();
                                        } catch (Exception e2) {
                                            e2.printStackTrace();
                                        }

                                    }

                                }


                                mData.add(mData_name);
                                mData.add(mData_type);
                                mData.add(mData_id);
                                mData.add(mData_imageurl);
                                mData.add(mData_status);
                                mData.add(mData_address);
                                mData.add(mData_phone_number);
                                mData.add(mData_lat);
                                mData.add(mData_lng);

                                allData.add(allData_name);
                                allData.add(allData_type);
                                allData.add(allData_id);
                                allData.add(allData_imageurl);
                                allData.add(allData_status);
                                allData.add(allData_address);
                                allData.add(allData_phone_number);
                                allData.add(allData_lat);
                                allData.add(allData_lng);

                                setupViewPager(viewPager);
                                tabLayout.setupWithViewPager(viewPager);
                                progressBar.setVisibility(View.GONE);



                            } catch (NullPointerException e1) {

                                Toast.makeText(ListingActivity.this, "Failed, Try again", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                Log.i("EXCEPTION", e1.getMessage());

                            }

                        }
                    });
        } else {
            Toast.makeText(ListingActivity.this, "Connect to Internet and Try again", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }


    public void showdb(){
        Cursor cursor_all = database.rawQuery("Select * from allisting", null);
        while(cursor_all.moveToNext()){
            allData_id.add(cursor_all.getString(0));
            allData_name.add(cursor_all.getString(1));
            allData_type.add(cursor_all.getString(2));
            allData_phone_number.add(cursor_all.getString(3));
            allData_status.add(cursor_all.getString(4));
            allData_lat.add(cursor_all.getString(5));
            allData_lng.add(cursor_all.getString(6));
            allData_address.add(cursor_all.getString(7));
            allData_imageurl.add((cursor_all.getString(8)));

        }
        cursor_all.close();



        Cursor cursor_my = database.rawQuery("Select * from mylisting", null);
        while(cursor_my.moveToNext()){
            mData_id.add(cursor_my.getString(0));
            mData_name.add(cursor_my.getString(1));
            mData_type.add(cursor_my.getString(2));
            mData_phone_number.add(cursor_my.getString(3));
            mData_status.add(cursor_my.getString(4));
            mData_lat.add(cursor_my.getString(5));
            mData_lng.add(cursor_my.getString(6));
            mData_address.add(cursor_my.getString(7));
            mData_imageurl.add((cursor_my.getString(8)));

        }
        cursor_my.close();
    }


    public void updatedb(){


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
                                    try {
                                        //    id TEXT , name TEXT, type TEXT, number TEXT, status TEXT, lat TEXT, lng TEXT, address TEXT, imageurl TEXT
                                        SQLiteStatement stmt = database.compileStatement(query1);
                                        stmt.bindString(1, listing.get("_id").getAsString());
                                        stmt.bindString(2, listing.get("name").getAsString());
                                        stmt.bindString(3, listing.get("listing_type").getAsString());
                                        stmt.bindString(4, listing.get("phone").getAsString());
                                        stmt.bindString(5, listing.get("status").getAsString());
                                        stmt.bindString(6, listing.get("lat").getAsString());
                                        stmt.bindString(7, listing.get("lng").getAsString());
                                        stmt.bindString(8, listing.get("address").getAsString());
                                        if (!listing.get("photo").getAsString().equals("")) {
                                            stmt.bindString(9,getString(R.string.media_url).concat(listing.get("photo").getAsString()));
                                           // Log.i("My Listing images", getString(R.string.media_url).concat(listing.get("photo").getAsString()));
                                        } else {
                                            stmt.bindString(9, "");
                                        }

                                        stmt.execute();
                                        stmt.clearBindings();

                                    } catch (Exception e0) {

                                        try {
                                            SQLiteStatement stmt = database.compileStatement(query2);
                                            stmt.bindString(1, listing.get("name").getAsString());
                                            stmt.bindString(2, listing.get("listing_type").getAsString());
                                            stmt.bindString(3, listing.get("phone").getAsString());
                                            stmt.bindString(4, listing.get("status").getAsString());
                                            stmt.bindString(5, listing.get("lat").getAsString());
                                            stmt.bindString(6, listing.get("lng").getAsString());
                                            stmt.bindString(7, listing.get("address").getAsString());
                                            if (!listing.get("photo").getAsString().equals("")) {
                                                stmt.bindString(8,getString(R.string.media_url).concat(listing.get("photo").getAsString()));
                                            } else {
                                                stmt.bindString(8, "");
                                            }
                                            stmt.bindString(9, listing.get("_id").getAsString());
                                            stmt.execute();
                                            stmt.clearBindings();
                                        } catch (Exception e2) {
                                            e2.printStackTrace();
                                        }

                                    }


                                }


                                //mData.add(mData_imageurl);

                                for (int i = 0; i < all_listings.size(); i++) {
                                    JsonObject listing = all_listings.get(i).getAsJsonObject();
                                    try {
                                        //    id TEXT , name TEXT, type TEXT, number TEXT, status TEXT, lat TEXT, lng TEXT, address TEXT, imageurl TEXT
                                        SQLiteStatement stmt = database.compileStatement(query3);
                                        stmt.bindString(1, listing.get("_id").getAsString());
                                        stmt.bindString(2, listing.get("name").getAsString());
                                        stmt.bindString(3, listing.get("listing_type").getAsString());
                                        stmt.bindString(4, listing.get("phone").getAsString());
                                        stmt.bindString(5, listing.get("status").getAsString());
                                        stmt.bindString(6, listing.get("lat").getAsString());
                                        stmt.bindString(7, listing.get("lng").getAsString());
                                        stmt.bindString(8, listing.get("address").getAsString());
                                        if (!listing.get("photo").getAsString().equals("")) {
                                           stmt.bindString(9,getString(R.string.media_url).concat(listing.get("photo").getAsString()));
                                            //Log.i("All Listing images", getString(R.string.media_url).concat(listing.get("photo").getAsString()));

                                        } else {
                                            stmt.bindString(9, "");
                                        }

                                        stmt.execute();
                                        stmt.clearBindings();

                                    } catch (Exception e0) {

                                        try {
                                            SQLiteStatement stmt = database.compileStatement(query4);
                                            stmt.bindString(1, listing.get("name").getAsString());
                                            stmt.bindString(2, listing.get("listing_type").getAsString());
                                            stmt.bindString(3, listing.get("phone").getAsString());
                                            stmt.bindString(4, listing.get("status").getAsString());
                                            stmt.bindString(5, listing.get("lat").getAsString());
                                            stmt.bindString(6, listing.get("lng").getAsString());
                                            stmt.bindString(7, listing.get("address").getAsString());
                                            if (!listing.get("photo").getAsString().equals("")) {
                                                stmt.bindString(8,getString(R.string.media_url).concat(listing.get("photo").getAsString()));
                                            } else {
                                                stmt.bindString(8, "");
                                            }
                                                stmt.bindString(9, listing.get("_id").getAsString());
                                            stmt.execute();
                                            stmt.clearBindings();
                                        } catch (Exception e2) {
                                            e2.printStackTrace();
                                        }

                                    }
                                }




                            } catch (NullPointerException e1) {

                                Toast.makeText(ListingActivity.this, "Failed to update", Toast.LENGTH_LONG).show();
                               // progressBar.setVisibility(View.GONE);
                                Log.i("EXCEPTION", e1.getMessage());

                            }

                        }
                    });
        } else {
            Toast.makeText(ListingActivity.this, "Connect to Internet and Try again", Toast.LENGTH_LONG).show();
           // progressBar.setVisibility(View.GONE);
        }

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