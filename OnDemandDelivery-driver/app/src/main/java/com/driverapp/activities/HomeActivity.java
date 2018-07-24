package com.driverapp.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.driverapp.Application;
import com.driverapp.Constants;
import com.driverapp.R;
import com.driverapp.Utils;
import com.driverapp.adapters.TaskAdapter;
import com.driverapp.models.TaskModel;
import com.driverapp.models.TaskResponse;
import com.driverapp.services.APIClient;
import com.driverapp.services.APIInterface;
import com.driverapp.views.AlertDialog;
import com.teliver.sdk.core.EventListener;
import com.teliver.sdk.core.Teliver;
import com.teliver.sdk.models.UserBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements TaskAdapter.Listner{

    public static final String TAG = "HomeActivity";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    APIInterface apiInterface;
    List<TaskModel> tasks;
    Toolbar toolbar;
    Button btnViewCurrentTask;
    ProgressDialog progressDialog;
    private Application application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        application = (com.driverapp.Application) getApplicationContext();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        initialiseView();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("message"));
        refreshTaskList();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Teliver.identifyUser(new UserBuilder(Constants.DRIVER_ID).setPhone("9860616030").setEmail("rahul431@.gmail.com").setUserType(UserBuilder.USER_TYPE.OPERATOR).registerPush().build());

    }

    private void initialiseView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.home_recyclerview);
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        setTitle(Constants.APP_NAME + " - "+Constants.DRIVER_ID);
        btnViewCurrentTask = findViewById(R.id.home_view_current_task_btn);
        btnViewCurrentTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trackingId = application.getStringInPref(Constants.KEY_TASK_ID);
                if (trackingId==null){
                    showToast("No Current Task Available");
                }else{
                    Intent intent = new Intent(HomeActivity.this,TripActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void refreshTaskList(){

        if (Utils.isOnline(this)) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Getting Tasks..");
            progressDialog.show();
            Call<TaskResponse> call = apiInterface.getTaskList(Constants.API_KEY, Constants.DRIVER_ID);
            call.enqueue(new Callback<TaskResponse>() {
                @Override
                public void onResponse(Call<TaskResponse> call, Response<TaskResponse> response) {
                    TaskResponse taskResponse = response.body();
                    tasks = taskResponse.data.taskList;
                    adapter = new TaskAdapter(tasks, HomeActivity.this);
                    recyclerView.setAdapter(adapter);
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<TaskResponse> call, Throwable t) {
                    call.cancel();
                    progressDialog.dismiss();
                    showToast("Failed to get tasks");
                }
            });
        }
    }

    private void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onTaskClicked(TaskModel task) {

        Intent intent = new Intent(HomeActivity.this,TripActivity.class);
//        intent.putExtra(Constants.KEY_TASK_ID,task.task_id);
//        intent.putExtra(Constants.KEY_TASK_STATUS,task.status);
//        intent.putExtra(Constants.KEY_PICKUP_LATITUDE,task.pickup.lnglat[0]);
//        intent.putExtra(Constants.KEY_PICKUP_LONGITUDE,task.pickup.lnglat[1]);
//        intent.putExtra(Constants.KEY_DROP_LATITUDE,task.drop.lnglat[0]);
//        intent.putExtra(Constants.KEY_DROP_LONGITUDE,task.drop.lnglat[1]);
        startActivity(intent);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String taskID = intent.getStringExtra(Constants.KEY_TASK_ID);
            final String status = intent.getStringExtra(Constants.KEY_TASK_STATUS);
            final Double pickupLat = intent.getDoubleExtra(Constants.KEY_PICKUP_LATITUDE,0.0);
            final Double pickupLong = intent.getDoubleExtra(Constants.KEY_PICKUP_LONGITUDE,0.0);
            final Double dropLat = intent.getDoubleExtra(Constants.KEY_DROP_LATITUDE,0.0);
            final Double dropLong = intent.getDoubleExtra(Constants.KEY_DROP_LONGITUDE,0.0);
            AlertDialog alertDialog = new AlertDialog(HomeActivity.this, "New Request Assigned", new AlertDialog.ClickListener() {
                @Override
                public void onAccepted(View view) {
                    application.storeStringInPref(Constants.KEY_TASK_D,taskID);
                    Teliver.updateDriverAvailability(false);
                    progressDialog = new ProgressDialog(HomeActivity.this);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Accepting Task");
                    progressDialog.show();
                    Teliver.acceptTask(taskID, new EventListener() {
                        @Override
                        public void onSuccess(String response) {
                            progressDialog.dismiss();
                            Log.i(TAG,response);
                            showToast("Task is Accepted");
                            Intent intent = new Intent(HomeActivity.this,TripActivity.class);
                            intent.putExtra(Constants.KEY_TASK_ID,taskID);
                            intent.putExtra(Constants.KEY_TASK_STATUS,status);
                            intent.putExtra(Constants.KEY_PICKUP_LATITUDE,pickupLat);
                            intent.putExtra(Constants.KEY_PICKUP_LONGITUDE,pickupLong);
                            intent.putExtra(Constants.KEY_DROP_LATITUDE,dropLat);
                            intent.putExtra(Constants.KEY_DROP_LONGITUDE,dropLong);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(String reason) {
                            progressDialog.dismiss();
                            Log.i(TAG,reason);
                            showToast("Failed Accept");
                        }
                    });
                }

                @Override
                public void onRejected(View view) {
                    application.storeStringInPref(Constants.KEY_TASK_D,null);
                    progressDialog = new ProgressDialog(HomeActivity.this);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Rejecting Task");
                    progressDialog.show();

                    Teliver.rejectTask(taskID, new EventListener() {
                        @Override
                        public void onSuccess(String response) {
                            progressDialog.dismiss();
                            Log.i(TAG,response);
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


}
