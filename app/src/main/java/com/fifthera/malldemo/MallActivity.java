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

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

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
                    loadUrl();
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
                mWebView.refresh();
            }
        });

        //模拟用户账户信息发生变化
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.refresh();
            }
        });

        loadUrl();
    }

    //sdk授权操作

    private void loadUrl() {
        // 用户id
        String uid = "123456789";
        //时间戳
        long currnetTime = System.currentTimeMillis() / 1000;
        //兜推流量主平台创建的福利商城 clientId和clientSecret，
        String clientId = "xxxxxxxxxxxx";
        String clientSecret = "xxxxxxxxxxxxx";
        //获取sign
        String sign = getSign(clientId, clientSecret, currnetTime, uid);
        StringBuilder str = new StringBuilder();
        if (isDebug) {
            str.append("https://ec-api-test.thefifthera.com");
        } else {
            str.append("https://ec-api.thefifthera.com");
        }
        str.append("/h5/v1/auth/redirect?client_id=")
                .append(clientId)
                .append("&sign=")
                .append(sign)
                .append("&timestamp=")
                .append(currnetTime)
                .append("&uid=")
                .append(uid)
                .append("&type=page.taolijin");
        String url = str.toString();
        mWebView.loadUrl(url);
    }

    private String getSign(String clientId, String clientSecret, long currentTime, String uid) {
        StringBuilder str = new StringBuilder();
        str.append(clientSecret);
        str.append("client_id").append(clientId)
                .append("timestamp").append(currentTime)
                .append("type").append("page.taolijin")
                .append("uid").append(uid);
        str.append(clientSecret);
        String s = new String(Hex.encodeHex(DigestUtils.md5(str.toString())));
        return s.toUpperCase();
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
