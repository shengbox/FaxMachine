/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.qingzhi.apps.fax.ui;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.qingzhi.apps.fax.R;


public class HomeActivity extends FragmentActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawer;
    private LinearLayout mDrawer2;
    private Fragment mContent;

    private ActionBarHelper mActionBar;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.drawer_layout);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer = (ListView) findViewById(R.id.left_list);
        mDrawer2 = (LinearLayout) findViewById(R.id.left_drawer);

        FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentById(R.id.content) == null) {
            mContent = new FaxActivity();
            fm.beginTransaction().replace(R.id.content, mContent).commit();
        }

        mDrawerLayout.setDrawerListener(new DemoDrawerListener());
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawer.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                Shakespeare.TITLES));
        mDrawer.setOnItemClickListener(new DrawerItemClickListener());

        mActionBar = createActionBarHelper();
        mActionBar.init();

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
         * The action bar home/up action should open or close the drawer.
         * mDrawerToggle will take care of this.
         */
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            mContent.setText(Shakespeare.DIALOGUE[position]);
//            mActionBar.setTitle(Shakespeare.TITLES[position]);

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

            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.content, mContent).commit();

            mActionBar.setTitle(null);
            mDrawerLayout.closeDrawer(mDrawer2);
        }
    }


    private class DemoDrawerListener implements DrawerLayout.DrawerListener {
        @Override
        public void onDrawerOpened(View drawerView) {
            mDrawerToggle.onDrawerOpened(drawerView);
            mActionBar.onDrawerOpened();
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            mDrawerToggle.onDrawerClosed(drawerView);
            mActionBar.onDrawerClosed();
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            mDrawerToggle.onDrawerStateChanged(newState);
        }
    }

    /**
     * Create a compatible helper that will manipulate the action bar if available.
     */
    private ActionBarHelper createActionBarHelper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return new ActionBarHelperICS();
        } else {
            return new ActionBarHelper();
        }
    }

    /**
     * Stub action bar helper; this does nothing.
     */
    private class ActionBarHelper {
        public void init() {
        }

        public void onDrawerClosed() {
        }

        public void onDrawerOpened() {
        }

        public void setTitle(CharSequence title) {
        }
    }

    /**
     * Action bar helper for use on ICS and newer devices.
     */
    private class ActionBarHelperICS extends ActionBarHelper {
        private final ActionBar mActionBar;
        private CharSequence mDrawerTitle;
        private CharSequence mTitle;

        ActionBarHelperICS() {
            mActionBar = getActionBar();
        }

        @Override
        public void init() {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
            mTitle = mDrawerTitle = getTitle();
        }


        @Override
        public void onDrawerClosed() {
            super.onDrawerClosed();
//            mActionBar.setTitle(mTitle);
        }


        @Override
        public void onDrawerOpened() {
            super.onDrawerOpened();
//            mActionBar.setTitle(mDrawerTitle);
        }

        @Override
        public void setTitle(CharSequence title) {
            mTitle = title;
        }
    }
}
