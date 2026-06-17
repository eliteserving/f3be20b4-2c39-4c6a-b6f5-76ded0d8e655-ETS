package com.example.ussdwebview;



import android.app.*;
import android.content.*;
import android.os.*;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;



public class KeepAliveService extends Service {



private static final String CHANNEL =
        "Fiskon";




@Override
public void onCreate(){


super.onCreate();



createChannel();



Intent open =
new Intent(
        this,
        MainActivity.class
);



PendingIntent click =
PendingIntent.getActivity(

        this,
        0,
        open,

        PendingIntent.FLAG_IMMUTABLE |
        PendingIntent.FLAG_UPDATE_CURRENT

);



Notification notification =

new NotificationCompat.Builder(
        this,
        CHANNEL
)

.setContentTitle(
        "Fiskon Running"
)

.setContentText(
        "SMS monitoring active"
)

.setSmallIcon(
        R.drawable.app_icon
)

.setOngoing(true)

.setContentIntent(click)

.build();



startForeground(
        1,
        notification
);



}







private void createChannel(){



if(Build.VERSION.SDK_INT >= 26){


NotificationChannel c =
new NotificationChannel(

CHANNEL,

"Fiskon Service",

NotificationManager.IMPORTANCE_LOW

);



getSystemService(
NotificationManager.class
)
.createNotificationChannel(c);



}


}






@Override
public int onStartCommand(
Intent i,
int f,
int id
){


return START_STICKY;


}







@Nullable
@Override
public IBinder onBind(Intent i){

return null;

}


}