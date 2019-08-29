package com.fifthera.malldemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.fifthera.ecmall.ECWebView;
import com.fifthera.ecmall.ErrorCode;
import com.fifthera.ecmall.JSApi;
import com.fifthera.ecmall.OnApiResponseListener;

import java.util.HashMap;
import java.util.Map;


public class MallActivity extends AppCompatActivity {
    private Context mContext;
    private ECWebView mWebView;
    private JSApi mApi;
    private Button mButton;

    //用来控制使用测试服 or 正式服
    public static boolean isDebug = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mall_activity_layout);
        mContext = this;
        mWebView = findViewById(R.id.ec_webview);
        mButton = findViewById(R.id.refresh_button);
        mApi = new JSApi(this);
        mWebView.addJavascriptObject(mApi);
        mApi.setOnApiResponseListener(new OnApiResponseListener() {
            @Override
            public void fail(int i) {
                //Token 失效的情况下需要重新授权
                if (i == ErrorCode.TOKEN_FAIL) {
                    mWebView.loadUrl(getAuthorityUrl());
                }
            }

            @Override
            public void goBack() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                });
            }

            @Override
            public void consumeSuccess() {
                //用户金币兑换淘礼金成功后后的回调
                mWebView.refresh();
            }

            @Override
            public void earnGold() {

            }
        });

        //模拟用户账户信息发生变化
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.refresh();
            }
        });

        mWebView.loadUrl(getAuthorityUrl());
    }


    String getAuthorityUrl() {
        Map<String, Object> map = new HashMap<>();
        String uid = "xxxxxxxxxxxx";//用户的唯一id
        String clientId = "xxxxxxxxxxxxxxx"; //兜推后台申请的clientId
        String clientSecret = "xxxxxxxxxxxx"; //兜推后台申请的clientSecret
        String token = "xxxxxxxxxxxxx"; //流量主服务端token，可根据实际情况决定是否需要此参数
        long currentTime = System.currentTimeMillis() / 1000;
        map.put("uid", uid);
        map.put("timestamp", currentTime);
        map.put("client_id", clientId);
        map.put("type", "page.taolijin");
        map.put("token", token);
        if (mWebView != null) {
            String url = mWebView.getAuthorityUrl(clientSecret, map, isDebug);
            return url;
        } else {
            return "";
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
