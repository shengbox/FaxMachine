package com.qingzhi.apps.fax.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import com.qingzhi.apps.fax.provider.FaxContract.FaxColumns;

public class FaxDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "fax.db";
    private static final int VER_SESSION_TYPE = 2;
    private static final int DATABASE_VERSION = VER_SESSION_TYPE;

    interface Tables {
        String FAX = "fax";
    }

    public FaxDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.FAX + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + FaxColumns.ATTACH_BUNDLE_ACCESS_CODE + " TEXT,"
                + FaxColumns.ATTACH_BUNDLE_ID + " INTEGER DEFAULT 0,"
                + FaxColumns.CATEGORY + " TEXT,"
                + FaxColumns.CREATE_TIME_STR + " TEXT,"
                + FaxColumns.CREATE_USER + " TEXT,"
                + FaxColumns.HAS_COMMIT_TO_SERVER + " INTEGER,"
                + FaxColumns.HAS_READ + " INTEGER,"
                + FaxColumns.ID + " TEXT,"
                + FaxColumns.IS_SUCCESS_SEND + " TEXT,"
                + FaxColumns.LAST_SEND_TIME_STR + " TEXT,"
                + FaxColumns.MEMO + " TEXT,"
                + FaxColumns.RECEIVER_DESC + " TEXT,"
                + FaxColumns.RECEIVER_PHONE + " TEXT,"
                + FaxColumns.SEND_ERROR_CODE + " INTEGER,"
                + FaxColumns.SEND_ERROR_MESSAGE + " TEXT,"
                + FaxColumns.SENDER_PHONE + " TEXT,"
                + FaxColumns.SENDING_PAGE_COUNT + " TEXT,"
                + "UNIQUE (" + FaxColumns.ID + ") ON CONFLICT REPLACE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}
