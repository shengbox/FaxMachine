/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.qingzhi.apps.fax.client;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.qingzhi.apps.fax.io.HandlerException;
import com.qingzhi.apps.fax.io.model.ErrorResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;

import static com.qingzhi.apps.fax.util.LogUtils.LOGD;
import static com.qingzhi.apps.fax.util.LogUtils.LOGE;

/**
 * Provides utility methods for communicating with the server.
 */
final public class NetworkUtilities {
    /**
     * The tag used to log to adb console.
     */
    private static final String TAG = "NetworkUtilities";
    /**
     * POST parameter name for the user's account name
     */
    public static final String PARAM_USERNAME = "m";
    public static final String PARAM_DEVICE_ID = "di";
    /**
     * POST parameter name for the user's password
     */
    public static final String PARAM_PASSWORD = "check_md5";

    public static final String BASE_URL = "http://sbucp.weibocall.com/ucp/";
    /**
     * URI for authentication service
     */
    public static final String AUTH_URI = BASE_URL + "account/login";

    private NetworkUtilities() {
    }

    /**
     * Connects to the SampleSync test server, authenticates the provided
     * username and password.
     *
     * @param username The server account username
     * @param password The server account password
     * @return String The authentication token returned by the server (or null)
     */
    public static String authenticate(String username, String password, String deviceId) {

        Map<String, String> map = new HashMap<String, String>();
        map.put(PARAM_USERNAME, username);
        map.put(PARAM_DEVICE_ID, deviceId);
        map.put(PARAM_PASSWORD, password);
        try {
            return executeGet(AUTH_URI, map);
        } catch (IOException e) {
            Log.e("AUTH_URI", e + "");
        }
        return null;
    }

    static int HTTP_CONNECTION_TIMEOUT = 20000;
    static int HTTP_SO_TIMEOUT = 30000;

    private static String executeGet(String url, Map<String, String> map) throws IOException {

        HttpClient httpclient = new DefaultHttpClient();
        HttpParams params = httpclient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, HTTP_CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, HTTP_SO_TIMEOUT);

        HttpPost post = new HttpPost(url);

        ArrayList<BasicNameValuePair> postDate = new ArrayList<BasicNameValuePair>();
        Set<String> set = map.keySet();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            postDate.add(new BasicNameValuePair(key, map.get(key)));
        }
        post.setEntity(new UrlEncodedFormEntity(postDate, HTTP.UTF_8));
        HttpResponse httpResponse = httpclient.execute(post);
        throwErrors(httpResponse);

        HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity != null) {
            String response = EntityUtils.toString(httpEntity);
            LOGD(TAG, response);
            return response;
        }
        return null;
    }

    private static void throwErrors(HttpResponse httpResponse) throws IOException {
        final int status = httpResponse.getStatusLine().getStatusCode();
        if (status < 200 || status >= 300) {
            String errorMessage = null;
            String errorContent = EntityUtils.toString(httpResponse.getEntity());
            try {
                LOGE(TAG, "Error content: " + errorContent);
                ErrorResponse errorResponse = new Gson().fromJson(
                        errorContent, ErrorResponse.class);
                errorMessage = errorResponse == null ? null : errorResponse.error_message;
            } catch (JsonSyntaxException ignored) {
            }

            String exceptionMessage = "Error response "
                    + status + " "
                    + errorContent
                    + (errorMessage == null ? "" : (": " + errorMessage))
                    + " for ";

            // TODO: the API should return 401, and we shouldn't have to parse the message
            throw (errorMessage != null && errorMessage.toLowerCase().contains("auth"))
                    ? new HandlerException.UnauthorizedException(exceptionMessage)
                    : new HandlerException(exceptionMessage);
        }
    }
}
