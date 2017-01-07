package com.vp6.anish.madguysmarketers;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OtpActivity extends AppCompatActivity implements SmsListener.OnSmsReceivedListener {
    EditText otp;
    String id;
    String phone_number;
    String name;
    long workingto;
    long workingfrom;
    private SmsListener receiver;
    ArrayList<String>output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        otp = (EditText)findViewById(R.id.login_otp);
        if(!SessionManager.getHasEnteredNumber(this)) {
            id = getIntent().getExtras().getString("id");
            phone_number = getIntent().getExtras().getString("number");
            name = getIntent().getExtras().getString("name");
            workingfrom = getIntent().getExtras().getLong("workingfrom");
            workingto = getIntent().getExtras().getLong("workingto");
            SessionManager.setId(OtpActivity.this,id);
            SessionManager.setWorkingFrom(OtpActivity.this,workingfrom);
            SessionManager.setWorkingTo(OtpActivity.this,workingto);
            SessionManager.setHasEnteredNumber(this, true);
        }
        output = new ArrayList<>();



        receiver = new SmsListener();
        receiver.setOnSmsReceivedListener(this);


        if(Build.VERSION.SDK_INT>=23){
          requestpermissions();
        }


        //Log.i("ID", id);

    }








    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    @TargetApi(Build.VERSION_CODES.M)
    private void requestpermissions() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.RECEIVE_SMS))
            permissionsNeeded.add("Receive SmS");
        if (!addPermission(permissionsList, Manifest.permission.READ_SMS))
            permissionsNeeded.add("Read SMs");


        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }

        //insertDummyContact();
       // checkauth();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.RECEIVE_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    //insertDummyContact();
                   // checkauth();
                } else {
                    // Permission Denied

                    new android.support.v7.app.AlertDialog.Builder(this)
                            .setTitle("Permission")
                            .setMessage("Permissions must be granted to proceeed further.")
                            .setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    dialog.cancel();

                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                    dialog.cancel();
                                   OtpActivity.this.finish();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                    //Toast.makeText(PermissionActivity.this, "Some Permission is Denied.", Toast.LENGTH_SHORT)
                    //      .show();
                    //  checkauth();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

























    public void verifyotp(View v){

        JsonObject json = new JsonObject();
        json.addProperty("otp", otp.getText().toString().trim());
        json.addProperty("_id", SessionManager.getId(this));


        Ion.with(this)
                .load(getString(R.string.base_url).concat("verify/"))
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {

                            output.add(result.get("admin").getAsString());
                            output.add(result.get("authenticated").getAsString());
                            output.add(result.get("authorized").getAsString());
                            output.add(result.get("token").getAsString());

                            if (output.get(0).equals("true"))
                                SessionManager.setIsAdmin(OtpActivity.this, true);
                            else
                                SessionManager.setIsAdmin(OtpActivity.this, false);

                            if (output.get(1).equals("true")) {
                                SessionManager.setIsAuthenticated(OtpActivity.this, true);
                                SessionManager.setIsUserLogin(OtpActivity.this, true);
                                SessionManager.setJwt(OtpActivity.this, output.get(3));
                                SessionManager.setPhoneNumber(OtpActivity.this, phone_number);
                                SessionManager.setName(OtpActivity.this, name);
                                SessionManager.setLastCallLogs(OtpActivity.this,System.currentTimeMillis());
                            } else {

                                SessionManager.setIsAuthenticated(OtpActivity.this, false);
                                SessionManager.setIsUserLogin(OtpActivity.this, false);
                                Toast.makeText(OtpActivity.this,"Incorrect OTP", Toast.LENGTH_SHORT);

                            }
                            if (output.get(2).equals("true"))
                                SessionManager.setIsAuthorized(OtpActivity.this, true);
                            else
                                SessionManager.setIsAuthorized(OtpActivity.this, false);



                            if (output.get(1).equals("true")) {
                                SessionManager.setHasEnteredNumber(OtpActivity.this, false);
                                Intent intent = new Intent(OtpActivity.this, PermissionActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        }
                        catch (NullPointerException e1)
                        {
                            Log.i("ID", "null");
                           // values.add("0");
                            Toast.makeText(OtpActivity.this,"Failed", Toast.LENGTH_LONG).show();

                        }
                    }
                });

    }


    @Override
    public void onSmsReceived(String otp_text) {
        try
        {
            otp.setText(otp_text);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void signupagain(View view){

        SessionManager.setHasEnteredNumber(this, false);
        Intent intent = new Intent(OtpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
