package com.qingzhi.apps.fax.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.view.*;
import android.widget.ListView;
import android.widget.TextView;
import com.qingzhi.apps.fax.sync.FaxMgr;
import com.qingzhi.apps.fax.R;
import com.qingzhi.apps.fax.io.model.Fax;
import com.qingzhi.apps.fax.provider.FaxContract;

public class FaxListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter mAdapter;
    int mNum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText("没有此类型记录");
        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.fax_box_list_item, null,
                new String[]{
                        FaxContract.Faxs.RECEIVER_DESC,
                        FaxContract.Faxs.CREATE_TIME_STR, FaxContract.Faxs.HAS_COMMIT_TO_SERVER},
                new int[]{R.id.receiver_desc, R.id.last_send_time_str, R.id.fax_action}, 0);

        SimpleCursorAdapter.ViewBinder viewBinder = new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                switch (view.getId()) {
                    case R.id.receiver_desc:
                        String desc = cursor.getString(columnIndex);
                        String receivePhone = cursor.getString(cursor.getColumnIndex(FaxContract.Faxs.RECEIVER_PHONE));
                        if (!TextUtils.isEmpty(receivePhone) && !TextUtils.isEmpty(desc)) {
                            desc = desc + "-" + receivePhone;
                        } else {
                            desc = desc + receivePhone;
                        }
                        ((TextView) view).setText(desc);
                        return true;
                    case R.id.last_send_time_str:
                        String sendTime = cursor.getString(columnIndex);
                        String createTime = cursor.getString(cursor.getColumnIndex(FaxContract.Faxs.CREATE_TIME_STR));
                        if (!TextUtils.isEmpty(sendTime)) {
                            ((TextView) view).setText(sendTime);
                        } else {
                            ((TextView) view).setText(createTime);
                        }
                        return true;
                    case R.id.fax_action:
                        if (mNum == 0) {
                            view.setBackgroundResource(R.drawable.fax_action_draft);
                        } else if (mNum == 1) {
                            int commit = cursor.getInt(cursor.getColumnIndex(FaxContract.Faxs.HAS_COMMIT_TO_SERVER));
                            int error = cursor.getInt(cursor.getColumnIndex(FaxContract.Faxs.SEND_ERROR_CODE));
                            if (Fax.sendStatus(commit, error) == Fax.FaxSendStatusSucces) {
                                view.setBackgroundResource(R.drawable.fax_action_send_success);
                            } else if (Fax.sendStatus(commit, error) == Fax.FaxSendStatusFail) {
                                view.setBackgroundResource(R.drawable.fax_action_send_fail);
                            } else {
                                view.setBackgroundResource(R.drawable.fax_action_sending);
                            }
                        } else {
                            int has_read = cursor.getInt(cursor.getColumnIndex(FaxContract.Faxs.HAS_READ));
                            if (has_read == 1) {
                                view.setBackgroundResource(R.drawable.fax_action_read);
                            } else {
                                view.setBackgroundResource(R.drawable.fax_action_unread);
                            }
                        }
                        return true;
                }
                return false;
            }
        };
        mAdapter.setViewBinder(viewBinder);
        setListAdapter(mAdapter);

        setListShown(false);
        getListView().setItemsCanFocus(true);
        getListView().setCacheColorHint(Color.TRANSPARENT);
        getListView().setDividerHeight(10);

        getLoaderManager().initLoader(0, null, this);
    }

    private final ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (getActivity() == null) {
                return;
            }

            Loader<Cursor> loader = getLoaderManager().getLoader(0);
            if (loader != null) {
                loader.forceLoad();
            }
        }
    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.getContentResolver().registerContentObserver(
                FaxContract.Faxs.CONTENT_URI, true, mObserver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().getContentResolver().unregisterContentObserver(mObserver);
    }

    static final String[] PROJECTIONS = new String[]{
            FaxContract.Faxs._ID,
            FaxContract.Faxs.ID,
            FaxContract.Faxs.ATTACH_BUNDLE_ACCESS_CODE,
            FaxContract.Faxs.ATTACH_BUNDLE_ID,
            FaxContract.Faxs.CATEGORY,
            FaxContract.Faxs.CREATE_TIME_STR,
            FaxContract.Faxs.CREATE_USER,
            FaxContract.Faxs.SENDER_PHONE,
            FaxContract.Faxs.RECEIVER_DESC,
            FaxContract.Faxs.RECEIVER_PHONE,
            FaxContract.Faxs.LAST_SEND_TIME_STR,
            FaxContract.Faxs.HAS_COMMIT_TO_SERVER,
            FaxContract.Faxs.HAS_READ,
            FaxContract.Faxs.SEND_ERROR_CODE,
    };

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri = FaxContract.Faxs.CONTENT_URI;
        return new CursorLoader(getActivity(), baseUri,
                PROJECTIONS, FaxContract.Faxs.CATEGORY + "=?", new String[]{String.valueOf(mNum)},
                null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Object cursor = mAdapter.getItem(position);
        if (cursor == null)
            return;
        Fax fax = FaxMgr.parser((Cursor) cursor, PROJECTIONS);

        if (fax.category == 2) {
            Intent intent = new Intent(getActivity(), FaxEdit.class);
            intent.putExtra("fax", fax);
            startActivity(intent);
        }
    }
}
