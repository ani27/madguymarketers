package com.vp6.anish.madguysmarketers;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StatFs;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.File;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {



    private ArrayList<String> imageUrls;
    private GalleryAdapter galleryAdapter;
    private RecyclerView recyclerView;
    public ArrayList<String> photosaddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Photos");


        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        final Cursor imagecursor = this.managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy + " DESC");

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        this.imageUrls = new ArrayList<>();

        for (int i = 0; i < imagecursor.getCount(); i++) {
            imagecursor.moveToPosition(i);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            imageUrls.add(imagecursor.getString(dataColumnIndex));

            //System.out.println("=====> Array path => "+imageUrls.get(i));
        }

        if(getIntent().getExtras().getString("number").equals("10")) {
            galleryAdapter = new GalleryAdapter(GalleryActivity.this, imageUrls,10);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(GalleryActivity.this, 3);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(galleryAdapter);
        }
        else if(getIntent().getExtras().getString("number").equals("1")){
            galleryAdapter = new GalleryAdapter(GalleryActivity.this, imageUrls,1);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(GalleryActivity.this, 3);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(galleryAdapter);
        }
    }


    public void upload(View v)
    {
        photosaddress = new ArrayList<>();
        photosaddress = galleryAdapter.getCheckedItems();
        Intent intent = new Intent();
        intent.putStringArrayListExtra("photosurl", photosaddress);
        setResult(RESULT_OK,intent);
        finish();
    }



}
