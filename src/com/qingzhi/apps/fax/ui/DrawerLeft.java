package com.qingzhi.apps.fax.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qingzhi.apps.fax.R;
import com.qingzhi.apps.fax.io.model.Person;
import com.qingzhi.apps.fax.util.AccountUtils;

public class DrawerLeft extends Fragment {
    Person person;
    String bpn;
    Account account;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.drawer_left_layout, container, false);

        AccountManager am = AccountManager.get(getActivity());
        Account[] accounts = am.getAccountsByType(AccountUtils.ACCOUNT_TYPE);
        String p = am.getUserData(accounts[0], "person");
        person = new Gson().fromJson(p, Person.class);
        bpn = am.getUserData(accounts[0], "bpn");
        account = accounts[0];

        ListView mDrawer = (ListView) view.findViewById(R.id.left_list);
        mDrawer.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                Shakespeare.TITLES));
        mDrawer.setOnItemClickListener(new DrawerItemClickListener());
        ImageView imageView = (ImageView) view.findViewById(R.id.photo);
        if (person != null && person.imgL != null) {
            ImageLoader.getInstance().displayImage(person.img, imageView);
        }
        ((TextView) view.findViewById(R.id.bpn)).setText(bpn);
        ((TextView) view.findViewById(R.id.name)).setText(account.name);
        return view;
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Fragment mContent = null;
            switch (position) {
                case 0:
                    mContent = new MeetingList();
                    break;
                case 1:
                    mContent = new FaxActivity();
                    break;
                case 2:
                    mContent = new AccountFragment();
                    break;
            }
            ((HomeActivity) getActivity()).changeContent(mContent);
        }
    }
}
