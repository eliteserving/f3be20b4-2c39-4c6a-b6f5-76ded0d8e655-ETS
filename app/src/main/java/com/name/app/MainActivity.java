package com.example.ussdwebview;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupSystemTheme();
        webView = findViewById(R.id.webview);
        webView.setVisibility(View.INVISIBLE);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {WebView.setWebContentsDebuggingEnabled(true);}
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                boolean isDarkMode =(getResources().getConfiguration().uiMode& Configuration.UI_MODE_NIGHT_MASK)== Configuration.UI_MODE_NIGHT_YES;
                String theme = isDarkMode ? "dark" : "light";
                view.evaluateJavascript("document.documentElement.setAttribute('data-theme','"+ theme + "')",value -> {view.setVisibility(View.VISIBLE);});
            }
        });
        webView.loadUrl("file:///android_asset/index.html");
    }
    private void setupSystemTheme() {
        boolean isDarkMode =(getResources().getConfiguration().uiMode& Configuration.UI_MODE_NIGHT_MASK)== Configuration.UI_MODE_NIGHT_YES;
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isDarkMode) {
                window.setStatusBarColor(Color.parseColor("#121212"));
                window.setNavigationBarColor(Color.parseColor("#121212"));
            } else {
                window.setStatusBarColor(Color.WHITE);
                window.setNavigationBarColor(Color.WHITE);
            }
        }
        if (!isDarkMode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (window.getInsetsController() != null) {window.getInsetsController().setSystemBarsAppearance(android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS| android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS| android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);}
            } else {
                View decor = window.getDecorView();
                int flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                }
                decor.setSystemUiVisibility(flags);
            }
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                getWindow().getDecorView().setSystemUiVisibility(0);
            }
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setupSystemTheme();
        boolean isDarkMode =(newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK)== Configuration.UI_MODE_NIGHT_YES;
        String theme = isDarkMode ? "dark" : "light";
        webView.evaluateJavascript("document.documentElement.setAttribute('data-theme','"+ theme + "')",null);
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