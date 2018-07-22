package com.driverapp.models;

import com.google.gson.annotations.SerializedName;

public class TaskModel {

    @SerializedName("time")
    public String time;
    @SerializedName("notes")
    public String notes;
    @SerializedName("status")
    public String status;
    @SerializedName("task_id")
    public String task_id;
    @SerializedName("order_id")
    public String order_id;
    @SerializedName("drop")
    public LocationInfo drop;
    @SerializedName("pickup")
    public LocationInfo pickup;

    public class LocationInfo{
        @SerializedName("lnglat")
        public double[] lnglat;
        @SerializedName("address")
        public String address;
        @SerializedName("customer")
        public Customer customer;
    }

    public class Customer{
        @SerializedName("name")
        public String name;
        @SerializedName("mobile")
        public String mobile;
    }

}
