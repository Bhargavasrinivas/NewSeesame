package com.seesame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

public class VideoActivity extends Activity {

    private WebView webview_videoVw;
    private ImageView img_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        webview_videoVw = findViewById(R.id.webview_videoVw);
        img_close = findViewById(R.id.img_close);

        // webview_videoVw.setBackgroundResource(R.drawable.sharebanner);
        webview_videoVw.getSettings().setLoadWithOverviewMode(true);
        webview_videoVw.getSettings().setUseWideViewPort(false);
        webview_videoVw.getSettings().setSupportZoom(false);
        webview_videoVw.getSettings().setJavaScriptEnabled(true);
        webview_videoVw.setBackgroundColor(Color.TRANSPARENT);
        webview_videoVw.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webview_videoVw.loadUrl("https://www.akunatech.com/image/about/akuna_video.mp4");

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


    }

}