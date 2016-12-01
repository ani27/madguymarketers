package com.vp6.anish.madguysmarketers;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.util.ArrayList;

public class MeetingActivity extends AppCompatActivity {

    ArrayList<String>meetingphotoaddress;
    TextView numberofphotos;
    TextView location;
    int MeetingPhoto = 301;
    int MeetingMap = 302;
    String lat_meeting="";
    String lng_meeting="";
    String id="";
    MeetingAdapter meetingAdapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        id = getIntent().getExtras().getString("id");

        recyclerView = (RecyclerView)findViewById(R.id.meeting_list);
        progressBar = (ProgressBar)findViewById(R.id.progressBar_meeting);
        progressBar.setVisibility(View.VISIBLE);

        JsonObject json = new JsonObject();
        json.addProperty("relatedObjId", id);
        Ion.with(MeetingActivity.this)
                .load("POST", getString(R.string.url).concat("getmeetings/"))
                .setHeader("x-access-token", SessionManager.getjwt(MeetingActivity.this))
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        JsonArray meetingdata = result.get("meetings").getAsJsonArray();
                       ArrayList<String>description_ = new ArrayList<String>();
                       ArrayList<ArrayList>photourl_ = new ArrayList<>();
                       ArrayList<String>lat_ = new ArrayList<>();
                       ArrayList<String>lng_ = new ArrayList<>();
                       ArrayList<String>creator_ = new ArrayList<>();
                       ArrayList<String>created_ = new ArrayList<>();
                       ArrayList<String>id_ = new ArrayList<>();


                        for (int i=0; i<meetingdata.size(); i++){
                            JsonObject meeting = meetingdata.get(i).getAsJsonObject();
                            String description = meeting.get("description").getAsString();
                            JsonArray photourl = meeting.get("photos").getAsJsonArray();
                            ArrayList<String>photosurl = new ArrayList<String>();
                            for (int j=0; j<photourl.size(); j++)
                            {
                                photosurl.add(getString(R.string.media_url).concat(photourl.get(j).getAsString()));
                            }
                            String lat = meeting.get("lat").getAsString();
                            String lng = meeting.get("lng").getAsString();
                            String creator = meeting.get("creator").getAsString();
                            String created = meeting.get("created").getAsString();
                            String id = meeting.get("_id").getAsString();
                            description_.add(description);
                            photourl_.add(photosurl);
                            lat_.add(lat);
                            lng_.add(lng);
                            creator_.add(creator);
                            created_.add(created);
                            id_.add(id);
                        }
                        meetingAdapter = new MeetingAdapter(MeetingActivity.this,description_,lat_,lng_,photourl_,id_,creator_,created_);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MeetingActivity.this);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(meetingAdapter);
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);


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

    public void addmeeting(View v) {
        meetingphotoaddress = new ArrayList<>();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.my_meeting_dialog, (ViewGroup) findViewById(R.id.root));

// Set up the input
        final EditText input = (EditText)view.findViewById(R.id.description);
        numberofphotos = (TextView) view.findViewById(R.id.numberofphotos);
        location = (TextView)view.findViewById(R.id.location_text);
        location.setText("Location not added");
        final Button addphotos = (Button)  view.findViewById(R.id.add_meeting_photos);

        addphotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Build.VERSION.SDK_INT >= 23) {
                    getPermissionToReadExternalStorage();
                }
                    if (ContextCompat.checkSelfPermission(MeetingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(MeetingActivity.this, GalleryActivity.class);
                        startActivityForResult(intent, MeetingPhoto);
                        //numberofphotos.setText(meetingphotoaddress.size() +" photos added");
                    }

            }
        });

        //addphotos.setText("Add Photos");

        final Button addlocation = (Button)view.findViewById(R.id.add_exact_location);
        addlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MeetingActivity.this, MapsActivity.class);
                startActivityForResult(intent, MeetingMap);
                if (!lat_meeting.equals("0") && !lng_meeting.equals("0")) {
                    //location.setText("Location added");


                }
            }
        });


        // addlocation.setText("Add Location");
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setHint("Add Meeting details");
        builder.setView(view);


