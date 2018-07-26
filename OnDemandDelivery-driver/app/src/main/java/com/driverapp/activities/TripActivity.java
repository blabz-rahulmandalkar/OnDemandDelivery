package com.driverapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.driverapp.Application;
import com.driverapp.Constants;
import com.driverapp.R;
import com.driverapp.services.MyLocationHandler;
import com.driverapp.services.RouteFinder;
import com.driverapp.views.AlertDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.teliver.sdk.core.EventListener;
import com.teliver.sdk.core.TLog;
import com.teliver.sdk.core.TaskListener;
import com.teliver.sdk.core.Teliver;
import com.teliver.sdk.core.TripListener;
import com.teliver.sdk.models.PushData;
import com.teliver.sdk.models.Task;
import com.teliver.sdk.models.Trip;
import com.teliver.sdk.models.TripBuilder;
import com.teliver.sdk.models.UserBuilder;

import org.json.JSONObject;

import java.util.List;

import ng.max.slideview.SlideView;

public class TripActivity extends AppCompatActivity implements OnMapReadyCallback,LocationListener {

    private static final int PERMISSIONS_REQUEST_READ_LOCATIONS = 1000;
    private static final int REQUEST_CHECK_SETTINGS = 1001;

    public static final String TAG = "TripActivity";
    Toolbar toolbar;
    GoogleMap googleMap;
    Marker ourGlobalMarker;
    SlideView slideView;
    Button btnViewDetails;
    String trackingId;
    //private String trackingId = "TELIVERTRK_101";
    private LatLng pickupLatLng;
    private LatLng dropLatLng;
    private Marker marker;
    String status;
    RelativeLayout relativeLayout;
    private Application application;
    ProgressDialog progressDialog;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Teliver.identifyUser(new UserBuilder(Constants.DRIVER_ID).setPhone(Constants.DRIVER_MOBILE).setEmail(Constants.DRIVER_EMAIL).setUserType(UserBuilder.USER_TYPE.OPERATOR).registerPush().build());
        TLog.setVisible(true);
        application = (com.driverapp.Application) getApplicationContext();
        trackingId = application.getStringInPref(Constants.KEY_TASK_ID);
        status = application.getStringInPref(Constants.KEY_TASK_STATUS);
        relativeLayout = findViewById(R.id.trip_bottom_relative_layout);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("message"));
        initialiseView();
        initMap();
        if (trackingId!=null){
            Log.i(TAG + "TrackingID",trackingId);
            getTaskDetail();
        }else{
            relativeLayout.setVisibility(View.GONE);
            showToast("No Current Task Available");
        }

    }

    private void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }


    private void getTaskDetail(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Getting Task Detail");
        progressDialog.show();
        Teliver.getMyTaskWithTaskId(trackingId , new TaskListener() {
            @Override
            public void onSuccess(Task task) {
                progressDialog.dismiss();
                status = task.getStatus();
               // maintainState();
                Double pickupLat = task.getPickUp().getLatLongs().get(0);
                Double pickupLong = task.getPickUp().getLatLongs().get(1);
                Double dropLat = task.getDrop().getLatLongs().get(0);
                Double dropLong = task.getDrop().getLatLongs().get(1);
                drawRoutes(pickupLat,pickupLong,dropLat,dropLong);
            }

            @Override
            public void onFailure(String reason) {
                progressDialog.dismiss();
                showToast("Failed to get task");
            }
        });
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initialiseView() {
        toolbar = findViewById(R.id.toolbarCart);
        slideView = findViewById(R.id.slideView);
        btnViewDetails = findViewById(R.id.trip_btn_view_details);
        setSupportActionBar(toolbar);
        //maintainState();
        slideView.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                //status = application.getStringInPref(Constants.KEY_TASK_STATUS);
                String slideText =  slideView.getTextView().getText().toString();
                switch (slideText){
                    case "START TRIP":
                        changeState(1);
                        break;
                    case "PICK UP":
                        changeState(2);
                        break;
                    case "DROP":
                        changeState(3);
                        break;
                    case "Delivered Already":
                        break;
                    default:
                        break;
                }

            }
        });

    }

