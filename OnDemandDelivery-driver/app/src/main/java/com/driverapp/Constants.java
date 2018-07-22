package com.driverapp;



public class Constants {

    public static final String USER_ID_AVAILED = "user_id_received",TRACKING_ID_CREATED = "tracking_id_created",
            STEP_ONE="step_one",STEP_TWO="step_two",STEP_THREE="step_three",STEP_FOUR="step_four",TRACKING_ID = "tracking_id";


    public static final int COARSE_LOCATION_PERMISSION = 4,SHOW_GPS_DIALOG = 2;

    //TODO: API Constants
    public static final String API_KEY = "4bb2df69c7ea4deeb1d4c0a5a49c33d0";
    public static final String API_BASE_URL = "https://api.teliver.xyz/v1/";
    public static final String API_GET_TASKS = "task/list";
    public static final String API_GET_TRIPS = "trips";

    //TODO :Driver ID
    public static final String ID_DRIVER = "aruna_driver";

    public static final String APP_NAME  = "Teliver Driver";


    //TODO:KEYS
    public static final String KEY_TASK_ID = "task_id";
    public static final String KEY_TASK_STATUS = "status";
    public static final String KEY_TASK_D = "task_id";

    public static final String KEY_PICKUP_LATITUDE = "pickup_latitude";
    public static final String KEY_PICKUP_LONGITUDE = "pickup_longitude";
    public static final String KEY_DROP_LATITUDE = "drop_latitude";
    public static final String KEY_DROP_LONGITUDE = "drop_longitude";


    //TODO: TASK STATUS
    public static final String STATUS_CREATED = "created";
    public static final String STATUS_ASSIGNED = "assigned";
    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_REJECTED = "rejected";
    public static final String STATUS_IN_PROGRESS = "in_progress";
    public static final String STATUS_CANCELLED = "cancelled";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_NO_TASK = "no_task";

    public static final String STATUS_TRIP_ACCEPTED = "trip_accepted";
    public static final String STATUS_TRIP_STARTED = "trip_started";
    public static final String STATUS_TRIP_PICKUPED = "trip_pickeup";
    public static final String STATUS_TRIP_DROPPED = "trip_dropped";



}
