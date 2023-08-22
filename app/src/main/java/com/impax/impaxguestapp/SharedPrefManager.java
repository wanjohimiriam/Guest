package com.impax.impaxguestapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;

/**
 * Created by KELVIN on 26/05/2017.
 */
public class SharedPrefManager {
    private static SharedPrefManager mInstance;
    private static Context mCtx;
    private static final String TAG_TOKEN = "tagtoken";
    private static final String TAG_LAT = "tagtoken45";
    private static final String TAG_LNG = "tagtoken48";
    private RequestQueue requestQueue;


    private final static String SHARED_PREF_NAME = "mysharedpref12";
    private final static String KEY_USER_ID = "userid";
    private final static String KEY_USER_FNAME = "userfname";
    private final static String KEY_USER_LNAME = "userlname";
    private final static String KEY_USER_EMAIL = "useremail";
    private final static String KEY_USER_PNUMBER = "usernumber";
    /* private final static String KEY_USER_COUNTRY = "usercountry";*/

    private SharedPrefManager(Context context)
    {
        mCtx = context;
    }
    public static synchronized SharedPrefManager getInstance(Context context)
    {
        if(mInstance==null)
        {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }
    public boolean userLogin(String fname,String email)
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.putString(KEY_USER_ID,user_id);
        editor.putString(KEY_USER_FNAME,fname);
        //editor.putString(KEY_USER_LNAME,lname);
        editor.putString(KEY_USER_EMAIL,email);
        //editor.putString(KEY_USER_PNUMBER,pnumber);
        /*editor.putString(KEY_USER_COUNTRY,country);*/
        editor.apply();
        return true;
    }
    public boolean isLoggedIn()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if(sharedPreferences.getString(KEY_USER_FNAME,null)!=null)
        {
            return true;
        }
        return false;
    }
    public boolean switchIsChecked()
    {
        SharedPreferences.Editor editor = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean("checked", true);
        editor.commit();
        return true;
    }
    public boolean switchUnchecked()
    {
        SharedPreferences.Editor editor = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean("checked", false);
        editor.commit();
        return true;
    }
    public boolean logout()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_FNAME,null);
        editor.apply();
        return true;
    }
    public String getKeyUserFname()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_FNAME,null);
    }
    public String getKeyUserLname()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_LNAME,null);
    }
    public String getKeyUserEmail()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_EMAIL,null);
    }
    public String getKeyUserId()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_ID, "");
        //return sharedPreferences.getString(String.valueOf(KEY_USER_ID),null);
    }
    public String getKeyUserPnumber()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_PNUMBER,null);
    }

    //this method will save the device token to shared preferences
    public boolean saveDeviceToken(String token){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TOKEN, token);
        editor.apply();
        return true;
    }
    //this method will fetch the device token from shared preferences
    public String getDeviceToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(TAG_TOKEN, null);
    }

    //this method will save the device token to shared preferences
    public boolean saveDeviceLatitude(String lat){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_LAT, lat);
        editor.apply();
        return true;
    }
    //this method will fetch the device token from shared preferences
    public String getDeviceLat(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(TAG_LAT, null);
    }


    public boolean saveDeviceLng(String lng){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_LNG, lng);
        editor.apply();
        return true;
    }
    //this method will fetch the device token from shared preferences
    public String getDeviceLng(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(TAG_LNG, null);
    }
   /* public String getKeyUserCountry()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_COUNTRY,null);
    }*/
}

