package com.example.ussdwebview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

    @Override

    public void onReceive(Context context,Intent intent){

        Bundle b =intent.getExtras();

        if(b != null){

        Object[] pdus =(Object[]) b.get("pdus");

            if(pdus != null){

                for(Object p : pdus){

                    SmsMessage sms =
                    SmsMessage.createFromPdu((byte[])p);

                    android.util.Log.d("Fiskon",sms.getOriginatingAddress()+":"+sms.getMessageBody());

                }

            }

        }

    }

}