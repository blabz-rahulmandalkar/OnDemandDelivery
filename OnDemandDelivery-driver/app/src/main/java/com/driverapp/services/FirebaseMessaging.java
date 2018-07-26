package com.driverapp.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.driverapp.Application;
import com.driverapp.Constants;
import com.driverapp.R;
import com.driverapp.Utils;
import com.driverapp.activities.HomeActivity;
import com.driverapp.activities.TripActivity;
import com.driverapp.models.TaskModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.teliver.sdk.core.Teliver;
import com.teliver.sdk.models.NotificationData;

import org.json.JSONObject;

import java.util.Map;


public class FirebaseMessaging extends FirebaseMessagingService {

    private Application application;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("TELIVER::", "onMessageReceived: PUSH TRY == "+remoteMessage.toString());
        try {
            if (Teliver.isTeliverPush(remoteMessage)) {
                Map<String, String> pushData = remoteMessage.getData();
                final NotificationData data = new GsonBuilder().create().fromJson(pushData.get("description"), NotificationData.class);
                Log.d("TELIVER::", "onMessageReceived: " + "tracking id ==" + data.getTrackingID()
                        + "commnad ==" + data.getCommand() + "message == " + data.getMessage());
                Intent intent = new Intent(this, TripActivity.class);
                JSONObject jsonObject = new JSONObject(data.getPayload());
                final TaskModel task = new Gson().fromJson(jsonObject.toString(),TaskModel.class);
                intent.putExtra(Constants.KEY_TASK_ID,task.task_id);
                intent.putExtra(Constants.KEY_TASK_STATUS,task.status);
                intent.putExtra(Constants.KEY_PICKUP_LATITUDE,task.pickup.lnglat[0]);
                intent.putExtra(Constants.KEY_PICKUP_LONGITUDE,task.pickup.lnglat[1]);
                intent.putExtra(Constants.KEY_DROP_LATITUDE,task.drop.lnglat[0]);
                intent.putExtra(Constants.KEY_DROP_LONGITUDE,task.drop.lnglat[1]);
                intent.setAction("message");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                sendPushNotification(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPushNotification(NotificationData data) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        notification.setContentTitle("Teliver");
        notification.setContentText(data.getMessage());
        notification.setSmallIcon(R.drawable.ic_scooter);
        notification.setLargeIcon(Utils.getBitmapIcon(this));
        notification.setStyle(new NotificationCompat.BigTextStyle().bigText(data.getMessage()).setBigContentTitle("Teliver"));
        notification.setAutoCancel(true);
        notification.setPriority(Notification.PRIORITY_MAX);
        notification.setDefaults(Notification.DEFAULT_ALL);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification.build());
    }


}


