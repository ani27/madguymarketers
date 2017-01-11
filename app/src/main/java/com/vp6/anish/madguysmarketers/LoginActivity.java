package com.vp6.anish.madguysmarketers;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class LoginActivity extends AppCompatActivity {

    EditText number;
    EditText name;
    EditText workingto;
    EditText workingfrom;
    TextView workinghours;
    TextView to;
    Button login;
    Button signinredirect;
    boolean onsignuppage = true;
    String id;
    java.util.Calendar mcurrentTime;
    long workingstart = 8 * 60;
    long workingend = 17 * 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        number = (EditText)findViewById(R.id.login_number);
        name = (EditText)findViewById(R.id.login_name);
        workingfrom = (EditText)findViewById(R.id.working_from);
        workingto = (EditText)findViewById(R.id.working_to);
        workingfrom.setInputType(InputType.TYPE_NULL);
        workingto.setInputType(InputType.TYPE_NULL);
        workinghours = (TextView)findViewById(R.id.textView2);
        to = (TextView)findViewById(R.id.textView3);
        signinredirect = (Button)findViewById(R.id.signinredirect);
        signinredirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onsignuppage){
                    login.setText("Sign In");
                    workingfrom.setVisibility(View.GONE);
                    workingto.setVisibility(View.GONE);
                    name.setVisibility(View.GONE);
                    workinghours.setVisibility(View.GONE);
                    to.setVisibility(View.GONE);
                    signinredirect.setText("Not a member? Sign Up");
                    onsignuppage = false;
                }
                else{
                    login.setText("Sign Up");
                    workingfrom.setVisibility(View.VISIBLE);
                    workingto.setVisibility(View.VISIBLE);
                    name.setVisibility(View.VISIBLE);
                    workinghours.setVisibility(View.VISIBLE);
                    to.setVisibility(View.VISIBLE);
                    signinredirect.setText("Already a member? Sign In");
                    onsignuppage = true;
                }
            }
        });


        workingfrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mcurrentTime = java.util.Calendar.getInstance();
                int hour = mcurrentTime.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(java.util.Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(LoginActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        workingstart = (selectedHour * 60)+selectedMinute;
                        int hourselected = selectedHour;
                        String hourcheck =""+selectedHour;
                        String minutecheck = ""+selectedMinute;
                        String am_pm ="AM";
                        if (selectedHour >= 12)
                        {   am_pm = "PM";
                            hourselected = selectedHour -12;
                            if(hourselected == 0)
                                hourselected=12;
                        }
                        if(hourselected < 10)
                        {
                            hourcheck = "0"+hourselected;
                        }
                        if (selectedMinute <10)
                        {
                            minutecheck = "0"+selectedMinute;
                        }

                        workingfrom.setText(""+hourcheck +":"+minutecheck+" "+am_pm);
                    }
                }, hour, minute,false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        workingto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mcurrentTime = java.util.Calendar.getInstance();
                int hour = mcurrentTime.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(java.util.Calendar.MINUTE);


                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(LoginActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        int hourselected = selectedHour;
                        workingend =  (selectedHour * 60)+selectedMinute;
                        String hourcheck = "" + selectedHour;
                        String minutecheck = "" + selectedMinute;
                        String am_pm = "AM";
                        if (selectedHour >= 12) {
                            am_pm = "PM";
                            hourselected = selectedHour - 12;
                            if (hourselected == 0)
                                hourselected = 12;
                        }
                        if (hourselected < 10) {
                            hourcheck = "0" + hourselected;
                        }
                        if (selectedMinute < 10) {
                            minutecheck = "0" + selectedMinute;
                        }

                        workingto.setText("" + hourcheck + ":" + minutecheck + " " + am_pm);
                    }
                }, hour, minute,false);
                mTimePicker.setTitle("Select Time");

                mTimePicker.show();
            }
        });
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
        if(SessionManager.getHasEnteredNumber(this)){

            Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
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
                            //Log.i("result",result.toString());
                            //Log.i("ID_ASYNC", result.get("_id").getAsString());
                            id = result.get("_id").getAsString();
                            Intent intent = new Intent(LoginActivity.this,OtpActivity.class);
                           /// Log.i("ID_LOGIN", id);
                            intent.putExtra("id", id);
                            intent.putExtra("number", result.get("phone").getAsString());
                            intent.putExtra("name", result.get("name").getAsString());
                            intent.putExtra("workingfrom", workingstart);
                            intent.putExtra("workingto", workingend);
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
