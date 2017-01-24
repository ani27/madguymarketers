package com.vp6.anish.madguysmarketers;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditCoachingActivity extends AppCompatActivity {


    EditText coachingname;
    EditText directorname;
    EditText state;
    EditText city;
    EditText address;
    EditText pincode;
    EditText mobilenumber;
    EditText hardware;
    EditText totalstudents;
    EditText totalstudentshavingmobile;
    EditText examteaching;
    TextView maplocation;
    int map_activity = 104;
    String latitude="";
    String longitude="";
    String id;
    Spinner coaching_status;
    ArrayList<String> senddata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_coaching);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit");

        coachingname = (EditText) findViewById(R.id.coaching_name);
        directorname = (EditText) findViewById(R.id.director_name);
        state = (EditText) findViewById(R.id.state);
        city = (EditText) findViewById(R.id.city);
        address = (EditText) findViewById(R.id.address);
        pincode = (EditText) findViewById(R.id.pincode);
        mobilenumber = (EditText) findViewById(R.id.mobile_number);
        hardware = (EditText) findViewById(R.id.hardware);
        totalstudents = (EditText) findViewById(R.id.total_student);
        totalstudentshavingmobile = (EditText) findViewById(R.id.total_students_having_mobile_phone);
        examteaching = (EditText) findViewById(R.id.exam_teaching);
        maplocation = (TextView) findViewById(R.id.exact_location);
        senddata = new ArrayList<>();

        coaching_status = (Spinner) findViewById(R.id.spinner_coaching_status);


        try {
            if (!getIntent().getExtras().getString("coachingname").isEmpty()) {

                coachingname.setText(getIntent().getExtras().getString("coachingname").trim());
                directorname.setText(getIntent().getExtras().getString("directorname").trim());
                state.setText(getIntent().getExtras().getString("state").trim());
                city.setText(getIntent().getExtras().getString("city").trim());
                pincode.setText(getIntent().getExtras().getString("pincode").trim());
                address.setText(getIntent().getExtras().getString("address").trim());
                mobilenumber.setText(getIntent().getExtras().getString("phonenumber").trim());
                hardware.setText(getIntent().getExtras().getString("hardware").trim());
                coaching_status.setSelection(getIntent().getExtras().getInt("coachingstatus"));
                latitude = getIntent().getExtras().getString("lat");
                longitude = getIntent().getExtras().getString("lng");
                totalstudents.setText(getIntent().getExtras().getString("totalstudents"));
                totalstudentshavingmobile.setText(getIntent().getExtras().getString("totalstudentshavingmobilephone"));
                examteaching.setText(getIntent().getExtras().getString("examteaching"));
                id = getIntent().getExtras().getString("id");

            }

        } catch (NullPointerException e1) {
            Log.i("Null Exception", e1.getMessage());

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

    public void maplocation(View view) {

        if (Build.VERSION.SDK_INT >= 23) {
            getPermissionToAccessFineLocation();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (gps) {
                Intent intent = new Intent(EditCoachingActivity.this, MapsActivity.class);
                startActivityForResult(intent, map_activity);
            } else {
                Toast.makeText(this, "Turn on the GPS first", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == map_activity) {
                latitude = data.getStringExtra("Latitude");
                longitude = data.getStringExtra("Longitude");

                try {
                    getAddressFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), this);
                }
                catch (NullPointerException e){

                    Log.i("Catch", "NullPointerException");
                }
                // Toast.makeText(this, latitude + " " + longitude, Toast.LENGTH_SHORT).show();
                if (!latitude.equals("0") && !longitude.equals("0")) {
                    maplocation.setText("Location added");
                    maplocation.setVisibility(View.VISIBLE);

                }
            }

        }
    }

    public void editcoaching(View view) {

        if (isNetworkAvailable()){
        if (!coachingname.getText().toString().trim().equals("")) {
            senddata.add(coachingname.getText().toString().trim());
            senddata.add(directorname.getText().toString().trim());
            senddata.add(state.getText().toString().trim());
            senddata.add(city.getText().toString().trim());
            senddata.add(address.getText().toString().trim());
            senddata.add(pincode.getText().toString().trim());
            senddata.add(mobilenumber.getText().toString().trim());
            senddata.add(latitude);
            senddata.add(longitude);
            senddata.add(totalstudents.getText().toString().trim());
            senddata.add(hardware.getText().toString().trim());
            senddata.add(totalstudentshavingmobile.getText().toString().trim());
            senddata.add(coaching_status.getSelectedItem().toString().trim());
            senddata.add(examteaching.getText().toString().trim());



            JsonObject json = new JsonObject();

            json.addProperty("name", senddata.get(0));
            json.addProperty("director", senddata.get(1));
            json.addProperty("state", senddata.get(2));
            json.addProperty("city", senddata.get(3));
            json.addProperty("address", senddata.get(4));
            json.addProperty("pin_code", (senddata.get(5)));
            json.addProperty("phone", senddata.get(6));
            json.addProperty("lat", senddata.get(7));
            json.addProperty("lng", senddata.get(8));
            json.addProperty("total_students", (senddata.get(9)));
            json.addProperty("hardware_given", senddata.get(10));
            json.addProperty("student_having_mobile", (senddata.get(11)));
            json.addProperty("coaching_status", senddata.get(12));
            json.addProperty("exam_teaching", senddata.get(13));

            Ion.with(EditCoachingActivity.this)
                    .load("PUT", getString(R.string.url).concat("coaching/" + id + "/"))
                    .setHeader("x-access-token", SessionManager.getjwt(EditCoachingActivity.this))
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            // do stuff with the result or error
                            Toast.makeText(EditCoachingActivity.this, "Edited", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(EditCoachingActivity.this, CoachingDisplayActivity.class);
                            intent.putExtra("id", id);
                            startActivity(intent);
                            finish();
                        }
                    });
            Log.i("TAG", "REQUEST");
        } else {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();

        }
    }
        else
        {
            Toast.makeText(EditCoachingActivity.this, "Connect to Internet and Try again", Toast.LENGTH_LONG).show();

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static final int ACCESS_FINE_LOCATION = 2;


    @TargetApi(Build.VERSION_CODES.M)
    public void getPermissionToAccessFineLocation() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
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


    }



    public void getAddressFromLocation(final double latitude, final double longitude, final Context context) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = "";
                try {
                    List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address1 = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address1.getMaxAddressLineIndex(); i++) {
                            sb.append(address1.getAddressLine(i)).append("\n");
                        }

                        result = sb.toString();
                        address.getText().clear();
                        address.setText(result);
                        city.getText().clear();
                        city.setText(address1.getLocality());
                        state.getText().clear();
                        state.setText(address1.getAdminArea());
                        pincode.getText().clear();
                        pincode.setText(address1.getPostalCode());


                    }
                } catch (IOException e) {
                    Log.e("GeoCoder", "Unable connect to Geocoder", e);
                }
            }
        });
    }


}