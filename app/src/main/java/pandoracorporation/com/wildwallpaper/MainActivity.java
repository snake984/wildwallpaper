package pandoracorporation.com.wildwallpaper;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.WallpaperManager;
import android.content.Intent;
import android.os.AsyncTask;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.pocketreddit.library.datasources.DataSourceException;
import com.pocketreddit.library.datasources.RedditDataSource;
import com.pocketreddit.library.things.Link;
import com.pocketreddit.library.things.Listing;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import pandoracorporation.com.wildwallpaper.adapter.ImageAdapter;
import pandoracorporation.com.wildwallpaper.fragment.NavigationDrawerFragment;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private static final String SUBREDDIT = "EarthPorn/";
    private ListView imagesList = null;
    private ImageAdapter imagesAdapter = null;
    private List<Link> wallpapers;
    private List<Link> filtered;
    public static int TIME_BETWEEN_2_TAKES = 10;
    public static int DISTANCE_BETWEEN_2_TAKES = 10;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private View mCurrentItemSelected = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.imagesList = (ListView) findViewById(R.id.ListView);
        imagesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("onCreate", "onItemLongClick");
                mCurrentItemSelected = view;
                return false;
            }
        });
        new SetWallpapersTask().execute((Void) null);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        registerForContextMenu(imagesList);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.i("onContextItemSelected", "in");
        switch (item.getItemId())
        {
            case R.id.set_wallpaper:
                setWallpapers(mCurrentItemSelected);
                return true;
            case R.id.save:
                //TODO
                return true;
            case R.id.share:
                //TODO
                return true;
            case R.id.locate:
                activateMapsActivity(mCurrentItemSelected);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    public void activateMapsActivity(View v) {
        TextView textView = (TextView) v.findViewById(R.id.item_title);

        Intent intent = new Intent();
        intent.setClass(this, MapsActivity.class);
        intent.putExtra("title", textView.getText().toString());
        startActivity(intent);
    }

    public void setWallpapers(View v)
    {
        ImageView img = (ImageView) v.findViewById(R.id.item_image);
        Log.i("setWallpaper", "in");
        for (Link l : wallpapers) {
            Log.d("setWallpaper", l.getTitle());
            if (l.getTitle().equals(img.getContentDescription().toString())) {
                Log.i("setWallpaper", "compareTo");
                changeWallpaper(l.getUrl());
                Toast.makeText(this, this.getResources().getString(R.string.wallpaper_changed), Toast.LENGTH_SHORT)
                        .show();
                break;
            }
        }
    }

    private void changeWallpaper(String url) {
        URL address = null;
        try {
            address = new URL(url);
        } catch (MalformedURLException e1) {
            Log.e("changeWallpaper", "Wrong URL: " + e1.getLocalizedMessage());
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) address.openConnection();
            WallpaperManager.getInstance(this.getApplicationContext()).setStream(conn.getInputStream());
        } catch (IOException e) {
            Log.e("changeWallpaper", "IO err: " + e.getLocalizedMessage());
        }
    }

    private List<Link> getWallpapers() {
        RedditDataSource dataSource = new RedditDataSource();
        wallpapers = new ArrayList<Link>();

        Listing<Link> links = null;
        try {
            links = dataSource.getLinksForSubreddit(SUBREDDIT);
        } catch (DataSourceException e) {
            Log.d("getSubreddit", "Failed: " + e.getLocalizedMessage());
        }

        if (links != null) {
            for (Link link : links.getChildren()) {
                if (domainAllowed(link.getDomain())) {
                    wallpapers.add(link);
                    // Log.d("Link", link.getDomain() + " " + link.getUrl());
                }
            }
        }
        return wallpapers;
    }

    private List<Link> searchWallpapers(String searchString) {
        filtered = new ArrayList<Link>();

        if (wallpapers != null) {
            for (Link link : wallpapers) {
                if (domainAllowed(link.getDomain())) {
                    String linkTitle = link.getTitle().toLowerCase();

                    if(linkTitle.contains(searchString.toLowerCase()))
                    filtered.add(link);
                    // Log.d("Link", link.getDomain() + " " + link.getUrl());
                }
            }
        }
        return filtered;
    }

    private boolean domainAllowed(String domain) {
        if (domain.contains("EarthPorn")
                || (domain.contains("flickr.com") && !domain.contains("static")))
            return false;
        return true;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();

        Intent intent = new Intent();

        switch (position) {
            case 1:
                return;
            case 2:
                //TODO
                break;
            case 3:
                //TODO
                break;
            case 4:
                //TODO
                break;
            case 5:
                //TODO
                break;
            default:
                break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.home_title_section);
                break;
            case 2:
                mTitle = getString(R.string.pictures_title_section);
                break;
            case 3:
                mTitle = getString(R.string.diaporama_title_section);
                break;
            case 4:
                mTitle = getString(R.string.help_title_section);
            case 5:
                mTitle = getString(R.string.parameters_title_section);
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
            getMenuInflater().inflate(R.menu.main, menu);
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
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                openSearch(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSearch(MenuItem item) {
        final SearchView searchView = (SearchView) item.getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                imagesList.setAdapter(new ImageAdapter(getApplicationContext(), searchWallpapers(newText)));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imagesList.deferNotifyDataSetChanged();
                    }
                });

                return true;
            }
        });

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                imagesList.setAdapter(new ImageAdapter(getApplicationContext(), wallpapers));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imagesList.deferNotifyDataSetChanged();
                    }
                });
                return true;
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        new SetWallpapersTask().execute((Void) null);
    }



    private class SetWallpapersTask extends AsyncTask<Void, Void, List<Link>> {

        @Override
        protected List<Link> doInBackground(Void... params) {
            return getWallpapers();
        }

        @Override
        protected void onPostExecute(List<Link> result) {
            imagesAdapter = new ImageAdapter(getApplicationContext(),
                    result);
            imagesList.setAdapter(imagesAdapter);
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
