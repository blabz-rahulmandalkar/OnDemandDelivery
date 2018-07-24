package com.driverapp;

import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.teliver.sdk.core.TLog;
import com.teliver.sdk.core.Teliver;


public class Application extends MultiDexApplication {

    private String TAG = "Application";
    private SharedPreferences sharedPreferences;

    private SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();
        Teliver.init(this, "e5108ffe621262e808b30c75d813a6b4");
        TLog.setVisible(true);
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Log.i(TAG,BuildConfig.TeliverKey);
        editor.apply();
    }

    public void storeStringInPref(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void storeBooleanInPref(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public String getStringInPref(String key) {
        return sharedPreferences.getString(key, null);
    }

    public boolean getBooleanInPef(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void deletePreference() {
        editor.clear();
        editor.commit();
    }
}
