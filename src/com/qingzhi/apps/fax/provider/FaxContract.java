package com.qingzhi.apps.fax.provider;


import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

public class FaxContract {


    interface FaxColumns {
        String ATTACH_BUNDLE_ACCESS_CODE = "attach_bundle_access_code";
        String ATTACH_BUNDLE_ID = "attach_bundle_id";
        String canRemove = "canRemove";
        String canSave = "canSave";
        String CATEGORY = "category";
        String CREATE_TIME_STR = "create_time_str";
        String CREATE_USER = "create_user";
        String duration_seconds = "duration_seconds";
        String enterprise_id = "enterprise_id";
        String HAS_COMMIT_TO_SERVER = "has_commit_to_server";
        String HAS_READ = "has_read";
        String ID = "id";
        String IS_SUCCESS_SEND = "isSuccessSend";
        String LAST_SEND_TIME_STR = "last_send_time_str";
        String MEMO = "memo";
        String RECEIVER_DESC = "receiver_desc";
        String RECEIVER_PHONE = "receiver_phone";
        String SEND_ERROR_CODE = "send_error_code";
        String SEND_ERROR_MESSAGE = "send_error_message";
        String SENDER_PHONE = "sender_phone";
        String SENDING_PAGE_COUNT = "sending_page_count";
    }

    public static final String CONTENT_AUTHORITY = "com.qingzhi.apps.fax";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String PATH_FRIENDS = "faxs";

    public static class Faxs implements FaxColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FRIENDS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.uc.fax";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.uc.fax";

        public static Uri buildFaxUri(String faxId) {
            return CONTENT_URI.buildUpon().appendPath(faxId).build();
        }

        public static String getFaxId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    interface Attach {
        String access_code = "access_code";
        String creator_id = "creator_id";
        String creator_name = "creator_name";
        String enable_thumb = "enable_thumb";
        String enterprise_id = "enterprise_id";
        String file_name = "file_name";
        String height = "height";
        String id = "id";
        String length = "length";
        String md5 = "md5";
        String memo = "memo";
        String mime_type = "mime_type";
        String size = "size";
        String storage_id = "storage_id";
        String time = "time";
        String width = "width";
        String pages_bundle_id = "pages_bundle_id";
    }

    public static Uri addCallerIsSyncAdapterParameter(Uri uri) {
        return uri.buildUpon().appendQueryParameter(
                ContactsContract.CALLER_IS_SYNCADAPTER, "true").build();
    }
}
