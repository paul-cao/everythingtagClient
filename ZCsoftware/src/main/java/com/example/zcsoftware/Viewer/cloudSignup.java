package com.example.zcsoftware.Viewer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.zcsoftware.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by Taotao on 9/3/13.
 */
public class cloudSignup extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cloudsignup);

        WebView myWebView = (WebView) findViewById(R.id.wvCldSign);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        Intent intentname = getIntent();
        final String urlStr = (String) intentname.getSerializableExtra("URL");

        //let application handle loading url instead of letting android launch default browser app
        myWebView.setWebViewClient(new WebViewClient() {
            @Override //override default behavior
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(urlStr);
                return true;
            }
        });



        myWebView.loadUrl(urlStr);
    }
}