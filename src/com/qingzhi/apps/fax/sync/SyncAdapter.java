/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.qingzhi.apps.fax.sync;

import android.accounts.Account;
import android.content.*;
import android.os.Bundle;
import com.qingzhi.apps.fax.BuildConfig;
import com.qingzhi.apps.fax.util.AccountUtils;

import java.io.IOException;

import static com.qingzhi.apps.fax.util.LogUtils.LOGE;
import static com.qingzhi.apps.fax.util.LogUtils.LOGI;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "SyncAdapter";

    private final Context mContext;
    private SyncHelper mSyncHelper;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;

        //noinspection ConstantConditions,PointlessBooleanExpression
        if (!BuildConfig.DEBUG) {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable throwable) {
                    LOGE(TAG, "Uncaught sync exception, suppressing UI in release build.",
                            throwable);
                }
            });
        }
    }

    @Override
    public void onPerformSync(final Account account, Bundle extras, String authority,
                              final ContentProviderClient provider, final SyncResult syncResult) {
        final boolean uploadOnly = extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, false);
        final boolean manualSync = extras.getBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
        final boolean initialize = extras.getBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, false);

        if (uploadOnly) {
            return;
        }

        LOGI(TAG, "Beginning sync for account " + "," +
                " uploadOnly=" + uploadOnly +
                " manualSync=" + manualSync +
                " initialize=" + initialize);

        if (initialize) {
            String chosenAccountName = AccountUtils.getChosenAccountName(mContext);
            boolean isChosenAccount =
                    chosenAccountName != null && chosenAccountName.equals(account.name);
            ContentResolver.setIsSyncable(account, authority, isChosenAccount ? 1 : 0);
            if (!isChosenAccount) {
                ++syncResult.stats.numAuthExceptions;
                return;
            }
        }

        // Perform a sync using SyncHelper
        if (mSyncHelper == null) {
            mSyncHelper = new SyncHelper(mContext);
        }

        try {
            mSyncHelper.performSync(syncResult,
                    SyncHelper.FLAG_SYNC_LOCAL | SyncHelper.FLAG_SYNC_REMOTE);

        } catch (IOException e) {
            ++syncResult.stats.numIoExceptions;
            LOGE(TAG, "Error syncing data for I/O 2012.", e);
        }
    }
}

