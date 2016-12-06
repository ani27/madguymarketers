package com.vp6.anish.madguysmarketers;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.graphics.drawable.GradientDrawable.LINE;

public class UserLocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    EditText date;
    Button showpath;
    String id = "";
    Calendar myCalendar;
    private ClusterManager<MyItem> mClusterManager;
    PolylineOptions polylineOptions;
    Polyline polyline;
     CheckedTextView radioButton;
    long distance_travelled;
    TextView distance;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);
        date = (EditText)findViewById(R.id.date);
        myCalendar = Calendar.getInstance();
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(UserLocationActivity.this, date_, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        showpath = (Button)findViewById(R.id.showpath);
        radioButton = (CheckedTextView)findViewById(R.id.checkedTextView);
        distance = (TextView)findViewById(R.id.user_travelled);
        radioButton.setSelected(false);
        id = getIntent().getExtras().getString("id");
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

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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

    public void showpath() {


        distance_travelled = 0;
        //distance_travelled =new Double(0.0);
        //decodedPath = PolyUtil.decode(LINE);
        polylineOptions = new PolylineOptions().width(5).color(R.color.colorAccent).visible(true);

        mClusterManager = new ClusterManager<>(UserLocationActivity.this, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.addPolyline(polylineOptions);


        JsonObject json = new JsonObject();
        json.addProperty("date", date.getText().toString().trim());

        Ion.with(UserLocationActivity.this)
                .load("POST", getString(R.string.url).concat("getlocation/"+id+"/"))
                .setHeader("x-access-token", SessionManager.getjwt(UserLocationActivity.this))
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        JsonArray locations = result.get("locations").getAsJsonArray();

                      if(locations.size() > 0) {
                          mMap.clear();
                          for (int i = 0; i < locations.size(); i++) {

                              JsonObject location = locations.get(i).getAsJsonObject();
                              String datetime = location.get("datetime").getAsString();
                              String lat = location.get("lat").getAsString();
                              String lng = location.get("lng").getAsString();
                              LatLng loc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                              MyItem offsetItem = new MyItem(Double.parseDouble(lat), Double.parseDouble(lng), datetime);
                              mClusterManager.addItem(offsetItem);
                              mClusterManager.setRenderer(new OwnRendring(getApplicationContext(),mMap,mClusterManager));
                              //mMap.addMarker(new MarkerOptions().position(loc)title(datetime));
                            //  decodedPath.add(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));

                              //polylineOptions.add(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));

                              if(i == locations.size()-1){

                                  mMap.setMaxZoomPreference(20.0f);
                                  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 10.0f));
                                  float kmvalue = distance_travelled/1000;
                                  //Log.i("Distance Travelledfinal", distance_travelled+"");
                                  //Log.i("KM final", kmvalue+"");
                                  distance.setText(kmvalue+" KM");
                              }
                              else{
                                  LatLng src =  new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                                  JsonObject location_des = locations.get(i+1).getAsJsonObject();
                                  String lat_des = location_des.get("lat").getAsString();
                                  String lng_des = location_des.get("lng").getAsString();
                                  LatLng des = new LatLng(Double.parseDouble(lat_des), Double.parseDouble(lng_des));
                                  polyline = mMap.addPolyline(new PolylineOptions()
                                          .add(new LatLng(src.latitude, src.longitude),
                                                  new LatLng(des.latitude, des.longitude))
                                          .width(5).color(Color.BLUE).geodesic(false));
                                  distance_travelled = distance_travelled + new Double(SphericalUtil.computeDistanceBetween(src,des)).longValue();
                                  //Log.i("Distances",SphericalUtil.computeDistanceBetween(src,des)+"");
                                  //Log.i("Distance Travelled", distance_travelled+"");
                              }
                          }
                      }
                        else{
                          Toast.makeText(UserLocationActivity.this,"No Location is available for this date",Toast.LENGTH_SHORT).show();
                      }
                    }
                });

      //  Double dt = new Double(distance_travelled);
       // int d = distance_travelled.intValue();

        // polyline = mMap.addPolyline(polylineOptions);
    }

    public void showmarkers() {

        distance_travelled = 0;

        //distance_travelled = new Double(0.0);
        mClusterManager = new ClusterManager<>(UserLocationActivity.this, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);



        JsonObject json = new JsonObject();
        json.addProperty("date", date.getText().toString().trim());

        Ion.with(UserLocationActivity.this)
                .load("POST", getString(R.string.url).concat("getlocation/"+id+"/"))
                .setHeader("x-access-token", SessionManager.getjwt(UserLocationActivity.this))
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        JsonArray locations = result.get("locations").getAsJsonArray();

                        if(locations.size() > 0) {
                            mMap.clear();
                            for (int i = 0; i < locations.size(); i++) {

                                JsonObject location = locations.get(i).getAsJsonObject();
                                String datetime = location.get("datetime").getAsString();
                                String lat = location.get("lat").getAsString();
                                String lng = location.get("lng").getAsString();
                                LatLng loc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                                MyItem offsetItem = new MyItem(Double.parseDouble(lat), Double.parseDouble(lng), datetime);
                                mClusterManager.addItem(offsetItem);
                                mClusterManager.setRenderer(new OwnRendring(getApplicationContext(),mMap,mClusterManager));

                                if(i == locations.size()-1){

                                    mMap.setMaxZoomPreference(20.0f);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 10.0f));
                                    float kmvalue = distance_travelled/1000;
                                    distance.setText(kmvalue+" KM");
                                }
                                else{
                                    LatLng src =  new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                                    JsonObject location_des = locations.get(i+1).getAsJsonObject();
                                    String lat_des = location_des.get("lat").getAsString();
                                    String lng_des = location_des.get("lng").getAsString();
                                    LatLng des = new LatLng(Double.parseDouble(lat_des), Double.parseDouble(lng_des));
                                    distance_travelled = distance_travelled + new Double(SphericalUtil.computeDistanceBetween(src,des)).longValue();
                                   // Log.i("Distances",SphericalUtil.computeDistanceBetween(src,des)+"");
                                   // Log.i("Distance Travelled", distance_travelled+"");

                                }


                            }
                        }
                        else{
                            Toast.makeText(UserLocationActivity.this,"No Location is available for this date",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //Double dt = new Double(distance_travelled.doubleValue());
        //int d = distance_travelled.intValue();

        // polyline = mMap.addPolyline(polylineOptions);
    }

    DatePickerDialog.OnDateSetListener date_ = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();

        }

    };

    private void updateLabel() {

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        date.setText(sdf.format(myCalendar.getTime()));
        boolean checked = radioButton.isChecked();
        if (checked){
            showpath();
        }
        else
        showmarkers();
    }

    public void userdetail(View view){
        this.finish();
    }

    public void setDecodedPath(View view){

       radioButton.setChecked(!radioButton.isChecked());
        boolean checked = radioButton.isChecked();

        if (checked)
        {
           // radioButton.setChecked(true);
            if (!date.getText().toString().trim().equals(""))
            showpath();
        }
        else
        {
            //radioButton.setChecked(false);
            if(!date.getText().toString().trim().equals(""))
            showmarkers();
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


}

class OwnRendring extends DefaultClusterRenderer<MyItem> {

    public OwnRendring(Context context, GoogleMap map,
                       ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
    }


    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {

        markerOptions.title(item.getTitle());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}

