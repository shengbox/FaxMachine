package com.qingzhi.apps.fax.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qingzhi.apps.fax.R;
import com.qingzhi.apps.fax.io.model.Person;
import com.qingzhi.apps.fax.util.AccountUtils;

public class DrawerLeft extends Fragment {
    Person person;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AccountManager am = AccountManager.get(getActivity());
        Account[] accounts = am.getAccountsByType(AccountUtils.ACCOUNT_TYPE);
        String p = am.getUserData(accounts[0], "person");
        person = new Gson().fromJson(p, Person.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.drawer_left_layout, container, false);

        ListView mDrawer = (ListView) view.findViewById(R.id.left_list);
        mDrawer.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                Shakespeare.TITLES));
        mDrawer.setOnItemClickListener(new DrawerItemClickListener());
        ImageView imageView = (ImageView) view.findViewById(R.id.photo);
        ImageLoader.getInstance().displayImage(person.img, imageView);
        return view;
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Fragment mContent;
            switch (position) {
                case 0:
                    mContent = new FaxActivity();
                    break;
                case 1:
                    mContent = new MeetingList();
                    break;
                case 2:
                    mContent = new AccountFragment();
                    break;
            }
        }
    }
}
