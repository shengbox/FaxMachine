package com.qingzhi.apps.fax.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.qingzhi.apps.fax.R;
import com.qingzhi.apps.fax.io.model.Fax;

public class FaxEdit extends FragmentActivity implements View.OnClickListener {
    EditText receiveName;
    EditText receiveNumber;
    Fax fax;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fax_edit);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        receiveName = (EditText) findViewById(R.id.fax_name);
        receiveNumber = (EditText) findViewById(R.id.fax_number);

        fax = getIntent().getParcelableExtra("fax");
        if (fax != null) {
            receiveName.setText(fax.receiver_desc);
            receiveNumber.setText(fax.receiver_phone);
        }

        findViewById(R.id.add_button).setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                Cursor c = null;
                try {
                    c = getContentResolver().query(uri, new String[]{BaseColumns._ID,
                            ContactsContract.CommonDataKinds.Phone.DATA,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                            null, null, null);
                    if (c != null && c.moveToFirst()) {
                        int id = c.getInt(0);
                        String phone = c.getString(1);
                        String name = c.getString(2);
                        receiveNumber.setText(phone);
                        receiveName.setText(name);
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.add("Add");
        item.setIcon(android.R.drawable.ic_menu_add);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if ("Add".equals(item.getTitle())) {
            Intent intent = new Intent(this, ShootPaper.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_button:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 1);
                break;
        }
    }
}