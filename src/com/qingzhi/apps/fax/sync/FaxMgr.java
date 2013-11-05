package com.qingzhi.apps.fax.sync;

import android.content.*;
import android.database.Cursor;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qingzhi.apps.fax.io.model.Attach;
import com.qingzhi.apps.fax.io.model.Fax;
import com.qingzhi.apps.fax.provider.FaxContract;
import com.qingzhi.apps.fax.util.Lists;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaxMgr {

    private static AsyncHttpClient client = new AsyncHttpClient();

    {
        client.removeHeader("");
        client.addHeader("", "");
    }


    public static void init(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isInit = preferences.getBoolean("fax_data_init", false);
        if (!isInit) {
            syncData(context);
        }
    }

    public void uploadAttach(){

    }

    public static void syncData(final Context context) {
        RequestParams params = new RequestParams();
        params.put("r", "gogo");
        params.put("un", "zhang");
        params.put("gt", "gogo:zhang:17d9cc1b-45f7-46f4-9e5b-baff02dcc7df");
        client.get("http://www.qingzhicall.com/ucm/api/fax/get_list", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                JsonObject jsonObject = new JsonParser().parse(response.toString()).getAsJsonObject();
                List<Fax> list = new Gson().fromJson(jsonObject.get("fax_list"), new TypeToken<List<Fax>>() {
                }.getType());

                try {
                    updateData(context, list);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.e("Fax", e.toString());
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                    Log.e("Fax", e.toString());
                }

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putBoolean("fax_data_init", true).commit();
            }
        });
    }

    public static void updateData(Context context, List<Fax> list) throws RemoteException, OperationApplicationException {
        ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
        for (Fax fax : list) {
            ContentProviderOperation.Builder builder = ContentProviderOperation
                    .newInsert(FaxContract.addCallerIsSyncAdapterParameter(
                            FaxContract.Faxs.CONTENT_URI));

            builder.withValue(FaxContract.Faxs.ATTACH_BUNDLE_ACCESS_CODE, fax.attach_bundle_access_code);
            builder.withValue(FaxContract.Faxs.ATTACH_BUNDLE_ID, fax.attach_bundle_id);
            builder.withValue(FaxContract.Faxs.CATEGORY, fax.category);
            builder.withValue(FaxContract.Faxs.CREATE_TIME_STR, fax.create_time_str);
            builder.withValue(FaxContract.Faxs.CREATE_USER, fax.create_user);
            builder.withValue(FaxContract.Faxs.HAS_COMMIT_TO_SERVER, fax.has_commit_to_server);

            builder.withValue(FaxContract.Faxs.HAS_READ, fax.has_read);
            builder.withValue(FaxContract.Faxs.ID, fax.id);
            builder.withValue(FaxContract.Faxs.LAST_SEND_TIME_STR, fax.last_send_time_str);
            builder.withValue(FaxContract.Faxs.RECEIVER_DESC, fax.receiver_desc);
            builder.withValue(FaxContract.Faxs.RECEIVER_PHONE, fax.receiver_phone);
            builder.withValue(FaxContract.Faxs.SEND_ERROR_CODE, fax.send_error_code);
            builder.withValue(FaxContract.Faxs.SEND_ERROR_MESSAGE, fax.send_error_message);
            builder.withValue(FaxContract.Faxs.SENDER_PHONE, fax.sender_phone);
            builder.withValue(FaxContract.Faxs.SENDING_PAGE_COUNT, fax.sending_page_count);

            batch.add(builder.build());
        }

        context.getContentResolver().applyBatch(FaxContract.CONTENT_AUTHORITY, batch);
    }

    /**
     * @param context
     * @param faxId
     */
    public static void deleteById(Context context, String faxId) {
        // TODO  http删除服务端数据
        context.getContentResolver().delete(FaxContract.Faxs.buildFaxUri(faxId),
                null, null);
    }

    /**
     * 登出时清除数据
     *
     * @param context
     */
    public static void clearData(Context context) {
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(FaxContract.BASE_CONTENT_URI, null, null);
    }

    public static Fax parser(Cursor cursor, String[] args) {
        Fax fax = new Fax();

        Class userCla = (Class) fax.getClass();
        Field[] fs = userCla.getDeclaredFields();
        for (String arg : args) {
            String value = cursor.getString(cursor.getColumnIndex(arg));
            for (int i = 0; i < fs.length; i++) {
                Field f = fs[i];
                String type = f.getType().toString();
                String name = f.getName();
                if (arg.equals(name)) {
                    try {
                        if (type.endsWith("int") || type.endsWith("Integer")) {
                            f.set(fax, Integer.valueOf(value));
                        } else {
                            f.set(fax, value);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return fax;
    }


    public void getBundleAttachList(String r, String un, String gt, String bundle_id, String access_code, String all_children) {
        Map<String,String> map = new HashMap<String, String>();
        map.put("r",r);
        map.put("un",un);
        map.put("bundle_id",bundle_id);
        map.put("access_code",access_code);
        map.put("all_children",all_children);
        RequestParams params = new RequestParams(map);
        AsyncHttpClient client1 = new AsyncHttpClient();
        client1.post("api/attach/get_bundle_attach_list",params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONObject response) {
                Gson gson = new Gson();
                Map<String, List<Attach>> map = gson.fromJson(response.optString("children"), new TypeToken<Map<String, List<Attach>>>() {}.getType());
                List<Attach> attachList = gson.fromJson(response.optString("attach_list"), new TypeToken<List<Attach>>() {}.getType());

            }
        });
    }
}
