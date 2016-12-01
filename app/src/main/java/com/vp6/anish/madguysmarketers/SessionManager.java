package com.vp6.anish.madguysmarketers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by anish on 20-10-2016.
 */
public class SessionManager {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "Stow";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String LAST_CALL_LOGS = "lastCallLogs";

    private static final String IS_AUTHENTICATED = "IsAuthenticated";
    private static final String IS_AUTHORIZED = "IsAuthorized";
    private static final String IS_ADMIN = "IsAdmin";
    private static final String IS_PHONE = "IsPhone";
    private static final String IS_NAME = "IsName";
    private static final String IS_JWT = "IsJwt";
    private static final String IS_ID = "IsId";




    public static void setIsUserLogin(Context context, boolean isUserSignUp) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND).edit();
        editor.putBoolean(IS_LOGIN, isUserSignUp);
        editor.commit();
    }

    public static boolean getisUserLogIn(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND);
        return savedSession.getBoolean(IS_LOGIN, false);
    }

    public static void setIsAuthenticated(Context context, boolean isUserAuthenticated) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND).edit();
        editor.putBoolean(IS_AUTHENTICATED, isUserAuthenticated);
        editor.commit();
    }

    public static boolean getIsAuthenticated(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND);
        return savedSession.getBoolean(IS_AUTHENTICATED, false);
    }

    public static void setIsAuthorized(Context context, boolean isUserAuthorized) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND).edit();
        editor.putBoolean(IS_AUTHORIZED, isUserAuthorized);
        editor.commit();
    }

    public static boolean getIsAuthorized(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND);
        return savedSession.getBoolean(IS_AUTHORIZED, false);
    }

    public static void setIsAdmin(Context context, boolean isUserAdmin) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND).edit();
        editor.putBoolean(IS_ADMIN, isUserAdmin);
        editor.commit();
    }

    public static boolean getIsAdmin(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND);
        return savedSession.getBoolean(IS_ADMIN, false);
    }

    public static void setPhoneNumber(Context context, String phonenumber) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND).edit();
        editor.putString(IS_PHONE, phonenumber);
        editor.commit();
    }

    public static String getphonenumber(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND);
        return savedSession.getString(IS_PHONE, "0");
    }

    public static void setName(Context context, String name) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND).edit();
        editor.putString(IS_NAME, name);
        editor.commit();
    }

    public static String getName(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND);
        return savedSession.getString(IS_NAME, "user");
    }
    public static void setJwt(Context context, String jwt) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND).edit();
        editor.putString(IS_JWT, jwt);
        editor.commit();
    }

    public static String getjwt(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND);
        return savedSession.getString(IS_JWT, "0");
    }

    public static void setId(Context context, String Id) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND).edit();
        editor.putString(IS_ID, Id);
        editor.commit();
    }

    public static String getId(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND);
        return savedSession.getString(IS_ID, "0");
    }

    public static void setLastCallLogs(Context context, Long jwt) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND).edit();
        editor.putLong(LAST_CALL_LOGS, jwt);
        editor.commit();
    }

    public static Long getLastCallLogs(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND);
        return savedSession.getLong(LAST_CALL_LOGS, 1477398133);
    }


}
