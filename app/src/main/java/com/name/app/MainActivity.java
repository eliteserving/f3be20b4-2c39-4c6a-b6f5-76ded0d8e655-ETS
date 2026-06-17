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


    private WebView webView;


    private static final int SMS_PERMISSION = 200;
    private static final int NOTIFICATION_PERMISSION = 300;




    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);



        // start foreground service permission flow
        requestNotificationPermission();



        setupSystemTheme();



        webView = findViewById(R.id.webview);



        WebSettings settings =
                webView.getSettings();


        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){

            WebView.setWebContentsDebuggingEnabled(true);

        }




        webView.addJavascriptInterface(
                new SmsBridge(),
                "AndroidSMS"
        );




        webView.setWebViewClient(
                new WebViewClient(){


                    @Override
                    public void onPageFinished(
                            WebView view,
                            String url
                    ){

                        super.onPageFinished(view,url);



                        boolean dark =
                                (getResources()
                                .getConfiguration()
                                .uiMode
                                &
                                Configuration.UI_MODE_NIGHT_MASK)
                                ==
                                Configuration.UI_MODE_NIGHT_YES;



                        String theme =
                                dark ? "dark" : "light";



                        view.evaluateJavascript(

                                "document.documentElement.setAttribute('data-theme','"
                                + theme +
                                "')",

                                null
                        );

                    }

                }
        );



        requestSmsPermission();



        webView.loadUrl(
                "file:///android_asset/index.html"
        );


    }








    private void requestNotificationPermission(){



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){



            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            )
            != PackageManager.PERMISSION_GRANTED){



                ActivityCompat.requestPermissions(

                        this,

                        new String[]{
                                Manifest.permission.POST_NOTIFICATIONS
                        },

                        NOTIFICATION_PERMISSION
                );



            }else{


                startKeepAliveService();


            }



        }else{


            startKeepAliveService();


        }


    }









    private void startKeepAliveService(){



        Intent intent =
                new Intent(
                        this,
                        KeepAliveService.class
                );



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){


            startForegroundService(intent);


        }else{


            startService(intent);

        }


    }











    private void requestSmsPermission(){



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



            JSONArray smsList =
                    new JSONArray();



            try {



                Uri uri =
                        Uri.parse(
                                "content://sms/"
                        );



                Cursor cursor =
                        getContentResolver()
                        .query(
                                uri,
                                null,
                                null,
                                null,
                                "date DESC"
                        );



                if(cursor != null){



                    int id =
                            cursor.getColumnIndex("_id");


                    int sender =
                            cursor.getColumnIndex("address");


                    int body =
                            cursor.getColumnIndex("body");


                    int date =
                            cursor.getColumnIndex("date");




                    while(cursor.moveToNext()){



                        JSONObject sms =
                                new JSONObject();



                        sms.put(
                                "id",
                                cursor.getString(id)
                        );


                        sms.put(
                                "sender",
                                cursor.getString(sender)
                        );


                        sms.put(
                                "message",
                                cursor.getString(body)
                        );


                        sms.put(
                                "date",
                                cursor.getString(date)
                        );



                        smsList.put(sms);


                    }



                    cursor.close();

                }



            }catch(Exception e){

                e.printStackTrace();

            }



            return smsList.toString();


        }


    }









    private void setupSystemTheme(){



        boolean darkMode =
                (getResources()
                .getConfiguration()
                .uiMode
                &
                Configuration.UI_MODE_NIGHT_MASK)
                ==
                Configuration.UI_MODE_NIGHT_YES;



        Window window =
                getWindow();




        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){



            if(darkMode){


                window.setStatusBarColor(
                        Color.parseColor("#121212")
                );


                window.setNavigationBarColor(
                        Color.parseColor("#121212")
                );


            }else{


                window.setStatusBarColor(
                        Color.WHITE
                );


                window.setNavigationBarColor(
                        Color.WHITE
                );

            }

        }





        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){


            if(window.getInsetsController()!=null){


                if(!darkMode){


                    window.getInsetsController()
                    .setSystemBarsAppearance(

                            android.view.WindowInsetsController
                            .APPEARANCE_LIGHT_STATUS_BARS |

                            android.view.WindowInsetsController
                            .APPEARANCE_LIGHT_NAVIGATION_BARS,


                            android.view.WindowInsetsController
                            .APPEARANCE_LIGHT_STATUS_BARS |

                            android.view.WindowInsetsController
                            .APPEARANCE_LIGHT_NAVIGATION_BARS

                    );


                }


            }


        }


    }











    @Override
    public void onConfigurationChanged(
            @NonNull Configuration config
    ){

        super.onConfigurationChanged(config);


        setupSystemTheme();

    }









    @Override
    public void onRequestPermissionsResult(

            int requestCode,

            @NonNull String[] permissions,

            @NonNull int[] grantResults

    ){



        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );



        if(requestCode == NOTIFICATION_PERMISSION){


            startKeepAliveService();


        }


    }










    @Override
    public void onBackPressed(){



        if(webView != null &&
                webView.canGoBack()){


            webView.goBack();



        }else{


            super.onBackPressed();

        }


    }


}