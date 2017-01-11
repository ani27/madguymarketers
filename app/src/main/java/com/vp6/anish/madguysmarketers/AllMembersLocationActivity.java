package com.vp6.anish.madguysmarketers;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class AllMembersLocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_members_location);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            Log.i("MAP", "inside googleapiclient");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {


                LinearLayout info = new LinearLayout(AllMembersLocationActivity.this);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(AllMembersLocationActivity.this);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setMaxLines(3);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(AllMembersLocationActivity.this);
                snippet.setTextColor(Color.GRAY);
                snippet.setMaxLines(3);
                snippet.setText(marker.getSnippet());


                info.addView(title);
                info.addView(snippet);


                return info;
            }
        });
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Ion.with(this)
                .load("POST", "http://madguylab.com/partner/auto_apps/get_our_members.php")
                .setHeader("x-access-token", SessionManager.getjwt(this))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        JsonArray users = result.get("users").getAsJsonArray();
                        for (int i = 0; i < users.size(); i++) {
                            JsonObject user = users.get(i).getAsJsonObject();
                            LatLng current_location = new LatLng(Double.parseDouble(user.get("latitude").getAsString()),Double.parseDouble(user.get("longitude").getAsString()));

                            mMap.addMarker(new MarkerOptions()
                                    .position(current_location)
                                    .title(user.get("user_name").getAsString() + "\n" + user.get("user_number").getAsString())
                                    .snippet(user.get("last_update").getAsString())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_map_icon)));
                            //(-34, 151);

                            if (i == users.size() - 1) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_location, 6.0f));
                            }

                        }

                    }


                });

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
}
