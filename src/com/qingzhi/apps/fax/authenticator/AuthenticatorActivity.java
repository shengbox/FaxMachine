package com.qingzhi.apps.fax.authenticator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.qingzhi.apps.fax.R;
import com.qingzhi.apps.fax.client.NetworkUtilities;
import com.qingzhi.apps.fax.io.model.QAccount;
import com.qingzhi.apps.fax.provider.FaxContract;
import com.qingzhi.apps.fax.ui.VerifyDialog;
import com.qingzhi.apps.fax.util.AccountUtils;
import com.qingzhi.apps.fax.util.QingzhiUtil;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";

    private static final String TAG = "AuthenticatorActivity";

    public static final String PARAM_USERNAME = "username";

    /** The Intent extra to store username. */
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

    protected boolean mRequestNewAccount = false;
    private Boolean mConfirmCredentials = false;

    /** Keep track of the login task so can cancel it if requested */
    private UserLoginTask mAuthTask = null;

    /** Keep track of the progress dialog so we can dismiss it */
    private ProgressDialog mProgressDialog = null;

    /**
     * for posting authentication attempts back to UI thread
     */
    private final Handler mHandler = new Handler();

    private TextView mMessage;

    private AccountManager mAccountManager;

    private String mUsername;
    private String mPassword;
    private EditText mPasswordEdit;
    private EditText mUsernameEdit;

    public static final String EXTRA_FINISH_INTENT
            = "com.qingzhi.apps.uc.extra.FINISH_INTENT";
    @Override
    protected void onCreate(Bundle icicle) {
        Log.i(TAG, "onCreate(" + icicle + ")");
        super.onCreate(icicle);

        if (getIntent().hasExtra(EXTRA_FINISH_INTENT)) {
            mFinishIntent = getIntent().getParcelableExtra(EXTRA_FINISH_INTENT);
        }

        mAccountManager = AccountManager.get(this);
        final Intent intent = getIntent();
        mUsername = intent.getStringExtra(PARAM_USERNAME);
        mRequestNewAccount = mUsername == null;
        mConfirmCredentials = intent.getBooleanExtra(PARAM_CONFIRM_CREDENTIALS, false);
        Log.i(TAG, "    request new: " + mRequestNewAccount);
//        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.login_activity);
//        getWindow().setFeatureDrawableResource(
//                Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_alert);
//        mMessage = (TextView) findViewById(R.id.message);
        mUsernameEdit = (EditText) findViewById(R.id.username_edit);
        mPasswordEdit = (EditText) findViewById(R.id.password_edit);
        if (!TextUtils.isEmpty(mUsername)) mUsernameEdit.setText(mUsername);
//        mMessage.setText(getMessage());
    }

    /*
     * {@inheritDoc}
     */
    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(R.string.ui_activity_authenticating));
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Log.i(TAG, "user cancelling authentication");
                if (mAuthTask != null) {
                    mAuthTask.cancel(true);
                }
            }
        });
        // We save off the progress dialog in a field so that we can dismiss
        // it later. We can't just call dismissDialog(0) because the system
        // can lose track of our dialog if there's an orientation change.
        mProgressDialog = dialog;
        return dialog;
    }


    /**
     * Handles onClick event on the Submit button. Sends username/password to
     * the server for authentication. The button is configured to call
     * handleLogin() in the layout XML.
     *
     * @param view The Submit button for which this method is invoked
     */
    public void handleLogin(View view) {
        if (mRequestNewAccount) {
            mUsername = mUsernameEdit.getText().toString();
        }
        mPassword = mPasswordEdit.getText().toString();
        if (TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mPassword)) {
            mMessage.setText(getMessage());
        } else {
            // Show a progress dialog, and kick off a background task to perform
            // the user login attempt.
//            mPassword = ImageCache.hashKeyForDisk(mPassword);
            Log.v(TAG, "password:" + mPassword);
            showProgress();
            mAuthTask = new UserLoginTask();
            mAuthTask.execute();
        }
    }

    /**
     * Shows the progress UI for a lengthy operation.
     */
    private void showProgress() {
        showDialog(0);
    }

    /**
     * Hides the progress UI for a lengthy operation.
     */
    private void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }


    /**
     * Returns the message to be displayed at the top of the login dialog box.
     */
    private CharSequence getMessage() {
        getString(R.string.label);
        if (TextUtils.isEmpty(mUsername)) {
            // If no username, then we ask the user to log in using an
            // appropriate service.
            final CharSequence msg = getText(R.string.login_activity_newaccount_text);
            return msg;
        }
        if (TextUtils.isEmpty(mPassword)) {
            // We have an account but no password
            return getText(R.string.login_activity_loginfail_text_pwmissing);
        }
        return null;
    }

    /**
     * Called when the authentication process completes (see attemptLogin()).
     *
     * @param authToken the authentication token returned by the server, or NULL if
     *            authentication failed.
     */
    public void onAuthenticationResult(String authToken) {

        boolean success = ((authToken != null) && (authToken.length() > 0));
        Log.i(TAG, "onAuthenticationResult(" + success + ")");

        // Our task is complete, so clear it out
        mAuthTask = null;

        // Hide the progress dialog
        hideProgress();

        if (success) {
            if (!mConfirmCredentials) {
                finishLogin(authToken);
            } else {
                finishConfirmCredentials(success);
            }
        } else {
            Log.e(TAG, "onAuthenticationResult: failed to authenticate");
//            if (mRequestNewAccount) {
//                // "Please enter a valid username/password.
//                mMessage.setText(getText(R.string.login_activity_loginfail_text_both));
//            } else {
//                // "Please enter a valid password." (Used when the
//                // account is already in the database but the password
//                // doesn't work.)
//                mMessage.setText(getText(R.string.login_activity_loginfail_text_pwonly));
//            }

            Intent intent = new Intent(this, VerifyDialog.class);
            startActivity(intent);
        }
    }

    /**
     * Called when response is received from the server for confirm credentials
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller.
     *
     * @param result the confirmCredentials result.
     */
    private void finishConfirmCredentials(boolean result) {
        Log.i(TAG, "finishConfirmCredentials()");
        final Account account = new Account(mUsername, AccountUtils.ACCOUNT_TYPE);
        mAccountManager.setPassword(account, mPassword);
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, result);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onAuthenticationCancel() {
        Log.i(TAG, "onAuthenticationCancel()");

        // Our task is complete, so clear it out
        mAuthTask = null;

        // Hide the progress dialog
        hideProgress();
    }

    /**
     * Called when response is received from the server for authentication
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller. We store the
     * authToken that's returned from the server as the 'password' for this
     * account - so we're never storing the user's actual password locally.
     *
     */
    private void finishLogin(String authToken) {
        Log.i(TAG, "finishLogin()");
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonParser().parse(authToken).getAsJsonObject();
        QAccount acc = gson.fromJson(jsonObject.get("qa"), QAccount.class);

        final Account account = new Account(acc.p.n, AccountUtils.ACCOUNT_TYPE);
        if (mRequestNewAccount) {
            Bundle userdata = new Bundle();
            userdata.putString("q", acc.q);
            userdata.putString("ln", acc.ln);
            userdata.putString("bpn", acc.bpn);
            userdata.putString("pass_md5", acc.pass_md5);
            userdata.putString("person", gson.toJson(acc.p));
            mAccountManager.addAccountExplicitly(account, acc.c, userdata);
            // Set contacts sync for this account.
//            ContentResolver.setSyncAutomatically(account, ContactsContract.AUTHORITY, true);
            ContentResolver.setSyncAutomatically(account, FaxContract.CONTENT_AUTHORITY, true);
        } else {
            mAccountManager.setPassword(account, acc.c);
        }
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUsername);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, AccountUtils.ACCOUNT_TYPE);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
//        finish();



        mCancelAuth = false;
        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccountsByType(AccountUtils.ACCOUNT_TYPE);
        mChosenAccount = accounts[0];
        tryAuthenticate();
    }

    /**
     * Represents an asynchronous task used to authenticate a user against the
     * SampleSync Service
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // We do the actual work of authenticating the user
            // in the NetworkUtilities class.
            try {
                final String deviceId = Settings.Secure.getString(AuthenticatorActivity.this.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                String check_md5 = QingzhiUtil.getMd5("account/login" + ":" + mUsername
                        + ":" + QingzhiUtil.getMd5(mPassword));
                return NetworkUtilities.authenticate(mUsername, check_md5, deviceId);
            } catch (Exception ex) {
                Log.e(TAG, "UserLoginTask.doInBackground: failed to authenticate");
                Log.i(TAG, ex.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String authToken) {
            // On a successful authentication, call back into the Activity to
            // communicate the authToken (or null for an error).
            onAuthenticationResult(authToken);
        }

        @Override
        protected void onCancelled() {
            // If the action was canceled (by the user clicking the cancel
            // button in the progress dialog), then call back into the
            // activity to let it know.
            onAuthenticationCancel();
        }
    }


    private static final int REQUEST_AUTHENTICATE = 100;
    private Account mChosenAccount;
    private Intent mFinishIntent;
    private boolean mCancelAuth = false;

    private void tryAuthenticate() {
        AccountUtils.tryAuthenticate(AuthenticatorActivity.this,
                cb,
                REQUEST_AUTHENTICATE,
                mChosenAccount);
    }

    AccountUtils.AuthenticateCallback cb = new AccountUtils.AuthenticateCallback() {
        @Override
        public boolean shouldCancelAuthentication() {
            return false;
        }

        @Override
        public void onAuthTokenAvailable(String authToken) {
//            ContentResolver.setIsSyncable(mChosenAccount, ScheduleContract.CONTENT_AUTHORITY, 1);
            finish();
        }
    };
}
