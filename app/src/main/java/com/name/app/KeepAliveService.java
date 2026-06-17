package com.example.ussdwebview;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;

import android.content.Intent;
import android.os.Build;
import android.os.IBinder;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;



public class KeepAliveService extends Service {



    private static final String CHANNEL_ID = "Fiskon";



    @Override
    public void onCreate() {

        super.onCreate();


        createNotificationChannel();



        Notification notification =
                new NotificationCompat.Builder(
                        this,
                        CHANNEL_ID
                )

                .setContentTitle(
                        "Fiskon Is Running"
                )

                .setContentText(
                        "App Monitoring Active"
                )

                .setSmallIcon(
                        R.drawable.app_icon
                )

                .setOngoing(true)

                .setPriority(
                        NotificationCompat.PRIORITY_LOW
                )

                .build();



        startForeground(
                1,
                notification
        );


    }







    private void createNotificationChannel(){


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){



            NotificationChannel channel =
                    new NotificationChannel(

                            CHANNEL_ID,

                            "Fiskon Background Service",

                            NotificationManager.IMPORTANCE_LOW

                    );



            channel.setDescription(
                    "Keeps Fiskon running"
            );



            NotificationManager manager =
                    getSystemService(
                            NotificationManager.class
                    );



            if(manager != null){

                manager.createNotificationChannel(
                        channel
                );

            }

        }


    }








    @Override
    public int onStartCommand(
            Intent intent,
            int flags,
            int startId
    ){


        return START_STICKY;

    }









    @Override
    public void onDestroy(){


        super.onDestroy();



        // restart service if Android kills it

        Intent restartService =
                new Intent(
                        getApplicationContext(),
                        KeepAliveService.class
                );



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){


            startForegroundService(
                    restartService
            );


        }else{


            startService(
                    restartService
            );

        }


    }







    @Nullable
    @Override
    public IBinder onBind(Intent intent){

        return null;

    }



}