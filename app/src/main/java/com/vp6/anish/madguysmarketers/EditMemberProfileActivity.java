package com.vp6.anish.madguysmarketers;

import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class EditMemberProfileActivity extends AppCompatActivity {

    EditText workingto;
    EditText workingfrom;
    EditText name_;
    String name;
    String id;
    String number;
    CircleImageView profilepic;
    TextView number_;
    java.util.Calendar mcurrentTime;
    final int PROFILE_PIC = 10001;
    final int PIC_CROP = 10002;
    ArrayList<String>photoaddress;
    private Uri mCropImagedUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_member_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        profilepic = (CircleImageView)findViewById(R.id.profile_image);
        workingfrom = (EditText)findViewById(R.id.working_from);
        workingto = (EditText)findViewById(R.id.working_to);
        workingfrom.setInputType(InputType.TYPE_NULL);
        workingto.setInputType(InputType.TYPE_NULL);
        name_ = (EditText)findViewById(R.id.name);
        number_ = (TextView)findViewById(R.id.number);
        name = getIntent().getExtras().getString("name");
        number = getIntent().getExtras().getString("number");
        id= getIntent().getExtras().getString("id");
        name_.setText(name);
        number_.setText(number);


        workingfrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mcurrentTime = java.util.Calendar.getInstance();
                int hour = mcurrentTime.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(java.util.Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EditMemberProfileActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
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
                mTimePicker = new TimePickerDialog(EditMemberProfileActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        int hourselected = selectedHour;
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
    }



    public void profilepic(View view)
    {
      Intent intent = new Intent(this,GalleryActivity.class);
        intent.putExtra("number", "1");
      startActivityForResult(intent, PROFILE_PIC);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (resultCode == RESULT_OK)
       {
        if (requestCode == PROFILE_PIC)
        {
            photoaddress = new ArrayList<>();
            photoaddress = data.getStringArrayListExtra("photosurl");
            String picUri = photoaddress.get(0);
            performCrop(picUri);

        }
        else if (requestCode == PIC_CROP){
          //  Bundle extras = data.getExtras();

                //Bitmap thePic = extras.getParcelable("data");

            Glide.with(this).load(mCropImagedUri)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(profilepic);

        }
       }
    }


    private void performCrop(String picUri){
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(Uri.fromFile(new File(picUri)), "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            File f = createNewFile("CROP_");
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Log.e("io", ex.getMessage());
            }

            mCropImagedUri = Uri.fromFile(f);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCropImagedUri);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    private File createNewFile(String prefix){
        if(prefix==null || "".equalsIgnoreCase(prefix)){
            prefix="IMG_";
        }
        File newDirectory = new File(Environment.getExternalStorageDirectory()+"/mypics");
        if(!newDirectory.exists()){
            if(newDirectory.mkdir()){
               Log.i(this.getClass().getName(), newDirectory.getAbsolutePath()+" directory created");
            }
        }
        File file = new File(newDirectory,(prefix+System.currentTimeMillis()+".jpg"));
        if(file.exists()){
            //this wont be executed
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

}
