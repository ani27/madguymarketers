package com.vp6.anish.madguysmarketers;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng markedlocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
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

        // Add a marker in Sydney and move the camera

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if((ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED))
        {
            Log.i("MAP","inside onconnected");
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
                //Toast.makeText(this, mLastLocation.getLatitude() + " " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                LatLng current_location = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());//(-34, 151);
                mMap.addMarker(new MarkerOptions().position(current_location).title("current Location"));
                markedlocation = current_location;
                mMap.setMinZoomPreference(6.0f);
                mMap.setMaxZoomPreference(20.0f);
                mMap.getMaxZoomLevel();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(current_location));
                Log.i("MAP","inside map");
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Location"));
                        markedlocation=latLng;
                        Log.i("MAP","inside on click listner");
                    }
                });

            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("MAP","inside onconnectionsuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("MAP","inside onconnectionfailed");
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        Log.i("MAP","inside on start");
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        Log.i("MAP","inside onstop");
    }


    public void sendmaplocation(View view) {
        Intent intent = new Intent();
        try {
            intent.putExtra("Latitude", markedlocation.latitude + "");
            intent.putExtra("Longitude", markedlocation.longitude + "");
        }
        catch (NullPointerException ex){
            Toast.makeText(MapsActivity.this, "Please Select a Location", Toast.LENGTH_LONG).show();
        }
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra("Latitude", 0+"");
        intent.putExtra("Longitude", 0+"");
        finish();
    }


}
