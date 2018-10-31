package com.example.anamaria.licentafirsttry.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.anamaria.licentafirsttry.R;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        WebView webView;
        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Toast.makeText(WebViewActivity.this, R.string.errorURL, Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = getIntent();
        String wordToSearch = intent.getStringExtra("search");
        String urlToLoad = getString(R.string.dexURL) + wordToSearch ;
        webView.loadUrl(urlToLoad);
        setContentView(webView);
    }
}
