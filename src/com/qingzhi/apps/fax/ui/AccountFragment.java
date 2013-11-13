package com.qingzhi.apps.fax.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.qingzhi.apps.fax.R;
import com.qingzhi.apps.fax.util.AccountUtils;

public class AccountFragment extends Fragment implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setTitle("账户");
        actionBar.setNavigationMode(ActionBar.DISPLAY_SHOW_TITLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        view.findViewById(R.id.logout_btn).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout_btn:
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                sp.edit().clear().commit();

                AccountManager mAccountManager = AccountManager.get(getActivity());
                Account[] accounts = mAccountManager.getAccountsByType(AccountUtils.ACCOUNT_TYPE);
                if (accounts != null) {
                    mAccountManager.removeAccount(accounts[0], callback, null);
                } else {
                    getActivity().finish();
                }
                break;
        }
    }

    AccountManagerCallback callback = new AccountManagerCallback() {
        @Override
        public void run(AccountManagerFuture future) {
            getActivity().finish();

            Intent addAccountIntent = new Intent(getActivity(), HomeActivity.class);
            getActivity().startActivity(addAccountIntent);
        }
    };
}
