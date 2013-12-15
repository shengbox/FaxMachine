package com.qingzhi.apps.fax.ui;

import android.app.ActionBar;
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
import android.view.View;
import android.widget.ListView;
import com.qingzhi.apps.fax.R;
import com.qingzhi.apps.fax.io.model.Fax;
import com.qingzhi.apps.fax.provider.FaxContract;
import com.qingzhi.apps.fax.sync.FaxMgr;


public class MeetingList extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setTitle("会议");
        // 将ActionBar的操作模型设置为NAVIGATION_MODE_LIST
        actionBar.setNavigationMode(ActionBar.DISPLAY_SHOW_TITLE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText("没有会议记录");
        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.fax_box_list_item, null,
                new String[]{
                        FaxContract.Meeting.ID},
                new int[]{R.id.receiver_desc}, 0);

        setListAdapter(mAdapter);

        setListShown(false);
        getListView().setItemsCanFocus(true);
        getListView().setCacheColorHint(Color.TRANSPARENT);
        getListView().setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, new int[]{0x00000000, 0x00000000}));
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
            FaxContract.Meeting._ID,
            FaxContract.Meeting.STATUS,
            FaxContract.Meeting.HOLDER_ENTER_FIRST,
            FaxContract.Meeting.UPDATE_TIME,
            FaxContract.Meeting.ENTER_TOKEN,
            FaxContract.Meeting.ENABLE_LISTEN,
            FaxContract.Meeting.CREATOR_NAME,
            FaxContract.Meeting.START_TIME,
            FaxContract.Meeting.ENTER_URL,
            FaxContract.Meeting.RECORD,
            FaxContract.Meeting.CREATOR_ID,
            FaxContract.Meeting.CREATE_TIME,
            FaxContract.Meeting.ROOM_ID,
            FaxContract.Meeting.END_TIME,
            FaxContract.Meeting.END_REASON,
            FaxContract.Meeting.CONF_TEL,
            FaxContract.Meeting.ID,
            FaxContract.Meeting.SUBJECT,
    };

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri = FaxContract.Meeting.CONTENT_URI;
        return new CursorLoader(getActivity(), baseUri,
                PROJECTIONS, null, null,
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

    }
}
