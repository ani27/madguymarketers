package com.vp6.anish.madguysmarketers;

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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.util.ArrayList;

public class CafeDisplayActivity extends AppCompatActivity {

    String typeofphotos;
    String id;
    int CafePhotos = 201;
    int AgreementPhotos = 202;
    int OwnerPhoto = 205;
    ArrayList<String>photoaddress;
    ArrayList<String> cafephotoaddress;
    ArrayList<String> agreementphotoaddress;
    ArrayList<Boolean> cafephotoupload;
    ArrayList<Boolean> agreementphotoupload;

    String owner_photo_url;
    HorizontalAdapter horizontalAdapter;
    RecyclerView cafe_photos;
    RecyclerView agreement_photos;
    TextView cafename;
    TextView ownername;
    TextView phone_number;
    TextView pincode;
    TextView city;
    TextView city_above;
    TextView state;
    String lat;
    String lng;
    TextView hardware;
    TextView cafestatus;
    TextView address;
    TextView number_of_cafe_photos;
    TextView number_of_agreement_photos;
    TextView location;

    ImageView ownerphoto;
    ImageView cafephoto;
    ProgressBar progressBar;
    Toolbar toolBar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cafe_display);
//        toolBar = (Toolbar)findViewById(R.id.toolbar);
//        collapsingToolbarLayout= (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);

