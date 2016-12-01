package com.vp6.anish.madguysmarketers;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class LoginActivity extends AppCompatActivity {

    EditText number;
    EditText name;
    Button login;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        number = (EditText)findViewById(R.id.login_number);
        name = (EditText)findViewById(R.id.login_name);
        login = (Button) findViewById(R.id.login_button);
        login.setClickable(false);
        login.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_dark_disabled));


        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }


            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.length() == 10)
                {
                    login.setClickable(true);

                    login.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_dark_focused));

                }
                else
                {
                    login.setClickable(false);
                    login.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_dark_disabled));

                    //login.setBackgroundTintList(ColorStateList.valueOf(R.color.grey));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if(SessionManager.getIsAuthenticated(LoginActivity.this)){
            Intent intent = new Intent(LoginActivity.this, PermissionActivity.class);
            startActivity(intent);
            finish();
        }



    }



    public void sendnumber(View view) {

        JsonObject json = new JsonObject();
        json.addProperty("phone", number.getText().toString().trim());
        json.addProperty("name", name.getText().toString().trim());
        Ion.with(this)
                .load(getString(R.string.base_url).concat("user/"))
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        String id;
                        try {
                            Log.i("ID_ASYNC", result.get("_id").getAsString());
                            id = result.get("_id").getAsString();

                            Intent intent = new Intent(LoginActivity.this,OtpActivity.class);
                    Log.i("ID_LOGIN", id);
                    intent.putExtra("id", id);
                    intent.putExtra("number", number.getText().toString().trim());
                            intent.putExtra("name", name.getText().toString().trim());
                    startActivity(intent);
                            finish();


                        }
                        catch (NullPointerException e1)
                        {
                            //Log.i("ID", "null");
                            Log.i("EXCEPTION",e1.getMessage());
                            Toast.makeText(LoginActivity.this,"Failed, Try again later", Toast.LENGTH_LONG).show();

                        }

                    }
                });

    }
}
