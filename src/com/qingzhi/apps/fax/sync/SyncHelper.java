package com.qingzhi.apps.fax.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.qingzhi.apps.fax.Constants;
import com.qingzhi.apps.fax.client.NetworkUtilities;
import com.qingzhi.apps.fax.io.HandlerException;
import com.qingzhi.apps.fax.io.JSONHandler;
import com.qingzhi.apps.fax.io.MeetingHandler;
import com.qingzhi.apps.fax.io.model.ErrorResponse;
import com.qingzhi.apps.fax.provider.FaxContract;
import com.qingzhi.apps.fax.util.AccountUtils;
import com.qingzhi.apps.fax.util.ApiSignatureTool;
import com.qingzhi.apps.fax.util.UIUtils;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static com.qingzhi.apps.fax.util.LogUtils.*;

public class SyncHelper {

    private static final String TAG = makeLogTag(SyncHelper.class);

    static {
        // Per http://android-developers.blogspot.com/2011/09/androids-http-clients.html
        if (!UIUtils.hasFroyo()) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    public static final int FLAG_SYNC_LOCAL = 0x1;
    public static final int FLAG_SYNC_REMOTE = 0x2;

    private static final int LOCAL_VERSION_CURRENT = 19;

    static int HTTP_CONNECTION_TIMEOUT = 20000;
    static int HTTP_SO_TIMEOUT = 30000;

    private Context mContext;
    private String mAuthToken;
    private String mUserAgent;

    public SyncHelper(Context context) {
        mContext = context;
        mUserAgent = buildUserAgent(context);
    }

    /**
     * Loads conference information (sessions, rooms, tracks, speakers, etc.)
     * from a local static cache data and then syncs down data from the
     * Conference API.
     *
     * @param syncResult Optional {@link android.content.SyncResult} object to populate.
     * @throws java.io.IOException
     */
    public void performSync(SyncResult syncResult, int flags) throws IOException {
        mAuthToken = AccountUtils.getAuthToken(mContext);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        final int localVersion = prefs.getInt("local_data_version", 0);

        // Bulk of sync work, performed by executing several fetches from
        // local and online sources.
        final ContentResolver resolver = mContext.getContentResolver();
        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        String syncTime = sp.getString(Constants.FRIEND_SYNC_TIME, "");

        AccountManager am = AccountManager.get(mContext);
        Account[] accounts = am.getAccountsByType(AccountUtils.ACCOUNT_TYPE);
        String q = am.getUserData(accounts[0], "q");
        String token = am.getPassword(accounts[0]);

        LOGI(TAG, "Performing sync");

        if (isOnline()) {
            try {
                final long startRemote = System.currentTimeMillis();
                LOGI(TAG, "Remote syncing sessions");
                Map<String, String> map = new HashMap<String, String>();
                map.put("q", q);
                map.put("query_direction", "2");
                String sn = ApiSignatureTool.signWithMD5("conf/get_list", map, token);
                map.put("sn", sn);
                batch.addAll(executeGet(NetworkUtilities.BASE_URL + "conf/get_list", map, new MeetingHandler(mContext)));
                // GET_ALL_SESSIONS covers the functionality GET_MY_SCHEDULE provides here.
                LOGD(TAG, "Remote sync took " + (System.currentTimeMillis() - startRemote) + "ms");
                if (syncResult != null) {
                    ++syncResult.stats.numUpdates;
                    ++syncResult.stats.numEntries;
                }

            } catch (HandlerException.UnauthorizedException e) {
                LOGI(TAG, "Unauthorized; getting a new auth token.", e);
                if (syncResult != null) {
                    ++syncResult.stats.numAuthExceptions;
                }
                AccountUtils.invalidateAuthToken(mContext);
            }
        }

        try {

//            int delete = resolver.delete(ScheduleContract.Friends.CONTENT_URI,
//                    ScheduleContract.Friends.TYPE + "= ?", new String[]{"1"});
//            Log.d(TAG, "delete group " + delete);

            // Apply all queued up remaining batch operations (only remote content at this point).
            resolver.applyBatch(FaxContract.CONTENT_AUTHORITY, batch);
        } catch (RemoteException e) {
            throw new RuntimeException("Problem applying batch operation", e);
        } catch (OperationApplicationException e) {
            throw new RuntimeException("Problem applying batch operation", e);
        }
    }

    /**
     * Build and return a user-agent string that can identify this application
     * to remote servers. Contains the package name and version code.
     */
    private static String buildUserAgent(Context context) {
        String versionName = "unknown";
        int versionCode = 0;

        try {
            final PackageInfo info = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        return context.getPackageName() + "/" + versionName + " (" + versionCode + ") (gzip)";
    }

    private ArrayList<ContentProviderOperation> executeGet(String url, Map<String, String> map,
                                                           JSONHandler handler) throws IOException {
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

        String response = "";
        HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity != null) {
            response = EntityUtils.toString(httpEntity);
            LOGD(TAG, response);
        }
        return handler.parse(response);
    }

    private static void throwErrors(HttpResponse httpResponse) throws IOException {
        final int status = httpResponse.getStatusLine().getStatusCode();
        if (status < 200 || status >= 300) {
            String errorMessage = null;
            String errorContent = EntityUtils.toString(httpResponse.getEntity());
            try {
                LOGV(TAG, "Error content: " + errorContent);
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

    private static String readInputStream(InputStream inputStream)
            throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String responseLine;
        StringBuilder responseBuilder = new StringBuilder();
        while ((responseLine = bufferedReader.readLine()) != null) {
            responseBuilder.append(responseLine);
        }
        return responseBuilder.toString();
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
