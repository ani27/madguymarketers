package com.vp6.anish.madguysmarketers;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class LocationDisplayActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    String lat;
    String lng;
    ArrayList<String>lat_list;
    ArrayList<String>lng_list;
    ArrayList<String>stat_list;
    ArrayList<String>name_list;
    ArrayList<String>address_list;
    ArrayList<String>type_list;
    ArrayList<String>id_list;
    boolean single = true;
    HashMap<LatLng, String>idMarkerMap;
    HashMap<LatLng, String>typeMarkerMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_display);
        //getActionBar().setTitle("Location");
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        idMarkerMap = new HashMap<LatLng, String>();
        typeMarkerMap = new HashMap<LatLng, String>();
        Log.i("Present", "Location display");
        try {
            lat = getIntent().getExtras().getString("lat");
            lng = getIntent().getExtras().getString("lng");

        } catch (NullPointerException e1) {
            lat = "0";
            lng = "0";
            Log.i("Single Exception", e1.getMessage());
            single = false;
        }

        try{
            lat_list= getIntent().getExtras().getStringArrayList("lat_list");
            lng_list= getIntent().getExtras().getStringArrayList("lng_list");
            stat_list= getIntent().getExtras().getStringArrayList("status_list");
            type_list= getIntent().getExtras().getStringArrayList("type_list");
            name_list= getIntent().getExtras().getStringArrayList("name_list");
            address_list= getIntent().getExtras().getStringArrayList("address_list");
            id_list= getIntent().getExtras().getStringArrayList("id_list");

        }catch (NullPointerException e1){
            lat_list = new ArrayList<>();
            lng_list = new ArrayList<>();
            stat_list = new ArrayList<>();
            address_list = new ArrayList<>();
            id_list = new ArrayList<>();
            name_list = new ArrayList<>();
            single = true;
            Log.i("List Exception", e1.getMessage());
        }

        if (lat == null){
            single = false;
        }
        else{
            single = true;
        }

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            Log.i("MAP", "inside googleapiclient");
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (!single) {
                    String id = idMarkerMap.get(marker.getPosition());
                    String type = typeMarkerMap.get(marker.getPosition());
                    click(id, type);
                }
            }
        });
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {


                LinearLayout info = new LinearLayout(LocationDisplayActivity.this);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(LocationDisplayActivity.this);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setMaxLines(3);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(LocationDisplayActivity.this);
                snippet.setTextColor(Color.GRAY);
                snippet.setMaxLines(3);
                snippet.setText(marker.getSnippet());


                info.addView(title);
                info.addView(snippet);


                return info;
            }
        });
        if (single) {
            // Add a marker in Sydney and move the camera
            LatLng location = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            mMap.addMarker(new MarkerOptions().position(location).title("Marked Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10.0f));
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);

        }
        else{
            for (int i = 0; i<lat_list.size(); i++){
                //int status;
                if (lat_list.get(i).equals("") && lng_list.get(i).equals("")){

                }
                else {
                    LatLng location = new LatLng(Double.parseDouble(lat_list.get(i)), Double.parseDouble(lng_list.get(i)));

                    if (type_list.get(i).trim().toString().equals("cafe")){


                        int status = status(stat_list.get(i));
                        if (status == 0)
                        mMap.addMarker(new MarkerOptions()
                                .position(location)
                                .title(name_list.get(i) + "\n" +stat_list.get(i))
                                .snippet(address_list.get(i))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.cafe_zero)));
                        else if (status ==1)
                            mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(name_list.get(i) + "\n" +stat_list.get(i))
                                    .snippet(address_list.get(i))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.cafe_one)));
                        else if (status == 2)
                            mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(name_list.get(i) + "\n" +stat_list.get(i))
                                    .snippet(address_list.get(i))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.cafe_two)));

                        else if (status == 3)
                            mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(name_list.get(i) + "\n" +stat_list.get(i))
                                    .snippet(address_list.get(i))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.cafe_three)));

                        else if (status == 4)
                            mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(name_list.get(i) + "\n" +stat_list.get(i))
                                    .snippet(address_list.get(i))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.cafe_four)));
                        else{
                            mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.cafe_zero)));
                        }



                    }
                    else{

                        int status = status(stat_list.get(i));
                        if (status == 0)
                            mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(name_list.get(i) + "\n" +stat_list.get(i))
                                    .snippet(address_list.get(i))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.coaching_zero)));
                        else if (status ==1)
                            mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(name_list.get(i) + "\n" +stat_list.get(i))
                                    .snippet(address_list.get(i))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.coaching_one)));
                        else if (status == 2)
                            mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(name_list.get(i) + "\n" +stat_list.get(i))
                                    .snippet(address_list.get(i))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.coaching_two)));
                        else if (status == 3)
                            mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(name_list.get(i) + "\n" +stat_list.get(i))
                                    .snippet(address_list.get(i))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.coaching_three)));
                        else if (status == 4)
                            mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(name_list.get(i) + "\n" +stat_list.get(i))
                                    .snippet(address_list.get(i))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.coaching_four)));
                        else{
                            mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.coaching_zero)));
                        }
                    }

                    idMarkerMap.put(location,id_list.get(i));
                   typeMarkerMap.put(location,type_list.get(i));

                    if (i == lat_list.size() - 1) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 6.0f));
                    }

                }
            }

            mMap.setMyLocationEnabled(true);

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
    @Override
    public void onBackPressed(){
        this.finish();
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


    public void click(String id, String type){
        if(type.equals("cafe")){
            Intent intent = new Intent(this,CafeDisplayActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
        else if (type.equals("coaching"))
        {
            Intent intent = new Intent(this,CoachingDisplayActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this,"failed, try later", Toast.LENGTH_SHORT).show();
        }
    }


}
