package com.driverapp.services;

import com.driverapp.Constants;
import com.driverapp.models.TaskResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by bridgelabz on 20/07/18.
 */

public interface APIInterface {

    @GET(Constants.API_GET_TASKS)
    Call<TaskResponse> getTaskList(@Query("apikey") String apiKey, @Query("driver_id") String driverId);
}
