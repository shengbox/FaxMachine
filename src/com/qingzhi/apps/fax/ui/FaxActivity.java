package com.qingzhi.apps.fax.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.MenuItemCompat;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import com.qingzhi.apps.fax.sync.FaxMgr;
import com.qingzhi.apps.fax.R;

public class FaxActivity extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FaxMgr.init(getActivity());
//        FragmentManager fm = getSupportFragmentManager();

        // Create the list fragment and add it as our sole content.
//        if (fm.findFragmentById(android.R.id.content) == null) {
//            FaxListFragment list = new FaxListFragment();
//            fm.beginTransaction().add(android.R.id.content, list).commit();
//        }


        setHasOptionsMenu(true);
        // 生成一个SpinnerAdapter
        SpinnerAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.fax_list_type, android.R.layout.simple_spinner_dropdown_item);

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setTitle(null);
        // 将ActionBar的操作模型设置为NAVIGATION_MODE_LIST
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(adapter, new DropDownListenser());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fax_content, container, false);
        FragmentManager fm = getChildFragmentManager();
        FaxListFragment listFragment = new FaxListFragment();
        fm.beginTransaction().replace(R.id.list, listFragment);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ("Send".equals(item.getTitle())) {
            Intent intent = new Intent(getActivity(), FaxEdit.class);
            getActivity().startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //To change body of overridden methods use File | Settings | File Templates.
        MenuItem item = menu.add("Send");
        item.setIcon(android.R.drawable.ic_menu_edit);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuItem item = menu.add("Send");
//        item.setIcon(android.R.drawable.ic_menu_edit);
//        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
//        return true;
//    }

    /**
     * 实现 ActionBar.OnNavigationListener接口
     */
    class DropDownListenser implements ActionBar.OnNavigationListener {
        // 得到和SpinnerAdapter里一致的字符数组
        String[] listNames = getResources().getStringArray(R.array.fax_list_type);

        /* 当选择下拉菜单项的时候，将Activity中的内容置换为对应的Fragment */
        public boolean onNavigationItemSelected(int itemPosition, long itemId) {
            // 生成自定的Fragment
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FaxListFragment list = new FaxListFragment();
            Bundle args = new Bundle();
            args.putInt("num", itemPosition);
            list.setArguments(args);
            fm.beginTransaction().replace(R.id.list, list).commit();
            return true;
        }
    }
}