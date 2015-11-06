package pandoracorporation.com.wildwallpaper.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pandoracorporation.com.wildwallpaper.R;
import pandoracorporation.com.wildwallpaper.activities.FullScreenActivity;
import pandoracorporation.com.wildwallpaper.adapter.GridViewAdapter;
import pandoracorporation.com.wildwallpaper.dao.PictureDao;
import pandoracorporation.com.wildwallpaper.database.PictureSQLiteHelper;
import pandoracorporation.com.wildwallpaper.utils.FileHelper;


public class MyPicturesFragment extends Fragment {

    private List<ContentValues> mPictures;

    private GridView mGridView;
    private GridViewAdapter mGridViewAdapter;

    private PictureDao mDao;


    public static MyPicturesFragment newInstance() {
        return new MyPicturesFragment();
    }

    public MyPicturesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_pictures, container, false);

        mDao = new PictureDao(getActivity());

        mGridView = (GridView) rootView.findViewById(R.id.gridView);
        getData();

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), FullScreenActivity.class);
                intent.putExtra("filename", (String) mGridViewAdapter.getPicsInfo().get(position).get(PictureSQLiteHelper.KEY_FILENAME));
                startActivityForResult(intent, FullScreenActivity.FULLSCREEN_ACTIVITY);
            }
        });

        return rootView;
    }

    private void getData() {
        final ArrayList<GridViewAdapter.ImageItem> imageItems = new ArrayList<>();
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity()
                , getString(R.string.please_wait)
                , getString(R.string.loading));

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    mDao.open();
                    mPictures = mDao.getAll();
                    mDao.close();

                    for(int i=0; i < mPictures.size(); i++) {
                        Bitmap bitmap = FileHelper.openThumbnailFromFile(mPictures.get(i).getAsString(PictureSQLiteHelper.KEY_FILENAME));
                        String title = mPictures.get(i).getAsString(PictureSQLiteHelper.KEY_TITLE);
                        imageItems.add(new GridViewAdapter.ImageItem(bitmap, title));
                    }


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mGridViewAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, imageItems, mPictures);
                            mGridView.setAdapter(mGridViewAdapter);
                            progressDialog.dismiss();
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                openSearch(item);
                return super.onOptionsItemSelected(item);
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
                //TODO

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
                //TODO
                return true;
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case FullScreenActivity.FULLSCREEN_ACTIVITY:
                if(resultCode == Activity.RESULT_OK) {
                    if(data != null) {
                        mGridViewAdapter.remove(data.getStringExtra("filename"));
                    }
                }
                break;

            default:
                getData();
                break;
        }
    }
}
