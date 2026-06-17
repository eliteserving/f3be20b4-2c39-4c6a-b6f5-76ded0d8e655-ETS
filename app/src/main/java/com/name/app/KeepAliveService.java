package com.example.ussdwebview;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class KeepAliveService extends Service {

    private static final String CHANNEL_ID ="Fiskon";

    @Override

    public void onCreate(){

        super.onCreate();

        createChannel();

        Intent open =new Intent(this,MainActivity.class);

        open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent click =PendingIntent.getActivity(this,0,open,PendingIntent.FLAG_UPDATE_CURRENT |PendingIntent.FLAG_IMMUTABLE);

        Notification notification =new NotificationCompat.Builder(this,CHANNEL_ID)

        .setContentTitle("Fiskon Running")

        .setContentText("SMS monitoring active")

        .setSmallIcon(R.drawable.app_icon)

        .setOngoing(true)

        .setContentIntent(click)

        .build();

        startForeground(1,notification);

    }

    private void createChannel(){

        if(Build.VERSION.SDK_INT >= 26){

            NotificationChannel c =new NotificationChannel(CHANNEL_ID,"Fiskon",NotificationManager.IMPORTANCE_LOW);

            getSystemService(NotificationManager.class)

            .createNotificationChannel(c);

        }

    }

    @Override

    public int onStartCommand(Intent i,int flags,int id){

        return START_STICKY;

    }

    @Nullable

    @Override

    public IBinder onBind(Intent i){return null;}

}