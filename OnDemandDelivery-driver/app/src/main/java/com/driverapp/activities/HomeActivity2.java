package com.driverapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.driverapp.Application;
import com.driverapp.Constants;
import com.driverapp.R;
import com.driverapp.adapters.DrawerItemCustomAdapter;
import com.driverapp.services.MyLocationHandler;
import com.driverapp.views.AlertDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.teliver.sdk.core.EventListener;
import com.teliver.sdk.core.Teliver;

import java.util.List;
import java.util.Locale;


public class HomeActivity2 extends AppCompatActivity implements OnMapReadyCallback, LocationListener{

    private static final int PERMISSIONS_REQUEST_READ_LOCATIONS = 1000;
    private static final int REQUEST_CHECK_SETTINGS = 1001;
    private static final String TAG = "HomeActivity";
    private static Float MAP_ZOOM_LEVEL = 16.f;
    private Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private RelativeLayout mParentlistLayout;
    private DrawerLayout mDrawerLayout;
    private ListView drawerListView;
    private DrawerItemCustomAdapter adapter;
    private CharSequence mTitle;
    private GoogleMap googleMap;
    private TextView tvCurrentAddress;
    Application application;
    MediaPlayer medipPlayer;
    private LocationManager locationManager;
    private Marker currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        initialize();
        initGoogleMaps();
        setupToolbar();
        setupDrawerToggle();
    }

    private void initGoogleMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initialize() {
        application = (com.driverapp.Application) getApplicationContext();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("message"));
        mParentlistLayout = findViewById(R.id.left_drawer);
        tvCurrentAddress = findViewById(R.id.home_card_tv_subtitle);
        mTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }


    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    void setupDrawerToggle(){
        drawerListView = findViewById(R.id.left_drawer_listview);
        adapter = new DrawerItemCustomAdapter(HomeActivity2.this,R.layout.item_nav_menu);
        drawerListView.setAdapter(adapter);
        drawerListView.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name, R.string.app_name);
        mDrawerToggle.syncState();
    }



    private void updateDriverLocation(Location location){
        if (googleMap!=null){
            if (currentMarker==null) {
                currentMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));//.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location_black_24dp)).flat(true));
            }else {
                currentMarker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
                //BitmapDescriptorFactory.
            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),MAP_ZOOM_LEVEL));
        }
    }

    //TODO: Location Listener
    @Override
    public void onLocationChanged(Location location) {
        updateDriverLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        checkGps();
    }

    @Override
    public void onProviderDisabled(String provider) {
        checkGps();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
            }
            mDrawerLayout.closeDrawer(mParentlistLayout);
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String taskID = intent.getStringExtra(Constants.KEY_TASK_ID);
            AlertDialog alertDialog = new AlertDialog(HomeActivity2.this, "New Request Assigned", new AlertDialog.ClickListener() {
                @Override
                public void onAccepted(View view) {

                    Teliver.acceptTask(taskID, new EventListener() {
                        @Override
                        public void onSuccess(String response) {
                            Teliver.updateDriverAvailability(false);
                            application.storeStringInPref(Constants.KEY_TASK_ID,taskID);
                            //TODO: Open Trip Activity
                        }

                        @Override
                        public void onFailure(String reason) {
                            Teliver.updateDriverAvailability(true);
                        }
                    });
                }

                @Override
                public void onRejected(View view) {
                    Teliver.rejectTask(taskID, new EventListener() {
                        @Override
                        public void onSuccess(String response) {
                            Teliver.updateDriverAvailability(true);
                            application.storeStringInPref(Constants.KEY_TASK_ID,null);
                        }

                        @Override
                        public void onFailure(String reason) {
                            Teliver.updateDriverAvailability(true);
                        }
                    });
                }
            });
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        requestLocationUpdates();
    }

    private void checkGps() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();
                if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    try {
                        status.startResolutionForResult(HomeActivity2.this, Constants.SHOW_GPS_DIALOG);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.SHOW_GPS_DIALOG && resultCode == RESULT_OK) {
            requestLocationUpdates();
        }else if (requestCode == Constants.SHOW_GPS_DIALOG && resultCode == RESULT_CANCELED) {
            finish();
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_LOCATIONS){
            if (grantResults.length>0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    requestLocationUpdates();
                }
            }
        }
    }

    private void requestLocationUpdates(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkIfAlreadyhavePermission()) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        6000, 5, this);
                getLastLocationNewMethod();
            }else{
                checkGps();
            }
        }else{
            requestForLocationPermission();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocationNewMethod(){
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            updateDriverLocation(location);
                        }else {
                            getLastLocationNewMethod();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    private void requestForLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_READ_LOCATIONS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
