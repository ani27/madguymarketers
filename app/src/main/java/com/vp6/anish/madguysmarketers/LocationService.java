package com.vp6.anish.madguysmarketers;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class LocationService extends Service {
    private static final String TAG = "TESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 5 * 60 * 1000;
    private static final float LOCATION_DISTANCE = 1f;

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;
        Service service;
        ArrayList<String> lats = new ArrayList<>();
        ArrayList<String> lngs = new ArrayList<>();
        ArrayList<String> datetimes = new ArrayList<>();
        ArrayList<ArrayList> calls = new ArrayList<>();
        NotificationCompat.Builder builder;
        NotificationManager manager;

        public LocationListener(String provider, Service service) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
            this.service = service;
        }

        @Override
        public void onLocationChanged(Location loc) {
            Log.e(TAG, "onLocationChanged: " + loc);
            mLastLocation.set(loc);

            Log.i("*****", "Location changed");


            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int minutes = Calendar.getInstance().get(Calendar.MINUTE);
            long time = hour * 60 + minutes;
            long workingfrom = SessionManager.getWorkingFrom(service);
            long workingto = SessionManager.getWorkingTo(service);
            boolean is_more = time > workingfrom;
            boolean is_less = time < workingto;
            Log.i("time", time + " ");
            Log.i("Working from", workingfrom + " inside listener");
            Log.i("Working to", workingto + " inside listener");

            if (is_more && is_less) {
                loc.getLatitude();
                loc.getLongitude();
                lats.add((loc.getLatitude()) + "");
                lngs.add(loc.getLongitude() + "");
                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();
                datetimes.add(ts);
//            Gson gson = new Gson();
//          final  String latitude = gson.toJson(lats);
//            String longitude = gson.toJson(lngs);
//            String dateandtime = gson.toJson(datetimes);
//

                if (isNetworkAvailable() && lats.size() > 0) {

                    Gson gson = new Gson();
                    final String latitude = gson.toJson(lats);
                    final String longitude = gson.toJson(lngs);
                    final String dateandtime = gson.toJson(datetimes);
                    final String last_latitude = lats.get(lats.size()-1);
                    final String last_longitude = lngs.get(lngs.size()-1);


                    JsonObject json = new JsonObject();
                    json.addProperty("lats", latitude);
                    json.addProperty("lngs", longitude);
                    json.addProperty("datetimes", dateandtime);
                     Log.i("Latitude", latitude);
                     Log.i("Longitude", longitude);

                    Ion.with(service)
                            .load("POST", getString(R.string.url).concat("synclocation/"))
                            .setHeader("x-access-token", SessionManager.getjwt(service))
                            .setJsonObjectBody(json)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {

                                    calls = getCallDetails();
                                    if (calls != null && calls.size() > 0) {

                                        for (int i = 0; i < calls.size(); i++) {
                                            JsonObject json_obj = new JsonObject();
                                            json_obj.addProperty("phone", calls.get(i).get(0).toString());
                                            json_obj.addProperty("datetime", calls.get(i).get(1).toString());
                                            json_obj.addProperty("duration", calls.get(i).get(2).toString());
                                            json_obj.addProperty("call_type", calls.get(i).get(3).toString());


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
                                    Ion.with(service)
                                            .load("POST", "http://madguylab.com/partner/auto_apps/update_member_location.php")
                                            .setHeader("x-access-token", SessionManager.getjwt(service))
                                            .setBodyParameter("user_id", SessionManager.getId(service))
                                            .setBodyParameter("user_name", SessionManager.getName(service))
                                            .setBodyParameter("latitude",  last_latitude)
                                            .setBodyParameter("longitude", last_longitude)
                                            .setBodyParameter("user_number",SessionManager.getphonenumber(service))
                                            .asString()
                                            .setCallback(new FutureCallback<String>() {
                                                @Override
                                                public void onCompleted(Exception e, String result) {
                                                    //calls = new ArrayList<ArrayList>();
                                                    Log.i("Last location", "updated");
                                                }
                                            });

                                }
                            });


                    lats = new ArrayList<>();
                    lngs = new ArrayList<>();
                    datetimes = new ArrayList<>();

                }
            }
        }


        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int minutes = Calendar.getInstance().get(Calendar.MINUTE);
            long time = hour * 60 + minutes;
            long workingfrom = SessionManager.getWorkingFrom(service);
            long workingto = SessionManager.getWorkingTo(service);
            boolean is_more = time > workingfrom;
            boolean is_less = time < workingto;
            Log.i("time", time + " ");
            Log.i("Working from", workingfrom + " inside listener");
            Log.i("Working to", workingto + " inside listener");

            if (is_more && is_less) {
                builder = new NotificationCompat.Builder(service)
                        .setSmallIcon(R.drawable.map)
                        .setContentTitle("MadGuys Marketers")
                        .setContentText("Gps is disabled. Turn on the gps");

                Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                PendingIntent contentIntent = PendingIntent.getActivity(service, 0, gpsOptionsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(contentIntent);
                builder.setOngoing(true);
                manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(0, builder.build());
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            if (builder != null) {
                builder.setOngoing(false);
                manager.cancel(0);
                Log.e(TAG, "onProviderEnabled: " + provider);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }

        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }


        private ArrayList<ArrayList> getCallDetails() {
            ArrayList<ArrayList> allcalldetails = new ArrayList<>();
            ArrayList<String> lastCallDetails = new ArrayList<>();
            Uri contacts = CallLog.Calls.CONTENT_URI;
            HashMap<String, String> rowDataCall;
            if (ActivityCompat.checkSelfPermission(LocationService.this, android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return null;
            }
            Cursor managedCursor = LocationService.this.getContentResolver().query(contacts, null, null, null, null);
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
                if (SessionManager.getLastCallLogs(LocationService.this) < Long.parseLong(callDate)) {
                    SessionManager.setLastCallLogs(LocationService.this, Long.parseLong(callDate));
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
                            dir = "REJECTED";
                            lastCallDetails.add(dir);
                            break;
                        default:
                            dir = "UNKNOWN";
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

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER, LocationService.this),
//            new LocationListener(LocationManager.NETWORK_PROVIDER, LocationService.this)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
//        try {
//            mLocationManager.requestLocationUpdates(
//                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                    mLocationListeners[1]);
//        } catch (java.lang.SecurityException ex) {
//            Log.i(TAG, "fail to request location update, ignore", ex);
//        } catch (IllegalArgumentException ex) {
//            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
//        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();

        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }

        Intent intent = new Intent("com.android.locservice");
        sendBroadcast(intent);
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }


}