//    //TODO: Create Request for location update
//    private void makeLocationRequest(){
//        PendingIntent pendingIntent = PendingIntent.getService(this, 0,new Intent(this, MyLocationHandler.class)
//                ,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        IntentFilter intentFilter_update = new IntentFilter(MyLocationHandler.UPDATE_LOCATION);
//        intentFilter_update.addCategory(Intent.CATEGORY_DEFAULT);
//        registerReceiver(locationReceiver,intentFilter_update);
//
//        if (checkIfAlreadyhavePermission()) {
//            LocationServices.FusedLocationApi.requestLocationUpdates(
//                    mGoogleApiClient, getLocationRequest(), pendingIntent);
//        }
//    }

    private void changeState(int stateId){
        if (trackingId!=null) {
            switch (stateId) {
                case 1:
                    startTrip();
                    sendEventPush("1", "Ambulance request is confirmed");
                    updatePreference(true,true,false,false);
                    updateBottomView();
                    break;
                case 2:
                    sendEventPush("2", "Ambulance is started,you can track now");
                    updatePreference(true,true,true,false);
                    updateBottomView();
                    showToast("Picked");
                    break;
                case 3:
                    sendEventPush("3", "Ambulance reached successfully.");
                    updatePreference(true,true,true,true);
                    updateBottomView();
                    showToast("Dropped");
                    //application.storeBooleanInPref(Constants.STEP_FOUR, true);
                    application.storeStringInPref(Constants.KEY_TASK_ID, null);
                    Teliver.updateDriverAvailability(true);
                    Teliver.stopTrip(trackingId);
                    break;
            }
        }else{
            relativeLayout.setVisibility(View.GONE);
            showToast("No Task Available");
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        Log.i(TAG,"Google Map Initialsed");
        requestLocationUpdates();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.googleMap.setMyLocationEnabled(true);
    }

    private void addRouteOnGoogleMap(List<LatLng> routePoints,String distance, String estimatedTime){
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(ResourcesCompat.getColor(getResources(),R.color.colorPrimary,getTheme()));
        polylineOptions.addAll(routePoints);
        polylineOptions.width(3);
        polylineOptions.startCap(new SquareCap());
        polylineOptions.endCap(new SquareCap());
        polylineOptions.geodesic(true);
        this.googleMap.addPolyline(polylineOptions);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng:routePoints){
            builder.include(latLng);
        }
        googleMap.addMarker(new MarkerOptions().position(pickupLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        googleMap.addMarker(new MarkerOptions().position(dropLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        LatLngBounds bounds = builder.build();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,10));
    }

    private void updateDriverLocation(Location location){
        if (location!=null) {
            if (googleMap != null) {
                if (marker != null) {
                    marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));
                }
            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
        }
    }

    private void sendEventPush(final String pushMessage, String tag) {
        String[] users = new String[]{"user_1"};
        PushData pushData = new PushData(users);
        pushData.setMessage(tag);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", pushMessage);
            jsonObject.put("operator", "driver431");
            pushData.setPayload(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Teliver.sendEventPush(trackingId, pushData, tag);
    }

    private void startTrip() {
        TripBuilder tripBuilder = new TripBuilder(trackingId);
        tripBuilder.withInterval(1000);
        tripBuilder.withDistance(5);
        Teliver.startTrip(tripBuilder.build());
        setTripListner();
    }

    private void setTripListner(){
        Teliver.setTripListener(new TripListener() {
            @Override
            public void onTripStarted(Trip tripDetails) {
                showToast("Trip is started");
                sendEventPush("1","Ambulance request is confirmed");
            }

            @Override
            public void onLocationUpdate(Location location) {
                   updateDriverLocation(location);
            }

            @Override
            public void onTripEnded(String trackingID) {
                showToast("Trip is ended");

            }

            @Override
            public void onTripError(String reason) {

            }
        });
    }

    @Override
    protected void onResume() {

        if (application.getBooleanInPef(Constants.STEP_FOUR))
            application.deletePreference();
        //maintainState();
        updateBottomView();
        super.onResume();
    }

    private void maintainState() {
        status = application.getStringInPref(Constants.KEY_TASK_STATUS);
        if (status!=null) {
            relativeLayout.setVisibility(View.VISIBLE);
//            if (status.equals("assigned")) {
//                if (application.getBooleanInPef(Constants.STEP_TWO)) {
//                    slideView.setText("Picked Up");
//
//                }
//                if (application.getBooleanInPef(Constants.STEP_THREE)) {
//                    slideView.setText("Dropped");
//
//                }
//                if (application.getBooleanInPef(Constants.STEP_FOUR)) {
//                    slideView.setText("Delivered Already");
//                    slideView.setEnabled(false);
//
//                }
//            } else {
//                slideView.setText("Delivered Already");
//                slideView.setEnabled(false);
//            }
            //new Code
        switch (status){
            case Constants.STATUS_CREATED:
                break;
            case Constants.STATUS_ASSIGNED:
                slideView.setText("Accept");
                break;
            case Constants.STATUS_ACCEPTED:
                slideView.setText("Confirm");
                break;
            case Constants.STATUS_REJECTED:
                break;
            case Constants.STATUS_IN_PROGRESS:
                break;
            case Constants.STATUS_CANCELLED:
                break;
            case Constants.STATUS_COMPLETED:
                application.storeStringInPref(Constants.KEY_TASK_STATUS,Constants.STATUS_NO_TASK);
                relativeLayout.setVisibility(View.GONE);
                showToast("No Task Available");
                break;

        }
        }else{
            application.storeStringInPref(Constants.KEY_TASK_STATUS,Constants.STATUS_NO_TASK);
            relativeLayout.setVisibility(View.GONE);
            showToast("No Task Available");
        }


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
                        status.startResolutionForResult(TripActivity.this, Constants.SHOW_GPS_DIALOG);
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

    //TODO:Check Location Permission
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
                        }else{
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

    //TODO:Request for location permission
    private void requestForLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_READ_LOCATIONS);
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String taskID = intent.getStringExtra(Constants.KEY_TASK_ID);
            final String status = intent.getStringExtra(Constants.KEY_TASK_STATUS);
            application.storeStringInPref(Constants.KEY_TASK_STATUS,status);
            final Double pickupLat = intent.getDoubleExtra(Constants.KEY_PICKUP_LATITUDE,0.0);
            final Double pickupLong = intent.getDoubleExtra(Constants.KEY_PICKUP_LONGITUDE,0.0);
            final Double dropLat = intent.getDoubleExtra(Constants.KEY_DROP_LATITUDE,0.0);
            final Double dropLong = intent.getDoubleExtra(Constants.KEY_DROP_LONGITUDE,0.0);
            AlertDialog alertDialog = new AlertDialog(TripActivity.this, "New Request Assigned", new AlertDialog.ClickListener() {
                @Override
                public void onAccepted(View view) {
                    progressDialog = new ProgressDialog(TripActivity.this);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Accepting Task");
                    progressDialog.show();
                    Teliver.acceptTask(taskID, new EventListener() {
                        @Override
                        public void onSuccess(String response) {
                            progressDialog.dismiss();
                            showToast("Task is Accepted");
                            trackingId = taskID;
                            drawRoutes(pickupLat,pickupLong,dropLat,dropLong);
                            Teliver.updateDriverAvailability(false);
                            application.storeStringInPref(Constants.KEY_TASK_ID,taskID);
                            updatePreference(true,false,false,false);
                            updateBottomView();
                        }

                        @Override
                        public void onFailure(String reason) {
                            Teliver.updateDriverAvailability(true);
                            progressDialog.dismiss();
                            Log.i(TAG,reason);
                            showToast("Failed Accept");
                        }
                    });
                }

                @Override
                public void onRejected(View view) {
                    progressDialog = new ProgressDialog(TripActivity.this);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Rejecting Task");
                    progressDialog.show();
                    Teliver.rejectTask(taskID, new EventListener() {
                        @Override
                        public void onSuccess(String response) {
                            progressDialog.dismiss();
                            application.storeStringInPref(Constants.KEY_TASK_D,null);
                            updatePreference(false,false,false,false);
                            Teliver.updateDriverAvailability(true);
                            showToast("Task is Rejected");
                        }

                        @Override
                        public void onFailure(String reason) {
                            progressDialog.dismiss();
                            Log.i(TAG,reason);
                            showToast("Failed to reject");
                        }
                    });
                }
            });
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    };

    private void updatePreference(boolean isTripAccepted,boolean isTripStarted,boolean isPickedup,boolean isDropped){
        application.storeBooleanInPref(Constants.STATUS_TRIP_ACCEPTED,isTripAccepted);
        application.storeBooleanInPref(Constants.STATUS_TRIP_STARTED,isTripStarted);
        application.storeBooleanInPref(Constants.STATUS_TRIP_PICKUPED,isPickedup);
        application.storeBooleanInPref(Constants.STATUS_TRIP_DROPPED,isDropped);
    }

    private void updateBottomView() {
        if (trackingId!=null) {
            Log.i(TAG + "TrackID",trackingId);
            relativeLayout.setVisibility(View.VISIBLE);

            if (application.getBooleanInPef(Constants.STATUS_TRIP_ACCEPTED)) {
                slideView.setText("START TRIP");

            }
            if (application.getBooleanInPef(Constants.STATUS_TRIP_STARTED)) {
                slideView.setText("PICK UP");

            }
            if (application.getBooleanInPef(Constants.STATUS_TRIP_PICKUPED)) {
                slideView.setText("DROP");
            }
            if (application.getBooleanInPef(Constants.STATUS_TRIP_DROPPED)) {
                relativeLayout.setVisibility(View.GONE);
                updatePreference(false, false, false, false);
                showToast("No Task Available");

            }
            if (!application.getBooleanInPef(Constants.STATUS_TRIP_ACCEPTED) && !application.getBooleanInPef(Constants.STATUS_TRIP_STARTED) && !application.getBooleanInPef(Constants.STATUS_TRIP_PICKUPED) && !application.getBooleanInPef(Constants.STATUS_TRIP_DROPPED)) {
                relativeLayout.setVisibility(View.GONE);
                showToast("No Task Available");
            }
        }else{
            relativeLayout.setVisibility(View.GONE);
            showToast("No Task Available");
        }

    }


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

    private void drawRoutes(double pickLat,double pickLong,double dropLat, double dropLong){
        pickupLatLng = new LatLng(pickLat,pickLong);
        dropLatLng = new LatLng(dropLat,dropLong);
        if (pickupLatLng!=null && dropLatLng!=null) {
            new RouteFinder(pickupLatLng, dropLatLng, new RouteFinder.RouteListener() {
                @Override
                public void onRouteReceived(List<LatLng> routePoints, String distance, String estimatedTime) {
                    addRouteOnGoogleMap(routePoints, distance, estimatedTime);
                }
            }).startRouteSearch();
        }
    }

//    private void updateMarkerPosition(Location newLocation) {
//        LatLng newLatLng = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
//        if(ourGlobalMarker == null) { // First time adding marker to map
//            ourGlobalMarker = googleMap.addMarker(new MarkerOptions().position(newLatLng));
//        }
//        else {
//            MarkerAnimation.animateMarkerToICS(ourGlobalMarker, newLatLng, new LatLngInterpolator.Spherical());
//        }
//    }

}
