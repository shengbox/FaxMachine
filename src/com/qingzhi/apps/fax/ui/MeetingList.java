package com.qingzhi.apps.fax.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.ListFragment;


public class MeetingList extends ListFragment {
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


    }
}
