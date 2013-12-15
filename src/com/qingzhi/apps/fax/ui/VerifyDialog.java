package com.qingzhi.apps.fax.ui;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qingzhi.apps.fax.Constants;
import com.qingzhi.apps.fax.R;
import com.qingzhi.apps.fax.util.ApiSignatureTool;
import com.qingzhi.apps.fax.util.QingRestClient;
import com.qingzhi.apps.fax.util.QingzhiUtil;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class VerifyDialog extends Activity implements View.OnClickListener {
    EditText phone;
    EditText code;
    String deviceId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_dialog);
        phone = (EditText) findViewById(R.id.phone);
        code = (EditText) findViewById(R.id.code);
        findViewById(R.id.get_code_btn).setOnClickListener(this);
        findViewById(R.id.verify_btn).setOnClickListener(this);
    }

    public void getCode() {
        deviceId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        int versionCode = QingzhiUtil.getVersionCode(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put("k", Constants.QINGZHI_APP_KEY);
        map.put("m", phone.getText().toString());
        //若为已开户的手机号，是否发送短信；1 发送，0不发送。
        map.put("old_account_send_sms", "1");
        map.put("cc", "86");
        map.put("di", deviceId);
        map.put("v", String.valueOf(versionCode));
        String sn = ApiSignatureTool.signWithMD5("account/get_code", map, Constants.QINGZHI_APP_SECRET);
        map.put("sn", sn);
        RequestParams params = new RequestParams(map);
        QingRestClient.post("account/get_code", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {

            }

            @Override
            public void onFailure(Throwable error, String content) {
            }
        });
    }

    public void verify() {
        RequestParams params = new RequestParams();
        params.put("m", phone.getText().toString());
        params.put("ac", code.getText().toString());
        params.put("di", deviceId);
        QingRestClient.get("account/verify", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                finish();
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_code_btn:
                getCode();
                break;
            case R.id.verify_btn:
                verify();
                break;
        }
    }
}
