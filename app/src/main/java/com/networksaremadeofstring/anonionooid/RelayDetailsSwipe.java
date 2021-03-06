/*
* Copyright (C) 2014 - Gareth Llewellyn
*
* This file is part of AnOnionooid - https://networksaremadeofstring.com/anonionooid/
*
* This program is free software: you can redistribute it and/or modify it
* under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License
* for more details.
*
* You should have received a copy of the GNU General Public License along with
* this program. If not, see <http://www.gnu.org/licenses/>
*/
package com.networksaremadeofstring.anonionooid;

import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.networksaremadeofstring.anonionooid.API.Ooo;
import com.networksaremadeofstring.anonionooid.API.Relay;
import com.networksaremadeofstring.anonionooid.R;
import com.networksaremadeofstring.anonionooid.cache.LocalCache;

public class RelayDetailsSwipe extends Activity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    Bundle arguments;
    Context mContext;
    LocalCache lc = null;
    boolean isFavourite = false;
    MenuItem favourite;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relay_details_swipe);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mContext = this;
        if(null == lc)
            lc = new LocalCache(mContext);

        lc.open();
        isFavourite = lc.isAFavourite(getIntent().getStringExtra(Ooo.ARG_ITEM_ID));
        lc.close();


        // Show the Up button in the action bar.
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.ab_icon);

        try
        {
            //fingerprint =
            actionBar.setSubtitle(getIntent().getStringExtra(Ooo.ARG_ITEM_ID));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (savedInstanceState == null)
        {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            arguments = new Bundle();
            arguments.putString(Ooo.ARG_ITEM_ID, getIntent().getStringExtra(Ooo.ARG_ITEM_ID));

            /*RelayDetailFragment fragment = new RelayDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.relay_detail_container, fragment)
                    .commit();*/
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.relay_details_swipe, menu);

        favourite = menu.findItem(R.id.action_favorite);

        if(isFavourite)
            favourite.setIcon(getResources().getDrawable(R.drawable.ic_favourite_solid));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, RelayListActivity.class));
            return true;
        }
        else if(id == R.id.action_favorite)
        {
            new AsyncTask<Void, Void, Boolean>()
            {
                @Override
                protected Boolean doInBackground(Void... params)
                {

                    try
                    {
                        if(null == lc)
                            lc = new LocalCache(mContext);

                        lc.open();
                        boolean result;
                        if(isFavourite)
                        {
                            result = lc.removeFavRelay(getIntent().getStringExtra(Ooo.ARG_ITEM_ID));
                        }
                        else
                        {
                            result = lc.addFavRelay(getIntent().getStringExtra(Ooo.ARG_ITEM_ID), getIntent().getStringExtra(Ooo.ARG_relay_nickname));
                        }

                        lc.close();

                        return result;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();

                        if(null != lc)
                            lc.close();

                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean success)
                {
                    if(success)
                    {
                        if(isFavourite)
                        {
                            Toast.makeText(mContext, getString(R.string.favResultRemoveSuccess), Toast.LENGTH_SHORT).show();
                            favourite.setIcon(getResources().getDrawable(R.drawable.ic_favourite));
                            isFavourite = false;
                        }
                        else
                        {
                            Toast.makeText(mContext, getString(R.string.favResultAddSuccess), Toast.LENGTH_SHORT).show();
                            favourite.setIcon(getResources().getDrawable(R.drawable.ic_favourite_solid));
                        }
                    }
                    else
                    {
                        if(isFavourite)
                        {
                            Toast.makeText(mContext, getString(R.string.favResultRemoveFailure), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(mContext, getString(R.string.favResultAddFailure), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }.execute(null, null, null);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        /*if(isFavourite)
            favourite.setIcon(getResources().getDrawable(R.drawable.ic_favourite_solid));*/
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);

            Fragment getFragment = null;

            switch(position)
            {
                case 0:
                {
                    getFragment = new RelayGeneralDetailsFragment();
                }
                break;

                case 1:
                {
                    getFragment = new RelayMetaDetailsFragment();
                }
                break;

                case 2:
                {
                    getFragment = new RelayGraphDetailsFragment();
                }
                break;
            }

            getFragment.setArguments(arguments);
            return getFragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_relay_details_swipe, container, false);
            return rootView;
        }
    }

}
