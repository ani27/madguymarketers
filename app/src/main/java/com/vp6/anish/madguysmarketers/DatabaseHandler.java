package com.vp6.anish.madguysmarketers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anish on 28-12-2016.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "ListingData";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MYLISTING_TABLE = "CREATE TABLE IF NOT EXISTS mylisting( id TEXT PRIMARY KEY, name TEXT, type TEXT, number TEXT, status TEXT, lat TEXT, lng TEXT, address TEXT, imageurl TEXT)";
        String CREATE_ALLISTING_TABLE = "CREATE TABLE IF NOT EXISTS allisting( id TEXT PRIMARY KEY, name TEXT, type TEXT, number TEXT, status TEXT, lat TEXT, lng TEXT, address TEXT, imageurl TEXT)";
        String CREATE_LOCATION_TABLE  = "CREATE TABLE IF NOT EXISTS location( datetime TEXT PRIMARY KEY, latitude TEXT, longitude TEXT)";
        db.execSQL(CREATE_MYLISTING_TABLE);
        db.execSQL(CREATE_ALLISTING_TABLE);
        db.execSQL(CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS mylisting");
        db.execSQL("DROP TABLE IF EXISTS allisting");
        db.execSQL("DROP TABLE IF EXISTS location");
        // Create tables again
        onCreate(db);
    }
    private static SQLiteDatabase databaseInstance = null;

    public static void initInstance(Context context) {
        if (databaseInstance == null)
            databaseInstance = new DatabaseHandler(context).getDatabase();
    }

    public static SQLiteDatabase getInstance(Context context) {
        if (databaseInstance == null)
            databaseInstance = new DatabaseHandler(context).getDatabase();

        return databaseInstance;
    }




    private SQLiteDatabase getDatabase() {
        return this.getWritableDatabase();
    }
}