// Set up the buttons
        builder.setPositiveButton("Add Meeting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (!input.getText().toString().trim().equals("")) {
                    if (isNetworkAvailable()) {
                        JsonObject json = new JsonObject();
                        json.addProperty("description", input.getText().toString().trim());
                        json.addProperty("lat", lat_meeting);
                        json.addProperty("lng", lng_meeting);
                        json.addProperty("relatedObjId", id);
                        Ion.with(MeetingActivity.this)
                                .load("POST", getString(R.string.url).concat("/meeting/"))
                                .setHeader("x-access-token", SessionManager.getjwt(MeetingActivity.this))
                                .setJsonObjectBody(json)
                                .asJsonObject()
                                .setCallback(new FutureCallback<JsonObject>() {
                                    @Override
                                    public void onCompleted(Exception e, JsonObject result) {

                                        String creator = result.get("creator").getAsString();
                                        String created = result.get("created").getAsString();
                                        String id = result.get("_id").getAsString();
                                        meetingAdapter.insert(input.getText().toString().trim(),creator, created, id,meetingphotoaddress,lat_meeting, lng_meeting);
                                        try {
                                            //String meeting_id = result.get("_id").getAsString();
                                            for (int i = 0; i < meetingphotoaddress.size(); i++) {
                                                Ion.with(MeetingActivity.this)
                                                        .load("POST", getString(R.string.url).concat("photo/"))
                                                        .setHeader("x-access-token", SessionManager.getjwt(MeetingActivity.this))
                                                        .setMultipartParameter("type", "meeting")
                                                        .setMultipartParameter("photo_type", "photos")
                                                        .setMultipartParameter("_id", id)
                                                        .setMultipartFile("image", "image/jpeg", new File(meetingphotoaddress.get(i)))
                                                        .asJsonObject()
                                                        .setCallback(new FutureCallback<JsonObject>() {
                                                            @Override
                                                            public void onCompleted(Exception e, JsonObject result) {

                                                            }
                                                        });


                                            }
                                        } catch (NullPointerException e1) {
                                            Toast.makeText(MeetingActivity.this, "Failed, try Later", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                });


                    } else {
                        Toast.makeText(MeetingActivity.this, "Connect to Internet and Try again", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(MeetingActivity.this, "Description is must. ", Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MeetingPhoto) {
                meetingphotoaddress = data.getStringArrayListExtra("photosurl");
                numberofphotos.setText(meetingphotoaddress.size() + " photos added");
            }
            else if (requestCode == MeetingMap){
                lat_meeting = data.getStringExtra("Latitude");
                lng_meeting = data.getStringExtra("Longitude");
                location.setText("Location added");
            }

        }
    }



    public void showmeetinglocation(String Lat, String Lng) {
        if(!Lat.equals("") && !Lng.equals("")){
            if (Build.VERSION.SDK_INT >= 23) {
                getPermissionToAccessFineLocation();
            }
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (gps) {
                    Intent intent = new Intent(MeetingActivity.this, LocationDisplayActivity.class);
                    intent.putExtra("lat", Lat);
                    intent.putExtra("lng", Lng);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Turn on the GPS first", Toast.LENGTH_LONG).show();
                }
            }}
        else
        {

            Toast.makeText(this, "Location was not marked at time of meeting.", Toast.LENGTH_LONG).show();

        }

    }



    private static final int ACCESS_FINE_LOCATION = 2;
    private static final int READ_EXTERNAL_STORAGE = 1;


    @TargetApi(Build.VERSION_CODES.M)
    public void getPermissionToReadExternalStorage() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
                Toast.makeText(this, "Reading Storage permission is required to add photos. You can always turn it off/on in Settings > Application > MadGuys Marketers", Toast.LENGTH_LONG).show();

            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE);
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    public void getPermissionToAccessFineLocation() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
                Toast.makeText(this, "Accessing Location is required to open and work with Maps. You can always turn it off/on in Settings > Application > MadGuys Marketers", Toast.LENGTH_LONG).show();

            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION);
        }

    }


    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request

        if (requestCode == ACCESS_FINE_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Access Location Permission granted", Toast.LENGTH_SHORT).show();


            } else {
                Toast.makeText(this, "Access Location permission denied. It is required to get your exact location", Toast.LENGTH_SHORT).show();
            }
        }

        else if (requestCode == READ_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read External Storage Permission granted", Toast.LENGTH_SHORT).show();


            } else {
                Toast.makeText(this, "Read External Storage permission denied. It is required to add photos", Toast.LENGTH_SHORT).show();
            }
        }



    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
