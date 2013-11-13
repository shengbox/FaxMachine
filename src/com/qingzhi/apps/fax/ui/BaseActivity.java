package com.qingzhi.apps.fax.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.qingzhi.apps.fax.util.AccountUtils;

public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!AccountUtils.isAuthenticated(this)) {
            AccountUtils.startAuthenticationFlow(this, getIntent());
            finish();
        } else {

            /**
            AccountManager accountManager = AccountManager.get(this);
            Account account = null;
            Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
            for (Account acc : accounts) {
                if (acc.name.equals(AccountUtils.getChosenAccountName(this))) {
                    account = acc;
                    break;
                }
            }
            if (account != null) {
                ContentResolver.setSyncAutomatically(account, ScheduleContract.CONTENT_AUTHORITY, true);
            }
             */
        }
    }
}
