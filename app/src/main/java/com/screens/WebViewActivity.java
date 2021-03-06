package com.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.view.Window;
import android.webkit.WebView;

import com.Utils;
import com.seesame.R;

import okhttp3.internal.Util;

public class WebViewActivity extends AppCompatActivity {

    private WebView webview;
    String pageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_web_view);
        this.getSupportActionBar().hide();
        initViews();

        weburlCall(pageData);
        //  webview.loadUrl("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/terms-conditions.html?alt=media&token=e08057e9-569a-47bf-b343-8cbbe2b83506");
    }


    private void initViews() {
        webview = findViewById(R.id.webview);
        Intent info = getIntent();
        pageData = info.getExtras().getString("PageInfo");
    }


    private void weburlCall(String pageInfo) {

        String data = pageInfo;

        switch (data) {

            //
            case "Terms of Service":
                webview.loadUrl("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/terms-conditions.html?alt=media&token=e08057e9-569a-47bf-b343-8cbbe2b83506");
                //  webview.loadUrl("https://www.akunatech.com/image/about/akuna_video.mp4");

                break;
            case "Privacy Policy":
                webview.loadUrl("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/privacy-policy.html?alt=media&token=be657fcd-5160-4034-96f7-777bea45f391");
                break;
            case "Contact Us":
                // https://www.budgetsafetywear.com.au/pages/embroidery-2
                // webview.loadUrl("https://www.budgetworkwear.com.au/pages/embroidery-1");
                webview.loadUrl("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/privacy-policy.html?alt=media&token=be657fcd-5160-4034-96f7-777bea45f391");
                break;

            case "FAQ's":

                webview.loadUrl("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/privacy-policy.html?alt=media&token=be657fcd-5160-4034-96f7-777bea45f391");

                break;

            case "Ratings":

                Intent login = new Intent(getApplication(), UserrateListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userId", Utils.userId);
                login.putExtras(bundle);
                startActivity(login);
                finish();

                break;


            case "AppVideo":

                webview.loadUrl("https://www.akunatech.com/image/about/akuna_video.mp4");

                break;


            //

            case "login":
              /*  Intent login = new Intent(WebviewActivity.this, LoginActivity.class);
                startActivity(login);
                finish();*/
                break;

        }


    }


}