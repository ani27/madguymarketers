package com.vp6.anish.madguysmarketers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class OtpActivity extends AppCompatActivity {
    EditText otp;
    String id;
    String phone_number;
    String name;
    long workingto;
    long workingfrom;

    ArrayList<String>output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        otp = (EditText)findViewById(R.id.login_otp);
        id = getIntent().getExtras().getString("id");
        phone_number = getIntent().getExtras().getString("number");
        name =  getIntent().getExtras().getString("name");
        workingfrom = getIntent().getExtras().getLong("workingfrom");
        workingto = getIntent().getExtras().getLong("workingto");
        output = new ArrayList<>();

        Log.i("ID", id);
    }

    public void verifyotp(View v){

        JsonObject json = new JsonObject();
        json.addProperty("otp", otp.getText().toString().trim());
        json.addProperty("_id", id);


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
                                SessionManager.setId(OtpActivity.this,id);
                                SessionManager.setWorkingFrom(OtpActivity.this,workingfrom);
                                SessionManager.setWorkingTo(OtpActivity.this,workingto);
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
}
