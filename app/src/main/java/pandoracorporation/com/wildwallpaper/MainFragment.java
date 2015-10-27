package pandoracorporation.com.wildwallpaper;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.jreddit.entity.Submission;
import pandoracorporation.com.wildwallpaper.Views.DividerItemDecoration;
import pandoracorporation.com.wildwallpaper.adapter.PictureAdapter;
import pandoracorporation.com.wildwallpaper.utils.PictureHelper;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment implements PictureHelper.WallpapersFetchingListener {


    private static final String SUBREDDIT = "EarthPorn/";
    public static View mCurrentItemSelected = null;
    private ListView imagesList = null;
    private RecyclerView recyclerView;
    private PictureAdapter recyclerViewAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PictureHelper mHelper;
    private List<Submission> wallpapers;
    private List<Submission> filtered;
    private ProgressBar mProgressBar;


    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHelper = new PictureHelper(getActivity());

        mProgressBar = (ProgressBar) view.findViewById(R.id.main_progressbar);
        mProgressBar.setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView) view.findViewById(R.id.ListView);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.main_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.accentLight, R.color.primaryDark,
                R.color.accent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHelper.fetchWallpapers(MainFragment.this);
                //initWallpapers();
            }
        });

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mSwipeRefreshLayout.setDistanceToTriggerSync(400);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        mHelper.fetchWallpapers(this); //TODO - Faire cette requête dans le onCreate puis set la vue ici
        //initWallpapers();
        view.setBackgroundColor(Color.WHITE);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo adapter = (AdapterView.AdapterContextMenuInfo) menuInfo;
        mCurrentItemSelected = (View) adapter.targetView;

        Log.i("onCreateContextMenu", "in");
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.i("onContextItemSelected", "in");
        switch (item.getItemId()) {
            case R.id.set_wallpaper:
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
        intent.setClass(getActivity(), MapsActivity.class);
        intent.putExtra("title", textView.getText().toString());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                openSearch(item);
                return super.onOptionsItemSelected(item);
            default:
                return false;
        }
    }


    private List<Submission> searchWallpapers(String searchString) {
        filtered = new ArrayList<>();

        if (wallpapers != null) {
            for (Submission link : wallpapers) {
                if (domainAllowed(link.getDomain())) {
                    String linkTitle = link.getTitle().toLowerCase();

                    if (linkTitle.contains(searchString.toLowerCase())) {
                        filtered.add(link);
                    }
                    // Log.d("Link", link.getDomain() + " " + link.getUrl());
                }
            }
        }
        return filtered;
    }

    private boolean domainAllowed(String domain) {
        if (domain.contains("EarthPorn") || (domain.contains("flickr.com") && !domain.contains("static"))) {
            return false;
        }
        return true;
    }

    private void initWallpapers() {
        mHelper.fetchWallpapers(this);
        mProgressBar.setVisibility(View.INVISIBLE);

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        recyclerViewAdapter = new PictureAdapter(getActivity(), mHelper.getWallpapers());
        recyclerView.setAdapter(recyclerViewAdapter);
    }


    public void disableRefreshing() {
        mSwipeRefreshLayout.setRefreshing(false);
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
                //imagesList.setAdapter(new ImageAdapter(getActivity(), searchWallpapers(newText)));
                recyclerView.setAdapter(new PictureAdapter(getActivity(), searchWallpapers(newText)));
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
                //imagesList.setAdapter(new ImageAdapter(getActivity(), wallpapers));
                return true;
            }
        });
    }

    @Override
    public void onWallpapersFetched(List<Submission> wallpapers) {

        mProgressBar.setVisibility(View.INVISIBLE);

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        recyclerViewAdapter = new PictureAdapter(getActivity(), wallpapers);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void onFetchingError() {
        //TODO - Notice user that fetching failed
        Toast.makeText(getActivity(), "Picture loading failed", Toast.LENGTH_SHORT).show();
    }
}
