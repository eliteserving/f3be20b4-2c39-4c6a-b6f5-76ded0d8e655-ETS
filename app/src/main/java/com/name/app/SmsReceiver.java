package com.example.ussdwebview;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;



public class SmsReceiver extends BroadcastReceiver {



@Override
public void onReceive(
        Context context,
        Intent intent
){



Bundle data =
        intent.getExtras();



if(data != null){



Object[] pdus =
        (Object[]) data.get("pdus");



if(pdus != null){



for(Object p : pdus){



SmsMessage sms =
SmsMessage.createFromPdu(
        (byte[])p
);



String sender =
        sms.getOriginatingAddress();



String message =
        sms.getMessageBody();




android.util.Log.d(

        "Fiskon SMS",

        sender + " : " + message

);


}


}


}


}

}