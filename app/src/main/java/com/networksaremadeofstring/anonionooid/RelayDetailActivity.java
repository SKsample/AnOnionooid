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

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

import android.view.MenuItem;

import com.networksaremadeofstring.anonionooid.API.Ooo;


/**
 * An activity representing a single Relay detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RelayListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link RelayDetailFragment}.
 */
public class RelayDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relay_detail);

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        try
        {
            getActionBar().setSubtitle(getIntent().getStringExtra(Ooo.ARG_ITEM_ID));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null)
        {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(Ooo.ARG_ITEM_ID, getIntent().getStringExtra(Ooo.ARG_ITEM_ID));

            RelayDetailFragment fragment = new RelayDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.relay_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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
        return super.onOptionsItemSelected(item);
    }
}
