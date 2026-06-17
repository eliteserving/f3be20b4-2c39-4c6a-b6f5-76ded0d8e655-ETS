package com.example.ussdwebview;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_REQUEST = 100;

    private WebView webView;
    private SmsBridge smsBridge;

    public static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        setContentView(R.layout.activity_main);

        setupSystemTheme();

        webView = findViewById(R.id.webview);

        smsBridge = new SmsBridge();

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        webView.addJavascriptInterface(
                smsBridge,
                "AndroidSMS"
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                boolean isDarkMode =
                        (getResources().getConfiguration().uiMode
                                & Configuration.UI_MODE_NIGHT_MASK)
                                == Configuration.UI_MODE_NIGHT_YES;

                String theme = isDarkMode ? "dark" : "light";

                view.evaluateJavascript(
                        "document.documentElement.setAttribute('data-theme', '" + theme + "')",
                        null
                );
            }
        });

        requestSmsPermission();

        webView.loadUrl("file:///android_asset/index.html");
    }

    private void requestSmsPermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.READ_SMS,
                            Manifest.permission.RECEIVE_SMS
                    },
                    SMS_PERMISSION_REQUEST
            );
        }
    }

    public void sendSmsToWebView(String message) {

        if (webView == null) return;

        smsBridge.setLatestSms(message);

        String escaped = message
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\n", "\\n");

        new Handler(Looper.getMainLooper()).post(() ->
                webView.evaluateJavascript(
                        "window.onSmsReceived && window.onSmsReceived('" +
                                escaped + "')",
                        null
                )
        );
    }

    public static class SmsBridge {

        private String latestSms = "";

        @JavascriptInterface
        public String getLatestSms() {
            return latestSms;
        }

        public void setLatestSms(String sms) {
            latestSms = sms;
        }
    }

    public static class SmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (!"android.provider.Telephony.SMS_RECEIVED"
                    .equals(intent.getAction())) {
                return;
            }

            Bundle bundle = intent.getExtras();

            if (bundle == null) {
                return;
            }

            Object[] pdus = (Object[]) bundle.get("pdus");

            if (pdus == null) {
                return;
            }

            for (Object pdu : pdus) {

                SmsMessage sms;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    String format = bundle.getString("format");

                    sms = SmsMessage.createFromPdu(
                            (byte[]) pdu,
                            format
                    );

                } else {

                    sms = SmsMessage.createFromPdu(
                            (byte[]) pdu
                    );
                }

                String message = sms.getMessageBody();

                if (instance != null) {
                    instance.sendSmsToWebView(message);
                }
            }
        }
    }

    private void setupSystemTheme() {

        boolean isDarkMode =
                (getResources().getConfiguration().uiMode
                        & Configuration.UI_MODE_NIGHT_MASK)
                        == Configuration.UI_MODE_NIGHT_YES;

        Window window = getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (isDarkMode) {

                window.setStatusBarColor(
                        Color.parseColor("#121212")
                );

                window.setNavigationBarColor(
                        Color.parseColor("#121212")
                );

            } else {

                window.setStatusBarColor(Color.WHITE);
                window.setNavigationBarColor(Color.WHITE);
            }
        }
    }

    @Override
    public void onConfigurationChanged(
            @NonNull Configuration newConfig
    ) {
        super.onConfigurationChanged(newConfig);

        setupSystemTheme();
    }

    @Override
    public void onBackPressed() {

        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}