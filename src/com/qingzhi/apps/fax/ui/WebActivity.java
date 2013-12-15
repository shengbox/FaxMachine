package com.qingzhi.apps.fax.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.qingzhi.apps.fax.R;

public class WebActivity extends Activity {
    public static final String URL = "URL";
    WebView webView;
    String url;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_layout);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        webView = (WebView) findViewById(R.id.webView);
        if (getIntent().hasExtra(URL)) {
            url = getIntent().getStringExtra(URL);
        }

        WebSettings settings = webView.getSettings();
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        webView.requestFocus();
        webView.setWebViewClient(new MyWebViewClient());
        setProgressBarIndeterminateVisibility(true);
        webView.loadUrl(url);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            setProgressBarIndeterminateVisibility(false);
        }

        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            setProgressBarIndeterminateVisibility(false);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class WebFragment extends Fragment {

    }
}
