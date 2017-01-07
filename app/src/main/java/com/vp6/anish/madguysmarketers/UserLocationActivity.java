package com.vp6.anish.madguysmarketers;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
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
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UserLocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    TextView date;
    Button showpath;
    String id = "";
    Calendar myCalendar;
    private ClusterManager<MyItem> mClusterManager;
    PolylineOptions polylineOptions;
    Polyline polyline;
    CheckedTextView radioButton;
    long distance_travelled;
    TextView distance;
    ArrayList<String>allUrl;
    ArrayList<LatLng> alllines;
    TextView admin_path;
    Button admin_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);
        date = (TextView) findViewById(R.id.date);
        myCalendar = Calendar.getInstance();
        admin_path = (TextView)findViewById(R.id.admin_travel);
        admin_button= (Button)findViewById(R.id.admin_button);
        if (SessionManager.getIsAdmin(this)){
            admin_path.setVisibility(View.VISIBLE);
            admin_button.setVisibility(View.VISIBLE);
        }
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        showpath = (Button) findViewById(R.id.showpath);
        radioButton = (CheckedTextView) findViewById(R.id.checkedTextView);
        distance = (TextView) findViewById(R.id.user_travelled);
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


    public void showmarkers() {

        distance_travelled = 0;

        //distance_travelled = new Double(0.0);
        mClusterManager = new ClusterManager<>(UserLocationActivity.this, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);


        JsonObject json = new JsonObject();
        json.addProperty("date", date.getText().toString().trim());

        Ion.with(UserLocationActivity.this)
                .load("POST", getString(R.string.url).concat("getlocation/" + id + "/"))
                .setHeader("x-access-token", SessionManager.getjwt(UserLocationActivity.this))
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        JsonArray locations = result.get("locations").getAsJsonArray();

                        if (locations.size() > 0) {
                            mMap.clear();
                            for (int i = 0; i < locations.size(); i++) {

                                JsonObject location = locations.get(i).getAsJsonObject();
                                String datetime = location.get("datetime").getAsString();
                                String lat = location.get("lat").getAsString();
                                String lng = location.get("lng").getAsString();
                                LatLng loc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                                MyItem offsetItem = new MyItem(Double.parseDouble(lat), Double.parseDouble(lng), datetime, i+"");
                                mClusterManager.addItem(offsetItem);
                                mClusterManager.setRenderer(new OwnRendring(getApplicationContext(), mMap, mClusterManager));

                                if (i == locations.size() - 1) {

                                    mMap.setMaxZoomPreference(20.0f);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 10.0f));
                                    float kmvalue = distance_travelled / 1000;
                                    distance.setText(kmvalue + " KM");
                                } else {
                                    LatLng src = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                                    JsonObject location_des = locations.get(i + 1).getAsJsonObject();
                                    String lat_des = location_des.get("lat").getAsString();
                                    String lng_des = location_des.get("lng").getAsString();
                                    LatLng des = new LatLng(Double.parseDouble(lat_des), Double.parseDouble(lng_des));
                                    distance_travelled = distance_travelled + new Double(SphericalUtil.computeDistanceBetween(src, des)).longValue();
                                    // Log.i("Distances",SphericalUtil.computeDistanceBetween(src,des)+"");
                                    // Log.i("Distance Travelled", distance_travelled+"");

                                }


                            }
                        } else {
                            Toast.makeText(UserLocationActivity.this, "No Location is available for this date", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

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
        if (checked) {
            showpath();
        } else
            showmarkers();
    }

    public void userdetail(View view) {

        new DatePickerDialog(UserLocationActivity.this, date_, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void setDecodedPath(View view) {

        radioButton.setChecked(!radioButton.isChecked());
        boolean checked = radioButton.isChecked();

        if (checked) {
            // radioButton.setChecked(true);
            if (date.getText().toString() != null && !date.getText().toString().trim().equals(""))
                showpath();
        } else {
            //radioButton.setChecked(false);
            if (date.getText().toString() != null && !date.getText().toString().trim().equals(""))
                showmarkers();
        }

    }


    public void showpath(){

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
                                MyItem offsetItem = new MyItem(Double.parseDouble(lat), Double.parseDouble(lng), datetime, i+"");
                                mClusterManager.addItem(offsetItem);
                                mClusterManager.setRenderer(new OwnRendring(getApplicationContext(),mMap,mClusterManager));

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

                                }
                            }
                        }
                        else{
                            Toast.makeText(UserLocationActivity.this,"No Location is available for this date",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void showroadpath(View view) {


        distance_travelled = 0;
        alllines = new ArrayList<>();
        allUrl = new ArrayList<>();
        polylineOptions = new PolylineOptions().width(5).color(R.color.colorAccent).visible(true);

        mClusterManager = new ClusterManager<>(UserLocationActivity.this, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
       // mMap.addPolyline(polylineOptions);


        JsonObject json = new JsonObject();
        json.addProperty("date", date.getText().toString().trim());

        Ion.with(UserLocationActivity.this)
                .load("POST", getString(R.string.url).concat("getlocation/" + id + "/"))
                .setHeader("x-access-token", SessionManager.getjwt(UserLocationActivity.this))
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        JsonArray locations = result.get("locations").getAsJsonArray();

                        if (locations.size() > 0) {
                            mMap.clear();
                            for (int i = 0; i < locations.size(); i++) {

                                JsonObject location = locations.get(i).getAsJsonObject();
                                String datetime = location.get("datetime").getAsString();
                                String lat = location.get("lat").getAsString();
                                String lng = location.get("lng").getAsString();
                                LatLng loc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                                MyItem offsetItem = new MyItem(Double.parseDouble(lat), Double.parseDouble(lng), datetime, i+"");
                                mClusterManager.addItem(offsetItem);
                                mClusterManager.setRenderer(new OwnRendring(getApplicationContext(), mMap, mClusterManager));

                                if (i == locations.size() - 1) {

                                    mMap.setMaxZoomPreference(20.0f);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 10.0f));
                                    float kmvalue = distance_travelled / 1000;
                                    distance.setText(kmvalue + " KM");
                                } else {
                                    LatLng src = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                                    JsonObject location_des = locations.get(i + 1).getAsJsonObject();
                                    String lat_des = location_des.get("lat").getAsString();
                                    String lng_des = location_des.get("lng").getAsString();
                                    LatLng des = new LatLng(Double.parseDouble(lat_des), Double.parseDouble(lng_des));

                                    if (new Double(SphericalUtil.computeDistanceBetween(src, des)).longValue() > 200) {
                                        //alllines.add(src);
                                        allUrl.add(getUrl(src, des));

                                    }

                                    distance_travelled = distance_travelled + new Double(SphericalUtil.computeDistanceBetween(src, des)).longValue();

                                }
                            }

                            FetchUrl FetchUrl = new FetchUrl();
                            FetchUrl.execute(allUrl);

                        } else {
                            Toast.makeText(UserLocationActivity.this, "No Location is available for this date", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }




    private String getUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }



        public class FetchUrl extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(ArrayList<String>... url) {

            // For storing data from web service
            ArrayList<String> data = new ArrayList<>();

            for (int i=0; i<url[0].size();i++) {
                try {
                    // Fetching the data from web service
                    data.add(downloadUrl(url[0].get(i)));
                    Log.d("Background Task data", data.toString());
                } catch (Exception e) {
                    Log.d("Background Task", e.toString());
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);
                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();
                // Connecting to url
                urlConnection.connect();
                // Reading data from url
                iStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                Log.i("downloadUrl", data.toString());
                br.close();

            } catch (Exception e) {
                Log.i("Exception", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

    }


    private class ParserTask extends AsyncTask<ArrayList<String>, Integer, ArrayList<List<List<HashMap<String, String>>>>> {

        long distance;
        // Parsing the data in non-ui thread
        @Override
        protected ArrayList<List<List<HashMap<String, String>>>> doInBackground(ArrayList<String>... jsonData) {

            JSONObject jObject;
            ArrayList<List<List<HashMap<String, String>>>> routes = new ArrayList<>();
             distance=0;

            for (int i=0; i<jsonData[0].size(); i++) {
                try {
                    jObject = new JSONObject(jsonData[0].get(i));
                    Log.d("ParserTask", jsonData[0].toString());
                    DataParser parser = new DataParser();
                    Log.d("ParserTask", parser.toString());

                    // Starts parsing data
                    routes.add(parser.parse(jObject));
                    distance = distance + parser.getDistance();
                    Log.d("ParserTask", "Executing routes");
                    Log.d("ParserTask", routes.toString());

                } catch (Exception e) {
                    Log.d("ParserTask", e.toString());
                    e.printStackTrace();
                }

            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(ArrayList<List<List<HashMap<String, String>>>> resultlarge) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            for (int k = 0; k < resultlarge.size(); k++) {
                // Traversing through all the routes
                List<List<HashMap<String, String>>> result = resultlarge.get(k);
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(Color.RED);

                    Log.i("onPostExecute", "onPostExecute lineoptions decoded");

                }

                // Drawing polyline in the Google Map for the i-th route
                if (lineOptions != null) {
                      mMap.addPolyline(lineOptions);
                    //allpaths.add(lineOptions);
                } else {
                    Log.d("onPostExecute", "without Polylines drawn");
                }


            }
           // Log.i("Distance", distance+"");
            admin_path.setText(distance+"");
        }
    }


}

class OwnRendring extends DefaultClusterRenderer<MyItem> {

    public OwnRendring(Context context, GoogleMap map,
                       ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
    }


    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {

        markerOptions.title(item.getTitle());
        markerOptions.snippet(item.getMarker_snippet());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}

