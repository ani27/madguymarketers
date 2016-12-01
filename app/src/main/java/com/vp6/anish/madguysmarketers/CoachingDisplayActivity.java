package com.vp6.anish.madguysmarketers;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class CoachingDisplayActivity extends AppCompatActivity {

    String typeofphotos;
    int CoachingPhotos = 201;
    int AgreementPhotos = 202;
    int StudentlistPhotos = 203;
    int CoachingRegistrationPhotos = 204;
    int DirectorPhotos = 205;
    ArrayList<String> photoaddress;
    ArrayList<String> coachingphotoaddress;
    ArrayList<String> coachingregistrationphotoaddress;
    ArrayList<String> studentlistphotoaddress;
    ArrayList<String> agreementphotoaddress;
    ArrayList<Boolean> coachingphotoupload;
    ArrayList<Boolean> coachingregistrationphotoupload;
    ArrayList<Boolean> studentlistphotoupload;
    ArrayList<Boolean> agreementphotoupload;

    HorizontalAdapter horizontalAdapter;
    RecyclerView coaching_photos;
    RecyclerView agreement_photos;
    RecyclerView studentlist_photos;
    RecyclerView coaching_registration_photos;
    TextView coachingname;
    TextView directorname;
    TextView phone_number;
    TextView pincode;
    TextView city;
    TextView state;
    String  lat;
    String  lng;
    TextView hardware;
    TextView coachingstatus;
    TextView address;
    TextView total_students;
    TextView students_having_mobile;
    TextView examteaching;
    TextView number_of_coaching_photos;
    TextView number_of_agreement_photos;
    TextView number_of_studentlist_photos;
    TextView number_of_coaching_registration_photos;
    ImageView director_photo_img;
    String director_photo_url = "";

    ProgressBar progressBar;
    RelativeLayout relativeLayout;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coaching_display);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Coaching");
        coachingphotoaddress = new ArrayList<>();
        agreementphotoaddress = new ArrayList<>();
        coachingregistrationphotoaddress = new ArrayList<>();
        studentlistphotoaddress = new ArrayList<>();
        coachingphotoupload = new ArrayList<>();
        agreementphotoupload = new ArrayList<>();
        studentlistphotoupload = new ArrayList<>();
        coachingregistrationphotoupload = new ArrayList<>();
        coaching_photos = (RecyclerView)findViewById(R.id.coaching_photo_recyclerview);
        agreement_photos= (RecyclerView)findViewById(R.id.partnership_agreement_photo_recyclerview);
        studentlist_photos = (RecyclerView)findViewById(R.id.studentlist_photo_recyclerview);
        coaching_registration_photos = (RecyclerView)findViewById(R.id.registration_photo_recyclerview);

        coachingname= (TextView)findViewById(R.id.coaching_name_display);
        directorname = (TextView)findViewById(R.id.director_name_display);
        phone_number  = (TextView)findViewById(R.id.phone_number_display);
        pincode = (TextView)findViewById(R.id.pincode_display);
        city  = (TextView)findViewById(R.id.city_diplay);
        state  = (TextView)findViewById(R.id.state_display);
        hardware  = (TextView)findViewById(R.id.hardware_display);
        coachingstatus  = (TextView)findViewById(R.id.coachingstatus_display);
        address = (TextView)findViewById(R.id.address_display);
        number_of_agreement_photos = (TextView)findViewById(R.id.partnership_agreement_photo_number);
        number_of_coaching_photos = (TextView)findViewById(R.id.coaching_photo_number);
        number_of_coaching_registration_photos = (TextView)findViewById(R.id.coaching_photo_number);
        number_of_studentlist_photos = (TextView)findViewById(R.id.studentlist_photo_number);
        total_students = (TextView)findViewById(R.id.total_student_display);
        students_having_mobile = (TextView)findViewById(R.id.total_students_having_mobile_phone_display);
        examteaching = (TextView)findViewById(R.id.exam_teaching_display);

        director_photo_img = (ImageView)findViewById(R.id.director_photo2);
        progressBar = (ProgressBar)findViewById(R.id.progressBar_display_coaching);
        relativeLayout = (RelativeLayout)findViewById(R.id.display_coaching);


        try{
            if(!getIntent().getExtras().getString("id").isEmpty()){
                id = getIntent().getExtras().getString("id").trim();
                progressBar.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.INVISIBLE);



                JsonObject json = new JsonObject();
                json.addProperty("token", SessionManager.getjwt(CoachingDisplayActivity.this));
                Ion.with(this)
                        .load("GET", getString(R.string.url).concat("coaching/"+id+"/"))
                        .setHeader("x-access-token",SessionManager.getjwt(CoachingDisplayActivity.this))
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(
                                new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                // do stuff with the result or error
                                try {
                                    String name = result.get("name").getAsString();
                                    String director = result.get("director").getAsString();
                                    String director_photo=result.get("director_photo").getAsString();
                                    JsonArray coaching_photo = result.get("photos").getAsJsonArray();
                                    String state_ = result.get("state").getAsString();
                                    String city_ = result.get("city").getAsString();
                                    String address_ = result.get("address").getAsString();
                                    Number pincode_ = result.get("pin_code").getAsNumber();
                                    String phone = result.get("phone").getAsString();
                                    String lat_ = result.get("lat").getAsString();
                                    String lng_ = result.get("lng").getAsString();
                                    String coaching_status= result.get("coaching_status").getAsString();
                                    String hardware_given = result.get("hardware_given").getAsString();
                                    String exam_teaching = result.get("exams_teaching").getAsString();
                                    Number totalstudent = result.get("total_students").getAsNumber();
                                    Number totalstudentshavingphone = result.get("students_having_mobile").getAsNumber();
                                    JsonArray agreement_photo = result.get("agreement_photos").getAsJsonArray();
                                    JsonArray student_list_photo = result.get("students_list_photos").getAsJsonArray();
                                    JsonArray registration_photo = result.get("registration_photos").getAsJsonArray();


                                    // int i = listings.size();


                                    coachingname.setText(name);
                                    directorname.setText(director);
                                    state.setText(state_);
                                    city.setText(city_);
                                    if(!pincode_.toString().equals("0"))
                                    pincode.setText(pincode_.toString());
                                    else
                                    pincode.setText("");
                                    address.setText(address_);
                                    phone_number.setText(phone);
                                    hardware.setText(hardware_given);
                                    coachingstatus.setText(coaching_status);
                                    lat =(lat_);
                                    lng =(lng_);
                                    examteaching.setText(exam_teaching);
                                    if(!totalstudent.toString().equals("0"))
                                    total_students.setText(totalstudent.toString());
                                    else
                                    total_students.setText("");
                                    if(!totalstudentshavingphone.toString().equals(""))
                                    students_having_mobile.setText(totalstudentshavingphone.toString());
                                    else
                                    students_having_mobile.setText("");



                                    relativeLayout.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);

                                    for(int i = 0;  i<coaching_photo.size(); i++)
                                    {
                                        coachingphotoaddress.add(getString(R.string.media_url).concat(coaching_photo.get(i).getAsString()));
                                        coachingphotoupload.add(true);
                                    }


                                    for(int i = 0;  i<agreement_photo.size(); i++)
                                    {
                                        agreementphotoaddress.add(getString(R.string.media_url).concat(agreement_photo.get(i).getAsString()));
                                        agreementphotoupload.add(true);
                                    }

                                    for(int i = 0;  i<student_list_photo.size(); i++)
                                    {
                                        studentlistphotoaddress.add(getString(R.string.media_url).concat(student_list_photo.get(i).getAsString()));
                                        studentlistphotoupload.add(true);
                                    }

                                    for(int i = 0;  i<registration_photo.size(); i++)
                                    {
                                        coachingregistrationphotoaddress.add("http://192.168.124.106:3000/media/".concat(registration_photo.get(i).getAsString()));
                                        coachingregistrationphotoupload.add(true);
                                    }

                                    if(!director_photo.equals("")){
                                        director_photo_url = "http://192.168.124.106:3000/media/".concat(director_photo);
                                        Glide.with(CoachingDisplayActivity.this).load(director_photo_url)
                                                .thumbnail(0.5f)
                                                .crossFade()
                                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                                .into(director_photo_img);

                                    }



                                    if(coachingphotoaddress.size() > 0)
                                    {
                                        horizontalAdapter = new HorizontalAdapter(CoachingDisplayActivity.this, coachingphotoaddress, "coaching", coachingphotoupload);
                                        LinearLayoutManager horizontalLayoutManagaer
                                                = new LinearLayoutManager(CoachingDisplayActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                        coaching_photos.setLayoutManager(horizontalLayoutManagaer);
                                        coaching_photos.setAdapter(horizontalAdapter);
                                        coaching_photos.setVisibility(View.VISIBLE);
                                        number_of_coaching_photos.setText(coachingphotoaddress.size() + " photos added");

                                    }

                                    if(agreementphotoaddress.size() > 0)
                                    {
                                        horizontalAdapter = new HorizontalAdapter(CoachingDisplayActivity.this, agreementphotoaddress, "agreement", agreementphotoupload);
                                        LinearLayoutManager horizontalLayoutManagaer
                                                = new LinearLayoutManager(CoachingDisplayActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                        agreement_photos.setLayoutManager(horizontalLayoutManagaer);
                                        agreement_photos.setAdapter(horizontalAdapter);
                                        agreement_photos.setVisibility(View.VISIBLE);
                                        number_of_agreement_photos.setText(agreementphotoaddress.size() + " photos added");

                                    }


                                    if(coachingregistrationphotoaddress.size() > 0)
                                    {
                                        horizontalAdapter = new HorizontalAdapter(CoachingDisplayActivity.this, coachingregistrationphotoaddress, "registration", coachingregistrationphotoupload);
                                        LinearLayoutManager horizontalLayoutManagaer
                                                = new LinearLayoutManager(CoachingDisplayActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                        coaching_registration_photos.setLayoutManager(horizontalLayoutManagaer);
                                        coaching_registration_photos.setAdapter(horizontalAdapter);
                                        coaching_registration_photos.setVisibility(View.VISIBLE);
                                        number_of_coaching_registration_photos.setText(coachingregistrationphotoaddress.size() + " photos added");

                                    }


                                    if(studentlistphotoaddress.size() > 0)
                                    {
                                        horizontalAdapter = new HorizontalAdapter(CoachingDisplayActivity.this, studentlistphotoaddress, "student_lsit", studentlistphotoupload);
                                        LinearLayoutManager horizontalLayoutManagaer
                                                = new LinearLayoutManager(CoachingDisplayActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                        studentlist_photos.setLayoutManager(horizontalLayoutManagaer);
                                        studentlist_photos.setAdapter(horizontalAdapter);
                                        studentlist_photos.setVisibility(View.VISIBLE);
                                        number_of_studentlist_photos.setText(studentlistphotoaddress.size() + " photos added");

                                    }



                                }
                                catch (NullPointerException e1)
                                {

                                    Toast.makeText(CoachingDisplayActivity.this,"Failed, Try again later", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                    relativeLayout.setVisibility(View.GONE);

                                    Log.i("EXCEPTION",e1.getMessage());

                                }


                            }
                        });

            }
        }
        catch (NullPointerException e1)
        {
            Log.i("Null Exception id",e1.getMessage());
            Toast.makeText(CoachingDisplayActivity.this,"Failed, Try again later", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.GONE);
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

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CoachingDisplayActivity.this, android.R.layout.select_dialog_item);
        arrayAdapter.add("Coaching Photos");
        arrayAdapter.add("Agreement Photos");
        arrayAdapter.add("Student List Photos");
        arrayAdapter.add("Coaching Registration Photos");

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
                    if (ContextCompat.checkSelfPermission(CoachingDisplayActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        Intent intent = new Intent(CoachingDisplayActivity.this, GalleryActivity.class);
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




    public int type(String str) {
        switch (str) {
            case "Coaching Photos":
                return 201;

            case "Agreement Photos":
                return 202;

            case "Student List Photos":
                return 203;
            case "Coaching Registration Photos":
                return 204;

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
            if (requestCode == CoachingPhotos) {
                photoaddress = new ArrayList<>();
                photoaddress = data.getStringArrayListExtra("photosurl");

                for (int i = 0; i < photoaddress.size(); i++) {
                    coachingphotoaddress.add(photoaddress.get(i));
                    coachingphotoupload.add(false);
                }
                horizontalAdapter = new HorizontalAdapter(CoachingDisplayActivity.this, coachingphotoaddress, "coaching",coachingphotoupload);
                LinearLayoutManager horizontalLayoutManagaer
                        = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                coaching_photos.setLayoutManager(horizontalLayoutManagaer);
                coaching_photos.setAdapter(horizontalAdapter);
                coaching_photos.setVisibility(View.VISIBLE);
                number_of_coaching_photos.setText(coachingphotoaddress.size() + " photos added");

                for(int i = 0; i<photoaddress.size(); i++) {
                    final int j =i;
                    final int k = coachingphotoaddress.size() - photoaddress.size();

                    Ion.with(this)
                            .load("POST", getString(R.string.url).concat("photo/"))
                            .setHeader("x-access-token", SessionManager.getjwt(CoachingDisplayActivity.this))
                            .setMultipartParameter("type", "coaching")
                            .setMultipartParameter("photo_type", "photos")
                            .setMultipartParameter("_id",id)
                            .setMultipartFile("image","image/jpeg", new File(photoaddress.get(i)))
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                  horizontalAdapter.uploadcomplete(k+j);
                                }
                            });
                }
            }
            else if (requestCode == AgreementPhotos) {
                photoaddress = new ArrayList<>();
                photoaddress = data.getStringArrayListExtra("photosurl");

                for (int i = 0; i < photoaddress.size(); i++) {
                    agreementphotoaddress.add(photoaddress.get(i));
                    agreementphotoupload.add(false);
                }
                horizontalAdapter = new HorizontalAdapter(CoachingDisplayActivity.this, agreementphotoaddress, "agreement",agreementphotoupload);
                LinearLayoutManager horizontalLayoutManagaer
                        = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                agreement_photos.setLayoutManager(horizontalLayoutManagaer);
                agreement_photos.setAdapter(horizontalAdapter);
                agreement_photos.setVisibility(View.VISIBLE);
                number_of_agreement_photos.setText(agreementphotoaddress.size() + " photos added");

                for(int i = 0; i<photoaddress.size(); i++) {
                    final int j =i;
                    final int k = agreementphotoaddress.size() - photoaddress.size();

                    Ion.with(this)
                            .load("POST", getString(R.string.url).concat("photo/"))
                            .setHeader("x-access-token", SessionManager.getjwt(CoachingDisplayActivity.this))
                            .setMultipartParameter("type", "coaching")
                            .setMultipartParameter("photo_type", "agreement_photos")
                            .setMultipartParameter("_id",id)
                            .setMultipartFile("image","image/jpeg", new File(photoaddress.get(i)))
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    horizontalAdapter.uploadcomplete(k+j);
                                }
                            });
                }

            }
            else if (requestCode == StudentlistPhotos) {
                photoaddress = new ArrayList<>();
                photoaddress = data.getStringArrayListExtra("photosurl");

                for (int i = 0; i < photoaddress.size(); i++) {
                    studentlistphotoaddress.add(photoaddress.get(i));
                    studentlistphotoupload.add(false);
                }
                horizontalAdapter = new HorizontalAdapter(CoachingDisplayActivity.this, studentlistphotoaddress, "student_list",studentlistphotoupload);
                LinearLayoutManager horizontalLayoutManagaer
                        = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                studentlist_photos.setLayoutManager(horizontalLayoutManagaer);
                studentlist_photos.setAdapter(horizontalAdapter);
                studentlist_photos.setVisibility(View.VISIBLE);
                number_of_studentlist_photos.setText(studentlistphotoaddress.size() + " photos added");

                for(int i = 0; i<photoaddress.size(); i++) {
                    final int j =i;
                    final int k = studentlistphotoaddress.size() - photoaddress.size();

                    Ion.with(this)
                            .load("POST", getString(R.string.url).concat("photo/"))
                            .setHeader("x-access-token", SessionManager.getjwt(CoachingDisplayActivity.this))
                            .setMultipartParameter("type", "coaching")
                            .setMultipartParameter("photo_type", "students_list_photos")
                            .setMultipartParameter("_id",id)
                            .setMultipartFile("image","image/jpeg", new File(photoaddress.get(i)))
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                 horizontalAdapter.uploadcomplete(k+j);
                                }
                            });
                }

            }
            else if (requestCode == CoachingRegistrationPhotos) {
                photoaddress = new ArrayList<>();
                photoaddress = data.getStringArrayListExtra("photosurl");

                for (int i = 0; i < photoaddress.size(); i++) {
                    coachingregistrationphotoaddress.add(photoaddress.get(i));
                    coachingphotoupload.add(false);
                }
                horizontalAdapter = new HorizontalAdapter(CoachingDisplayActivity.this, coachingregistrationphotoaddress, "registration",coachingphotoupload);
                LinearLayoutManager horizontalLayoutManagaer
                        = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                coaching_registration_photos.setLayoutManager(horizontalLayoutManagaer);
                coaching_registration_photos.setAdapter(horizontalAdapter);
                coaching_registration_photos.setVisibility(View.VISIBLE);
                number_of_coaching_registration_photos.setText(coachingregistrationphotoaddress.size() + " photos added");

                for(int i = 0; i<photoaddress.size(); i++) {
                    final int j =i;
                    final int k = coachingregistrationphotoaddress.size() - photoaddress.size();

                    Ion.with(this)
                            .load("POST", getString(R.string.url).concat("photo/"))
                            .setHeader("x-access-token", SessionManager.getjwt(CoachingDisplayActivity.this))
                            .setMultipartParameter("type", "coaching")
                            .setMultipartParameter("photo_type", "registration_photos")
                            .setMultipartParameter("_id",id)
                            .setMultipartFile("image","image/jpeg", new File(photoaddress.get(i)))
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                  horizontalAdapter.uploadcomplete(k+j);
                                }
                            });
                }

            }

            else if (requestCode == DirectorPhotos) {
                photoaddress = new ArrayList<>();
                photoaddress = data.getStringArrayListExtra("photosurl");


                Glide.with(CoachingDisplayActivity.this).load(photoaddress.get(0))
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(director_photo_img);


                    Ion.with(this)
                            .load("POST", getString(R.string.url).concat("photo/"))
                            .setHeader("x-access-token", SessionManager.getjwt(CoachingDisplayActivity.this))
                            .setMultipartParameter("type", "coaching")
                            .setMultipartParameter("photo_type", "director_photo")
                            .setMultipartParameter("_id",id)
                            .setMultipartFile("image","image/jpeg", new File(photoaddress.get(0)))
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {

                                }
                            });


            }
        }
    }




    public void viewonmap_coaching(View view) {

        if(!lat.equals("") && !lng.equals("")){
        if (Build.VERSION.SDK_INT >= 23) {
            getPermissionToAccessFineLocation();
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (gps) {
                Intent intent = new Intent(CoachingDisplayActivity.this, LocationDisplayActivity.class);
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
        if(type.equals("coaching"))
        {
            coaching_photos.setVisibility(View.GONE);
        }
        else if(type.equals("agreement"))
        {
            agreement_photos.setVisibility(View.GONE);
        }
        else if (type.equals("student_list"))
        {
            studentlist_photos.setVisibility(View.GONE);

        }
        else if (type.equals("registration"))
        {
            coaching_registration_photos.setVisibility(View.GONE);
        }

    }

    public void numberofphotochanged(String type, int number)
    {

        if(type.equals("coaching"))
        {
            number_of_coaching_photos.setText(number +" photos added");
        }
        else if(type.equals("agreement"))
        {
            number_of_agreement_photos.setText(number +" photos added");
        }
        else if (type.equals("student_list"))
        {
            number_of_studentlist_photos.setText(number +" photos added");

        }
        else if (type.equals("registration"))
        {
            number_of_coaching_registration_photos.setText(number +" photos added");
        }
    }


    public void editcoaching(View view) {


        Intent intent = new Intent(CoachingDisplayActivity.this, EditCoachingActivity.class);
        intent.putExtra("coachingname",coachingname.getText().toString().trim());
        intent.putExtra("directorname", directorname.getText().toString().trim());
        intent.putExtra("phonenumber", phone_number.getText().toString().trim());
        intent.putExtra("pincode", pincode.getText().toString().trim());
        intent.putExtra("city", city.getText().toString().trim());
        intent.putExtra("state", state.getText().toString().trim());
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        intent.putExtra("hardware", hardware.getText().toString().trim());
        intent.putExtra("address",address.getText().toString().trim());
        int cafe_status = status(coachingstatus.getText().toString().trim());
        intent.putExtra("cafestatus",cafe_status);
        intent.putExtra("id",id);
        intent.putExtra("examteaching", examteaching.getText().toString().trim());
        intent.putExtra("totalstudents", total_students.getText().toString().trim());
        intent.putExtra("totalstudentshavingmobilephone", students_having_mobile.getText().toString().trim());

        startActivity(intent);
        finish();
    }

    public void directorphoto(View view) {
        Intent intent = new Intent(CoachingDisplayActivity.this, GalleryActivity.class);
        intent.putExtra("number","1");
        startActivityForResult(intent, DirectorPhotos);
    }

   public void browsemeetingscoaching(View view) {
        if (isNetworkAvailable()) {
            Intent intent = new Intent(CoachingDisplayActivity.this, MeetingActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
        else {
            Toast.makeText(CoachingDisplayActivity.this, "Connect to Internet and Try again", Toast.LENGTH_LONG).show();
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