//        setSupportActionBar(toolBar);
//        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cafephotoaddress = new ArrayList<>();
        agreementphotoaddress = new ArrayList<>();
        cafephotoupload = new ArrayList<>();
        agreementphotoupload = new ArrayList<>();
        cafe_photos = (RecyclerView)findViewById(R.id.cafe_photo_recyclerview);
        agreement_photos= (RecyclerView)findViewById(R.id.partnership_agreement_photo_recyclerview);

        cafename= (TextView)findViewById(R.id.cafe_name_display);
        ownername  = (TextView)findViewById(R.id.owner_name_display);
        phone_number  = (TextView)findViewById(R.id.phone_number_display);
        pincode = (TextView)findViewById(R.id.pincode_display);
        city  = (TextView)findViewById(R.id.city_diplay);
        city_above  = (TextView)findViewById(R.id.city_display);
        state  = (TextView)findViewById(R.id.state_display);
        hardware  = (TextView)findViewById(R.id.hardware_display);
        cafestatus  = (TextView)findViewById(R.id.cafestatus_display);
        address = (TextView)findViewById(R.id.address_display);
        number_of_agreement_photos = (TextView)findViewById(R.id.partnership_agreement_photo_number);
        number_of_cafe_photos = (TextView)findViewById(R.id.cafe_photo_number);
        ownerphoto = (ImageView)findViewById(R.id.owner_photo2);
        cafephoto = (ImageView)findViewById(R.id.cafe_image);
        progressBar = (ProgressBar)findViewById(R.id.progressBar_display_cafe);
        relativeLayout = (RelativeLayout)findViewById(R.id.display_cafe);



       try{
        if(!getIntent().getExtras().getString("id").isEmpty()){
            id = getIntent().getExtras().getString("id").trim();
            progressBar.setVisibility(View.VISIBLE);
           // relativeLayout.setVisibility(View.INVISIBLE);



            JsonObject json = new JsonObject();
            json.addProperty("token", SessionManager.getjwt(CafeDisplayActivity.this));
            Ion.with(this)
                    .load("GET", getString(R.string.url).concat("cafe/"+id+"/"))
                    .setHeader("x-access-token",SessionManager.getjwt(CafeDisplayActivity.this))
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            // do stuff with the result or error
                            try {
                                String name = result.get("name").getAsString();
                                String owner = result.get("owner").getAsString();
                                String owner_photo=result.get("owner_photo").getAsString();
                                JsonArray cafe_photo = result.get("photos").getAsJsonArray();
                                String state_ = result.get("state").getAsString();
                                String city_ = result.get("city").getAsString();
                                String address_ = result.get("address").getAsString();
                                Number pincode_ = result.get("pin_code").getAsNumber();
                                String phone = result.get("phone").getAsString();
                                String lat_ = result.get("lat").getAsString();
                                String lng_ = result.get("lng").getAsString();
                                String cafe_status= result.get("cafe_status").getAsString();
                                String hardware_given = result.get("hardware_given").getAsString();
                                JsonArray agreement_photo = result.get("agreement_photos").getAsJsonArray();


                                cafename.setText(name);
                                if(!owner.equals(""))
                                ownername.setText(owner);
                                if(!state_.equals(""))
                                state.setText(state_);
                                if(!city_.equals("")) {
                                    city.setText(city_);
                                    city_above.setText(city_);
                                }
                                if(!pincode_.toString().equals(""))
                                    pincode.setText(pincode_.toString());
                                if (!address_.equals(""))
                                address.setText(address_);
                                if (!phone.equals(""))
                                phone_number.setText(phone);
                                if (!hardware_given.equals(""))
                                hardware.setText(hardware_given);
                                if (!cafe_status.equals(""))
                                cafestatus.setText(cafe_status);
                                lat =(lat_);
                                lng =(lng_);


                                relativeLayout.setVisibility(View.VISIBLE);

                                progressBar.setVisibility(View.GONE);
                                for(int i = 0;  i<cafe_photo.size(); i++)
                                {
                                    cafephotoaddress.add(getString(R.string.media_url).concat(cafe_photo.get(i).getAsString()));
                                    cafephotoupload.add(true);
                                    Log.i("photos","added");
                                }
                                for(int i = 0;  i<agreement_photo.size(); i++)
                                {
                                    agreementphotoaddress.add(getString(R.string.media_url).concat(agreement_photo.get(i).getAsString()));
                                    agreementphotoupload.add(true);
                                }
                                if(!owner_photo.equals("")){
                                    owner_photo_url = getString(R.string.media_url).concat(owner_photo);
                                    Glide.with(CafeDisplayActivity.this).load(owner_photo_url)
                                            .thumbnail(0.5f)
                                            .crossFade()
                                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                            .into(ownerphoto);

                                }


                                if(cafephotoaddress.size() > 0)
                                {
                                    horizontalAdapter = new HorizontalAdapter(CafeDisplayActivity.this, cafephotoaddress, "cafe", cafephotoupload);
                                    LinearLayoutManager horizontalLayoutManagaer
                                            = new LinearLayoutManager(CafeDisplayActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                    cafe_photos.setLayoutManager(horizontalLayoutManagaer);
                                    cafe_photos.setAdapter(horizontalAdapter);
                                    cafe_photos.setVisibility(View.VISIBLE);
                                    number_of_cafe_photos.setText(cafephotoaddress.size() + " photos added");

                                    Glide.with(CafeDisplayActivity.this).load(cafephotoaddress.get(0))
                                            .thumbnail(0.5f)
                                            .crossFade()
                                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                            .into(cafephoto);


                                }
                                else
                                {
                                    number_of_cafe_photos.setText( "0 cafe photos added");

                                }

                                if(agreementphotoaddress.size() > 0)
                                {
                                    horizontalAdapter = new HorizontalAdapter(CafeDisplayActivity.this, agreementphotoaddress, "agreement", agreementphotoupload);
                                    LinearLayoutManager horizontalLayoutManagaer
                                            = new LinearLayoutManager(CafeDisplayActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                    agreement_photos.setLayoutManager(horizontalLayoutManagaer);
                                    agreement_photos.setAdapter(horizontalAdapter);
                                    agreement_photos.setVisibility(View.VISIBLE);
                                    number_of_agreement_photos.setText(agreementphotoaddress.size() + " photos added");

                                }
                                else
                                {
                                    number_of_agreement_photos.setText("0 agreement photos added");

                                }
                            }
                            catch (NullPointerException e1)
                            {

                                Toast.makeText(CafeDisplayActivity.this,"Failed, Try again later", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                Log.i("EXCEPTION",e1.getMessage());

                            }

                        }
                    });

        }
       }
       catch (NullPointerException e1)
       {
           Log.i("Null Exception id",e1.getMessage());
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


    public void addphotos(View v) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose type to Add photos");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CafeDisplayActivity.this, android.R.layout.select_dialog_item);
        arrayAdapter.add("Cafe Photos");
        arrayAdapter.add("Agreement Photos");

        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                typeofphotos = arrayAdapter.getItem(i);
                int j = type(typeofphotos);
                dialogInterface.dismiss();
                dialogInterface.cancel();

                if(Build.VERSION.SDK_INT >= 23) {
                    getPermissionToReadExternalStorage();
                }
                    if (ContextCompat.checkSelfPermission(CafeDisplayActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        Intent intent = new Intent(CafeDisplayActivity.this, GalleryActivity.class);
                        intent.putExtra("number","10");
                        startActivityForResult(intent, j);
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


    public void editcafe(View v){
        Intent intent = new Intent(CafeDisplayActivity.this, EditCafeActivity.class);
        intent.putExtra("cafename",cafename.getText().toString().trim());
        intent.putExtra("ownername", ownername.getText().toString().trim());
        intent.putExtra("phonenumber", phone_number.getText().toString().trim());
        intent.putExtra("pincode", pincode.getText().toString().trim());
        intent.putExtra("city", city.getText().toString().trim());
        intent.putExtra("state", state.getText().toString().trim());
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        intent.putExtra("hardware", hardware.getText().toString().trim());
        intent.putExtra("address",address.getText().toString().trim());
        int cafe_status = status(cafestatus.getText().toString().trim());
        intent.putExtra("cafestatus",cafe_status);
        intent.putExtra("id",id);

        startActivity(intent);
        finish();
    }

    public int type(String str) {
        switch (str) {
            case "Cafe Photos":
                return 201;

            case "Agreement Photos":
                return 202;

            default:
                return 0;

        }
    }

    public int status(String str) {
        switch (str) {
            case "Waiting Approach":
                return 0;

            case "Accepted – Demo Scheduled":
                return 1;

            case "Demo Done – Trial Start":
                return 2;
            case "Rejected":
                return 3;
            case "Running":
                return 4;
            default:
                return 0;

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CafePhotos) {
                photoaddress = new ArrayList<>();
                photoaddress = data.getStringArrayListExtra("photosurl");

                for (int i = 0; i < photoaddress.size(); i++) {
                    cafephotoaddress.add(photoaddress.get(i));
                    cafephotoupload.add(false);
                }
                horizontalAdapter = new HorizontalAdapter(CafeDisplayActivity.this, cafephotoaddress, "cafe", cafephotoupload);
                LinearLayoutManager horizontalLayoutManagaer
                        = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                cafe_photos.setLayoutManager(horizontalLayoutManagaer);
                cafe_photos.setAdapter(horizontalAdapter);
                cafe_photos.setVisibility(View.VISIBLE);
                number_of_cafe_photos.setText(cafephotoaddress.size() + " photos added");

                for (int i = 0; i < photoaddress.size(); i++) {
                    final int j = i;
                    final int k = cafephotoaddress.size() - photoaddress.size();
                    Ion.with(this)
                            .load("POST", getString(R.string.url).concat("photo/"))
                            .setHeader("x-access-token", SessionManager.getjwt(CafeDisplayActivity.this))
                            .setMultipartParameter("type", "cafe")
                            .setMultipartParameter("photo_type", "photos")
                            .setMultipartParameter("_id", id)
                            .setMultipartFile("image", "image/jpeg", new File(photoaddress.get(i)))
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {

                                    horizontalAdapter.uploadcomplete(k + j);
                                    //Log.i("Cafe Photo", (k + j) + "");
                                }
                            });
                }
            } else if (requestCode == AgreementPhotos) {
                photoaddress = new ArrayList<>();
                photoaddress = data.getStringArrayListExtra("photosurl");

                for (int i = 0; i < photoaddress.size(); i++) {
                    agreementphotoaddress.add(photoaddress.get(i));
                    agreementphotoupload.add(false);
                }
                horizontalAdapter = new HorizontalAdapter(CafeDisplayActivity.this, agreementphotoaddress, "agreement", agreementphotoupload);
                final LinearLayoutManager horizontalLayoutManagaer
                        = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                agreement_photos.setLayoutManager(horizontalLayoutManagaer);
                agreement_photos.setAdapter(horizontalAdapter);
                agreement_photos.setVisibility(View.VISIBLE);
                number_of_agreement_photos.setText(agreementphotoaddress.size() + " photos added");

                for (int i = 0; i < photoaddress.size(); i++) {
                    final int j = i;
                    final int k = agreementphotoaddress.size() - photoaddress.size();

                    Ion.with(this)
                            .load("POST", getString(R.string.url).concat("photo/"))
                            .setHeader("x-access-token", SessionManager.getjwt(CafeDisplayActivity.this))
                            .setMultipartParameter("type", "cafe")
                            .setMultipartParameter("photo_type", "agreement_photos")
                            .setMultipartParameter("_id", id)
                            .setMultipartFile("image", "image/jpeg", new File(photoaddress.get(i)))
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    horizontalAdapter.uploadcomplete(k + j);
                                }
                            });
                }

            } else if (requestCode == OwnerPhoto) {
                photoaddress = new ArrayList<>();
                photoaddress = data.getStringArrayListExtra("photosurl");


                Glide.with(CafeDisplayActivity.this).load(photoaddress.get(0))
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(ownerphoto);


                Ion.with(this)
                        .load("POST", getString(R.string.url).concat("photo/"))
                        .setHeader("x-access-token", SessionManager.getjwt(CafeDisplayActivity.this))
                        .setMultipartParameter("type", "cafe")
                        .setMultipartParameter("photo_type", "owner_photo")
                        .setMultipartParameter("_id", id)
                        .setMultipartFile("image", "image/jpeg", new File(photoaddress.get(0)))
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {

                            }
                        });
            }



        }
    }

    public void viewonmap(View view) {

        if(!lat.equals("") && !lng.equals("")){
        if (Build.VERSION.SDK_INT >= 23) {
            getPermissionToAccessFineLocation();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (gps) {
                Intent intent = new Intent(CafeDisplayActivity.this, LocationDisplayActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Turn on the GPS first", Toast.LENGTH_LONG).show();
            }
        }}
        else
        {

            Toast.makeText(this, "Location not marked. Mark it by editing", Toast.LENGTH_LONG).show();

        }
    }



    private static final int ACCESS_FINE_LOCATION = 2;
    private static final int READ_EXTERNAL_STORAGE = 1;


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


    public void lastphoto(String type)
    {
        if(type.equals("cafe"))
        {
            cafe_photos.setVisibility(View.GONE);
        }
        else if(type.equals("agreement"))
        {
            agreement_photos.setVisibility(View.GONE);
        }
    }

    public void numberofphotochanged(String type, int number)
    {
        if(type.equals("cafe"))
        {
            number_of_cafe_photos.setText(number +" photos added");
        }
        else if(type.equals("agreement"))
        {
            number_of_agreement_photos.setText(number +" photos added");
        }
    }

    public void ownerphoto(View view) {
        Intent intent = new Intent(CafeDisplayActivity.this, GalleryActivity.class);
        intent.putExtra("number","1");
        startActivityForResult(intent, OwnerPhoto);
    }

    public void browsemeetings(View view) {
        if (isNetworkAvailable()) {
            Intent intent = new Intent(CafeDisplayActivity.this, MeetingActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
        else {
            Toast.makeText(CafeDisplayActivity.this, "Connect to Internet and Try again", Toast.LENGTH_LONG).show();
        }

        }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
