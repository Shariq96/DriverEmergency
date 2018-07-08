package com.example.user.driveremergency;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by SHARIK on 10/25/2017.
 */

public class MyAndroidFirebaseMsgService extends FirebaseMessagingService {

    private static final String TAG = "MyAndroidFCMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        String parts = remoteMessage.getNotification().getBody();
        String Parts[] = parts.split(",");
        String lat = Parts[0];
        String mobile_no = Parts[1];

        String color = Parts[2];
        String Customer_id = Parts[3];
        String longi = Parts[4];
        String Clickaction = remoteMessage.getData().get("clicker");

        if(myApplication.isActivityVisible()) {
            Intent intent = new Intent("myFunction");
            intent.putExtra("usermobile_no", mobile_no);
            intent.putExtra("lat", lat);
            intent.putExtra("longi",longi);
            intent.putExtra("token",color);
            intent.putExtra("customer_id",Customer_id);
            intent.putExtra("ClickAction",Clickaction);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
        else
            createNotification(remoteMessage.getNotification().getBody());
    }

    private void createNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        long[] pattern = {500, 500, 500, 500, 500};
        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("EMERGENCY")
                .setContentText(messageBody)
                .setVibrate(pattern)
                .setAutoCancel(true)
                .setLights(Color.RED, 1, 1)
                .setSound(notificationSoundURI)
                .setContentIntent(resultIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, mNotificationBuilder.build());
        getApplicationContext().startActivity(intent);

    }
}