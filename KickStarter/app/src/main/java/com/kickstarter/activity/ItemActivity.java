package com.kickstarter.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.kickstarter.R;

public class ItemActivity extends AppCompatActivity {
    ProgressDialog progDailog;
    WebView wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        init();

        String uri= getIntent().getStringExtra("uri");

        wv = (WebView) findViewById(R.id.webView);
        wv.getSettings().setPluginState(WebSettings.PluginState.ON);
        wv.getSettings().setAllowFileAccess(true);
        wv.setHorizontalScrollBarEnabled(true);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);

        wv.getSettings().setSupportZoom(true);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setDisplayZoomControls(true);

        wv.getSettings().setAppCacheMaxSize( 100 * 1024 * 1024 ); // 100MB
        wv.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
        wv.getSettings().setAllowFileAccess( true );
        wv.getSettings().setAppCacheEnabled( true );
        wv.getSettings().setJavaScriptEnabled( true );
        wv.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT ); // load online by default

        if ( !isNetworkAvailable() ) { // loading offline
            wv.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        }

        wv.loadUrl(uri);
        wv.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Toast.makeText(ItemActivity.this, "Loading Page...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                Toast.makeText(ItemActivity.this, "Loading Done...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void init() {
//        progDailog = ProgressDialog.show(ItemActivity.this, "Loading","Please wait...", true);
        //progDailog.setCancelable(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        wv.destroy();
        this.finish();

    }
}
