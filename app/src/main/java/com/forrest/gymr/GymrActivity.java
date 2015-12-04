package com.forrest.gymr;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.forrest.gymr.gymrfragments.ChatFragment;
import com.forrest.gymr.gymrfragments.NavigationDrawerFragment;
import com.forrest.gymr.gymrfragments.ProfileFragment;
import com.forrest.gymr.gymrfragments.SettingsFragment;


public class GymrActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gymr);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }


    protected void onResume() {
        super.onResume(); /*
        ParseGeoPoint.getCurrentLocationInBackground(4000, new LocationCallback() {
            @Override
            public void done(ParseGeoPoint p, ParseException e) {
                  ParseUser.getCurrentUser().put("prevLocation", p);
            }
        });
*/
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        if (position == 0) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, MatchingFragment.newInstance())
                    .commit();
        } else if (position == 1) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new ProfileFragment())
                    .commit();
        } else if (position == 2) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new ChatFragment())
                    .commit();
        } else if (position == 3) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new SettingsFragment())
                    .commit();
        } else if (position == 4) {
            Intent i = new Intent(this, MatchingCriteriaActivity.class);
            startActivity(i);
        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.gymr, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
