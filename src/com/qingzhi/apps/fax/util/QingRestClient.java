package com.qingzhi.apps.fax.util;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qingzhi.apps.fax.BuildConfig;

import static com.qingzhi.apps.fax.util.LogUtils.LOGD;

public class QingRestClient {

    private static final String TAG = "QingRestClient";

    private static final String BASE_URL = "http://sbucp.weibocall.com/ucp/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if (BuildConfig.DEBUG) {
            LOGD(TAG, getAbsoluteUrl(url));
        }
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if (BuildConfig.DEBUG) {
            LOGD(TAG, getAbsoluteUrl(url));
        }
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
