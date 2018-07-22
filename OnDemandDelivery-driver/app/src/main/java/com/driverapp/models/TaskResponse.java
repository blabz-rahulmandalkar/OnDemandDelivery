package com.driverapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TaskResponse {

    @SerializedName("message")
    public String message;
    @SerializedName("success")
    public boolean success;
    @SerializedName("data")
    public Data data;

    public class Data{
        @SerializedName("task_list")
        public List<TaskModel> taskList;
        @SerializedName("total")
        public int total;
    }
}
