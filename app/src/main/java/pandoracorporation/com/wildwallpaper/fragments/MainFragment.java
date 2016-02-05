package pandoracorporation.com.wildwallpaper.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.github.jreddit.entity.Submission;
import pandoracorporation.com.wildwallpaper.R;
import pandoracorporation.com.wildwallpaper.activities.MainActivity;
import pandoracorporation.com.wildwallpaper.activities.MapsActivity;
import pandoracorporation.com.wildwallpaper.adapter.PictureAdapter;
import pandoracorporation.com.wildwallpaper.utils.PictureHelper;
import pandoracorporation.com.wildwallpaper.views.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment implements PictureHelper.WallpapersFetchingListener {


    //region Attributes
    private RecyclerView mPicturesRecyclerView;
    private PictureAdapter mPicturesAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PictureHelper mPictureHelper;
    private List<Submission> mPictureSubmissionsList;
    private List<Submission> mFilteredPicturesList; //TODO - Faire la recherche
    private ProgressBar mProgressBar;
    //endregion


    //region Constructor(s)
    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }
    //endregion

    //region Fragment lifecycle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mPictureSubmissionsList = new ArrayList<>();
        mFilteredPicturesList = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = (ProgressBar) view.findViewById(R.id.main_progressbar);
        mProgressBar.setVisibility(View.VISIBLE);

        mPicturesRecyclerView = (RecyclerView) view.findViewById(R.id.ListView);
        mPicturesRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.main_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.accentLight, R.color.primaryDark,
                R.color.accent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPictureHelper.fetchWallpapers(MainFragment.this);
            }
        });

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mSwipeRefreshLayout.setDistanceToTriggerSync(400);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mPicturesRecyclerView.setLayoutManager(mLayoutManager);

        mPicturesAdapter = new PictureAdapter(getActivity(), mPictureSubmissionsList);
        mPicturesAdapter.setHasStableIds(true);
        mPicturesRecyclerView.setAdapter(mPicturesAdapter);

        if (((MainActivity) getActivity()).getSubmissionList() == null) {
            mPictureHelper = new PictureHelper();
            mPictureHelper.fetchWallpapers(this);
        } else {
            mPictureSubmissionsList.clear();
            mPictureSubmissionsList.addAll(((MainActivity) getActivity()).getSubmissionList());
        }

        view.setBackgroundColor(Color.WHITE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                openSearch(item);
                return true;
            default:
                return false;
        }
    }
    //endregion

    //region Activity launcher
    public void activateMapsActivity(View v) {
        TextView textView = (TextView) v.findViewById(R.id.item_title);

        Intent intent = new Intent();
        intent.setClass(getActivity(), MapsActivity.class);
        intent.putExtra("title", textView.getText().toString());
        startActivity(intent);
    }
    //endregion

    //region Search methods
    //Search by title and auhtor
    private List<Submission> searchWallpapers(@NonNull String searchString) {
        if (mPictureSubmissionsList == null) {
            return null;
        }

        if (searchString.length() == 0) {
            return mPictureSubmissionsList;
        }

        mFilteredPicturesList.clear();
        searchString = searchString.toLowerCase();
        for (Submission submission : mPictureSubmissionsList) {
            if (submission.getTitle().toLowerCase().contains(searchString) ||
                    submission.getAuthor().toLowerCase().contains(searchString)) {
                mFilteredPicturesList.add(submission);
            }
        }

        return mFilteredPicturesList;
    }

    private void openSearch(MenuItem item) {
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchWallpapers(newText);
                mPicturesAdapter.setPictures(mFilteredPicturesList);
                mPicturesAdapter.notifyDataSetChanged();
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mPicturesAdapter.setPictures(mPictureSubmissionsList);
                mPicturesAdapter.notifyDataSetChanged();
                return true;
            }
        });


        item.expandActionView();
    }

    //endregion

    //region UI
    public void disableRefreshing() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
    //endregion

    //region Wallpapers fetching callbacks
    @Override
    public void onWallpapersFetched(List<Submission> wallpapers) {

        if (mProgressBar != null && mProgressBar.isShown()) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        this.mPictureSubmissionsList.clear();
        ((MainActivity) getActivity()).setSubmissionList(mPictureHelper.getWallpapers());
        this.mPictureSubmissionsList.addAll(mPictureHelper.getWallpapers());
        mPicturesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFetchingError() {
        Toast.makeText(getActivity(), "Picture loading failed", Toast.LENGTH_SHORT).show();
    }
    //endregion
}
