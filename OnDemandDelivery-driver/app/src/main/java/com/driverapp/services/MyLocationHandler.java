package com.driverapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderApi;

/**
 * Created by bridgelabz on 22/07/18.
 */

public class MyLocationHandler extends IntentService {

    public static final String LATITUDE = "current_latitude";
    public static final String LONGITUDE = "current_longitude";
    public static final String UPDATE_LOCATION = "update_location";
    public MyLocationHandler() {
        super("MyLocationHandler");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Location location = intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
        if (location!=null) {
            //if (location.getAccuracy() < 20) {
            Intent intentUpdate = new Intent();
            intentUpdate.setAction(UPDATE_LOCATION);
            intentUpdate.addCategory(Intent.CATEGORY_DEFAULT);
            intentUpdate.putExtra(LATITUDE, location.getLatitude());
            intentUpdate.putExtra(LONGITUDE, location.getLongitude());
            sendBroadcast(intentUpdate);
            // }
        }

    }


}