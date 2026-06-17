package com.example.ussdwebview;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONObject;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



public class MainActivity extends AppCompatActivity {


    WebView webView;


    private static final int SMS_PERMISSION = 200;



    @Override
    protected void onCreate(Bundle b){


        super.onCreate(b);


        setContentView(R.layout.activity_main);



        startKeepAlive();



        setupSystemTheme();



        webView =
                findViewById(R.id.webview);



        WebSettings settings =
                webView.getSettings();


        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);



        webView.addJavascriptInterface(
                new SmsBridge(),
                "AndroidSMS"
        );



        webView.setWebViewClient(
                new WebViewClient()
        );



        requestSms();



        webView.loadUrl(
                "file:///android_asset/index.html"
        );

    }







    private void startKeepAlive(){


        Intent i =
                new Intent(
                        this,
                        KeepAliveService.class
                );


        if(Build.VERSION.SDK_INT >= 26){

            startForegroundService(i);

        }else{

            startService(i);

        }

    }








    private void requestSms(){


        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
        )
        != PackageManager.PERMISSION_GRANTED){



            ActivityCompat.requestPermissions(

                    this,

                    new String[]{
                            Manifest.permission.READ_SMS,
                            Manifest.permission.RECEIVE_SMS
                    },

                    SMS_PERMISSION

            );

        }

    }









    public class SmsBridge {


        @JavascriptInterface
        public String getAllSms(){


            JSONArray array =
                    new JSONArray();



            Cursor c =
                    getContentResolver()
                    .query(

                            Uri.parse(
                            "content://sms/"
                            ),

                            null,
                            null,
                            null,
                            "date DESC"

                    );



            if(c != null){


                while(c.moveToNext()){


                    try{


                        JSONObject sms =
                                new JSONObject();


                        sms.put(
                                "sender",
                                c.getString(
                                c.getColumnIndex("address"))
                        );


                        sms.put(
                                "message",
                                c.getString(
                                c.getColumnIndex("body"))
                        );


                        sms.put(
                                "date",
                                c.getString(
                                c.getColumnIndex("date"))
                        );


                        array.put(sms);


                    }catch(Exception e){}


                }



                c.close();

            }



            return array.toString();


        }


    }








    private void setupSystemTheme(){


        boolean dark =
        (getResources()
        .getConfiguration()
        .uiMode
        &
        Configuration.UI_MODE_NIGHT_MASK)
        ==
        Configuration.UI_MODE_NIGHT_YES;



        Window w =
                getWindow();



        w.setStatusBarColor(
                dark
                ?
                Color.DKGRAY
                :
                Color.WHITE
        );

    }

}