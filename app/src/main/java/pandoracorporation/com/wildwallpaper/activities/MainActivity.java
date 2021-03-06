package pandoracorporation.com.wildwallpaper.activities;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.github.jreddit.entity.Submission;
import pandoracorporation.com.wildwallpaper.R;
import pandoracorporation.com.wildwallpaper.adapter.NavDrawerListAdapter;
import pandoracorporation.com.wildwallpaper.dao.PictureDao;
import pandoracorporation.com.wildwallpaper.fragments.MainFragment;
import pandoracorporation.com.wildwallpaper.fragments.MyPicturesFragment;
import pandoracorporation.com.wildwallpaper.fragments.SettingsFragment;
import pandoracorporation.com.wildwallpaper.views.NavDrawerItem;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//TODO - Faire son propre thème (un theme pour api >= 21 et un theme pour api < 21)
public class MainActivity extends AppCompatActivity {

    public final static String MAIN_TAG = "MAIN_TAG";
    public final static String MY_PICTURES_TAG = "MY_PICTURES_TAG";
    public final static String HELP_TAG = "HELP_TAG";
    public final static String SETTINGS_TAG = "SETTINGS_TAG";


    //region Attributes
    private Toolbar mToolbar;
    private PictureDao mPictureDao;
    private List<Submission> mSubmissionList; //Liste des posts reddit
    //endregion

    //region Drawer attributes
    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    private NavDrawerItem mPicturesNavDrawerItem;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private int mCurrentSelectedPosition = 1;
    //endregion

    //region Activity lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MAIN", getString(R.string.screen_type));
        setContentView(R.layout.activity_main);
        mPictureDao = new PictureDao(this);

        //region Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        //endregion

        //region Status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.primaryDark));
        }
        //endregion

        initNavigationDrawer();

        mTitle = getTitle();

    }


    @Override
    public void onBackPressed() {
        if (mCurrentSelectedPosition != 1) {
            getFragmentManager().popBackStack();
            displayView(1);
        } else {
            finish();
        }
    }


    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        menu.findItem(R.id.action_search).setVisible(!drawerOpen);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_settings) {
            displayView(4);
        }

        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    //endregion

    //region UI
    private void initNavigationDrawer() {

        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navDrawerItems = new ArrayList<>();

        //region Header Drawer
        LayoutInflater inflater = this.getLayoutInflater();
        View header = inflater.inflate(R.layout.drawer_header, mDrawerList, false);

        TextView username = (TextView) header.findViewById(R.id.username);
        username.setText(R.string.welcome_message);

        mDrawerList.addHeaderView(header, null, false);
        //endregion
        //Saving this item to edit dynamically the number of pictures
        mPicturesNavDrawerItem = new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1), true,
                "" + countSavedPictures());

        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Photos
        navDrawerItems.add(mPicturesNavDrawerItem);
        // Help
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Settings
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));

        // Recycle the typed array
        navMenuIcons.recycle();

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(this, navDrawerItems);
        mDrawerList.setAdapter(adapter);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name,
                //nav menu toggle icon
                R.string.app_name // nav drawer open - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        displayView(mCurrentSelectedPosition);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        String tag = null;
        position -= 1;
        switch (position) {
            case 0:
                fragment = MainFragment.newInstance();
                tag = MAIN_TAG;
                break;
            case 1:
                fragment = MyPicturesFragment.newInstance();
                tag = MY_PICTURES_TAG;
                break;
            case 2:
                //TODO
                fragment = MainFragment.newInstance();
                tag = HELP_TAG;
                break;
            case 3:
                fragment = SettingsFragment.newInstance();
                tag = SETTINGS_TAG;
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment, tag).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            Log.d("DRAWER", mDrawerLayout.toString());
            mDrawerLayout.closeDrawer(mDrawerList);
            mCurrentSelectedPosition = position + 1;
        } else {
            // error in creating fragment
            Log.e(this.getClass().getCanonicalName(), "Error in creating fragment");
        }
    }

    public void refreshNumberOfPics() {
        mPicturesNavDrawerItem.setCount("" + countSavedPictures());
        adapter = new NavDrawerListAdapter(this, navDrawerItems);
        mDrawerList.setAdapter(adapter);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
    }


    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }
    //endregion

    //region Utils
    private int countSavedPictures() {
        int result = 0;

        try {
            mPictureDao.open();
            result = mPictureDao.getCount();
            mPictureDao.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    //Notifie la galerie Android native de la création d'une nouvelle image
    public void notifyGallery(@NonNull String filename) {
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filename))));
    }
    //endregion

    //region Getters/Setters
    public List<Submission> getSubmissionList() {
        return mSubmissionList;
    }

    public void setSubmissionList(List<Submission> submissionList) {
        if (mSubmissionList == null) {
            mSubmissionList = new ArrayList<>();
        }
        mSubmissionList.clear();
        mSubmissionList.addAll(submissionList);
    }
    //endregion

    //region Drawer interface

    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }
    //endregion
}
