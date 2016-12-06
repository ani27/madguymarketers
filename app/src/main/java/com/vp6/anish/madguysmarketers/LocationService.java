package com.vp6.anish.madguysmarketers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class LocationService extends Service {
    public static final String BROADCAST_ACTION = "Hello World";
    private static final int FIVE_MINUTES = 1000 * 60 * 5;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;

    Intent intent;
    int counter = 0;

    @Override
    public void onCreate() {
        super.onCreate();
       // intent = new Intent(BROADCAST_ACTION);
       // Toast.makeText(this,"Service created", Toast.LENGTH_LONG).show();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
       // Toast.makeText(this,"StartCommand Service", Toast.LENGTH_LONG).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener(LocationService.this);
        Log.i("location manager","zero");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i("Permission", "error");
            return START_NOT_STICKY ;
        }
        Log.i("location manager","one");
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4*60*1000, 0, listener);
        Log.i("location manager","two");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4*60*1000, 0, listener);
       // locationManager.requestLocationUpdates();
       // Toast.makeText(this,"Start Service 2", Toast.LENGTH_LONG).show();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > FIVE_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -FIVE_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than five minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }


    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
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
        locationManager.removeUpdates(listener);
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }




    public class MyLocationListener implements LocationListener
    {

        Service service;
        ArrayList<String> lats = new ArrayList<>();
        ArrayList<String>lngs = new ArrayList<>();
        ArrayList<String>datetimes = new ArrayList<>();
        ArrayList<ArrayList>calls = new ArrayList<>();
        NotificationCompat.Builder builder;
        NotificationManager manager;

        public MyLocationListener(Service service)
        {
            this.service = service;
        }
        public void onLocationChanged(final Location loc)
        {

            Log.i("*****", "Location changed");
            if(isBetterLocation(loc, previousBestLocation)) {
                loc.getLatitude();
                loc.getLongitude();
               lats.add((loc.getLatitude())+"");
                lngs.add(loc.getLongitude()+"");
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                datetimes.add(ts);
                Gson gson  = new Gson();
                String latitude = gson.toJson(lats);
                String longitude = gson.toJson(lngs);
                final String dateandtime = gson.toJson(datetimes);




                if(isNetworkAvailable()){

                    JsonObject json = new JsonObject();
                    json.addProperty("lats",latitude);
                    json.addProperty("lngs",longitude);
                    json.addProperty("datetimes",dateandtime);



                    Ion.with(service)
                            .load("POST", getString(R.string.url).concat("synclocation/"))
                            .setHeader("x-access-token", SessionManager.getjwt(service))
                            .setJsonObjectBody(json)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {

                                    lats = new ArrayList<String>();
                                    lngs = new ArrayList<String>();
                                    datetimes = new ArrayList<String>();

                                    calls = getCallDetails();
                                    if (calls != null && calls.size()>0) {

                                        for (int i = 0; i < calls.size(); i++) {
                                            JsonObject json_obj = new JsonObject();
                                            json_obj.addProperty("phone",calls.get(i).get(0).toString() );
                                            json_obj.addProperty("datetime", calls.get(i).get(1).toString());
                                            json_obj.addProperty("duration", calls.get(i).get(2).toString());
                                            json_obj.addProperty("call_type",calls.get(i).get(3).toString());


                                            Ion.with(service)
                                                    .load("POST", getString(R.string.url).concat("call/"))
                                                    .setHeader("x-access-token", SessionManager.getjwt(service))
                                                    .setJsonObjectBody(json_obj)
                                                    .asJsonObject()
                                                    .setCallback(new FutureCallback<JsonObject>() {
                                                        @Override
                                                        public void onCompleted(Exception e, JsonObject result) {
                                                            calls = new ArrayList<ArrayList>();
                                                        }
                                                    });
                                        }

                                    }
                                }
                            });



                }


            }
        }



        public void onProviderDisabled(String provider)
        {
            builder = new NotificationCompat.Builder(service)
                            .setSmallIcon(R.drawable.settings)
                            .setContentTitle("MadGuys Marketers")
                            .setContentText("Gps is disabled. Turn on the gps");

            Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            PendingIntent contentIntent = PendingIntent.getActivity(service, 0, gpsOptionsIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);

            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());
            //Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
        }


        public void onProviderEnabled(String provider)
        {
            manager.cancel(0);
            //Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }

        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }


        private ArrayList<ArrayList> getCallDetails() {
            ArrayList<ArrayList>allcalldetails = new ArrayList<>();
            ArrayList<String>lastCallDetails = new ArrayList<>();
            Uri contacts = CallLog.Calls.CONTENT_URI;
            HashMap<String, String> rowDataCall;
            if (ActivityCompat.checkSelfPermission(service, android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return null ;
            }
            Cursor managedCursor = service.getContentResolver().query(contacts, null, null, null, null);
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

            while (managedCursor.moveToNext()) {


                String phNumber = managedCursor.getString(number);
                String callType = managedCursor.getString(type);
                String callDate = managedCursor.getString(date);
                String callDayTime = new Date(Long.valueOf(callDate)).toString();
                // long timestamp = convertDateToTimestamp(callDayTime);
                String callDuration = managedCursor.getString(duration);
                String dir = null;
                if (SessionManager.getLastCallLogs(service) < Long.parseLong(callDate)) {
                    SessionManager.setLastCallLogs(service, Long.parseLong(callDate));
                    Log.i("Call details", phNumber + " " + callDate + "  " + Long.parseLong(callDate));
                    lastCallDetails.add(phNumber);
                    lastCallDetails.add(callDate);
                    lastCallDetails.add(callDuration);

                    int dircode = Integer.parseInt(callType);
                    switch (dircode) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            dir = "OUTGOING";
                            lastCallDetails.add(dir);
                            break;

                        case CallLog.Calls.INCOMING_TYPE:
                            dir = "INCOMING";
                            lastCallDetails.add(dir);
                            break;

                        case CallLog.Calls.MISSED_TYPE:
                            dir = "MISSED";
                            lastCallDetails.add(dir);
                            break;
                        case CallLog.Calls.REJECTED_TYPE:
                            dir ="REJECTED";
                            lastCallDetails.add(dir);
                            break;
                        default:
                            dir="UNKNOWN";
                            lastCallDetails.add(dir);
                            break;
                    }
                    allcalldetails.add(lastCallDetails);
                }

            }
            managedCursor.close();

            return allcalldetails;
        }


    }
}
