package com.example.sergey.bikedb.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by User on 20/09/2015.
 */
public class SharedManager {
    private static SharedManager ourInstance = new SharedManager();


    private SharedPreferences mSharedPref;

    public static SharedManager getInstance() {
        return ourInstance;
    }

    private SharedManager() {
    }


    public void init(Context context) {

        mSharedPref = context.getSharedPreferences(context.getPackageName(),0);
    }

    public void put(String key,String value) {

        mSharedPref.edit().putString(key,value).apply();

    }

    public void put(String key,boolean value) {

        mSharedPref.edit().putBoolean(key, value).apply();

    }

    public void put(String key,int value) {

        mSharedPref.edit().putInt(key, value).apply();

    }

    public void put(String key,float value) {

        mSharedPref.edit().putFloat(key, value).apply();

    }

    public boolean getBoolean(String key) {
        return mSharedPref.getBoolean(key,false);
    }

    public String getString(String key) {

        return mSharedPref.getString(key, null);
    }

    public int getInt(String key) {

        return mSharedPref.getInt(key, 0);
    }

    public float getFloat(String key) {

        return mSharedPref.getFloat(key,0f);
    }







}
