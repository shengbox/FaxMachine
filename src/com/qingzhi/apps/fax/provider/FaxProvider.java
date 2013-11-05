package com.qingzhi.apps.fax.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.qingzhi.apps.fax.provider.FaxDatabase.Tables;
import com.qingzhi.apps.fax.util.SelectionBuilder;

public class FaxProvider extends ContentProvider {

    private FaxDatabase mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int FAXS = 100;
    private static final int FAXS_ID = 101;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FaxContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "faxs", FAXS);
        matcher.addURI(authority, "faxs/*", FAXS_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new FaxDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            default: {
                final SelectionBuilder builder = buildExpandedSelection(uri, match);
                return builder.where(selection, selectionArgs).query(db, projection, sortOrder);
            }
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAXS: {
                db.insertOrThrow(Tables.FAX, null, values);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return FaxContract.Faxs.buildFaxUri(values.getAsString(FaxContract.Faxs.ID));
            }
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (uri == FaxContract.BASE_CONTENT_URI) {
            // Handle whole database deletes (e.g. when signing out)
            deleteDatabase();
            getContext().getContentResolver().notifyChange(uri, null, false);
            return 1;
        }
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).delete(db);
        getContext().getContentResolver().notifyChange(uri, null, false);
        return retVal;
    }

    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAXS: {
                return builder.table(Tables.FAX);
            }
            case FAXS_ID: {
                final String id = FaxContract.Faxs.getFaxId(uri);
                return builder.table(Tables.FAX)
                        .where(FaxContract.Faxs.ID + "=?", id);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        switch (match) {
            case FAXS: {
                return builder.table(Tables.FAX);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    private void deleteDatabase() {
        mOpenHelper.close();
        Context context = getContext();
        FaxDatabase.deleteDatabase(context);
        mOpenHelper = new FaxDatabase(getContext());
    }
}
