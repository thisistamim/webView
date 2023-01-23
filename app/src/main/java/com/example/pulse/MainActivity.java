package com.example.pulse;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Context;
import android.content.DialogInterface;

import android.net.NetworkInfo;
import android.net.ConnectivityManager;

import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;

import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.CookieManager;
import android.webkit.SafeBrowsingResponse;
import android.webkit.WebStorage;

import android.widget.Button;
import android.widget.LinearLayout;


public class MainActivity extends AppCompatActivity {
    WebView webView;
    LinearLayout internetConnected,internetNotConnected;
    Button reloadButton;
    private WebSettings webSettings;
    private WebStorage webStorage;
    private CookieManager cookieManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.MyWebApp);
        internetConnected = findViewById(R.id.onInternet);
        internetNotConnected = findViewById(R.id.noInternet);
        reloadButton = findViewById(R.id.reloadBtn);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://www.linkedin.com/pulse/topics/home");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error){
                internetConnected.setVisibility(view.GONE);
                internetNotConnected.setVisibility(view.VISIBLE);
                super.onReceivedError(view, request, error);
            }
        });

        reloadButton.setOnClickListener(new View.OnClickListener() {
            boolean connected = false;
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

            @Override
            public void onClick(View view) {
                webView.reload();

                if((connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) || (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)) {
                    webView.setWebViewClient(new WebViewClient(){
                        public void onPageStarted(WebView view, String url) {
                            internetNotConnected.setVisibility(View.VISIBLE);
                        }

                        public void onPageFinished(WebView view, String url) {
                            internetNotConnected.setVisibility(View.GONE);
                            internetConnected.setVisibility(View.VISIBLE);
                        }

                        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                            internetConnected.setVisibility(view.GONE);
                            internetNotConnected.setVisibility(view.VISIBLE);
                        }
                    });
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Do you want to exit ?");
            builder.setCancelable(false);
            builder.setPositiveButton("Exit", (DialogInterface.OnClickListener) (dialog, which) -> {
                finish();
            });
            builder.setNegativeButton("Later", (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.cancel();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            //mannage cookies;
            webStorage.getInstance().deleteAllData();
            webView.clearFormData();
            webView.clearHistory();
            webView.clearSslPreferences();
            webView.clearCache(true);
            cookieManager.getInstance().removeAllCookies(null);
            cookieManager.getInstance().flush();
        }
    }


    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            return false;
        }
        return true;
    }

}