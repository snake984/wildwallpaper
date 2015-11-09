package pandoracorporation.com.wildwallpaper.fragments;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import pandoracorporation.com.wildwallpaper.R;
import pandoracorporation.com.wildwallpaper.activities.MainActivity;
import pandoracorporation.com.wildwallpaper.dao.PictureDao;
import pandoracorporation.com.wildwallpaper.database.PictureSQLiteHelper;
import pandoracorporation.com.wildwallpaper.utils.FileHelper;

import java.sql.SQLException;
import java.util.List;


public class SettingsFragment extends PreferenceFragment {


    private static final CharSequence KEY_CLEAR_PICTURES = "key_clear_pictures";

    private PictureDao mPicDao;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);


        mPicDao = new PictureDao(getActivity());
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Preference clearPicturesPreference = findPreference(KEY_CLEAR_PICTURES);

        clearPicturesPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        deleteAllPictures();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), getString(R.string.pictures_deleted), Toast.LENGTH_SHORT).show();
                                ((MainActivity) getActivity()).refreshNumberOfPics();
                            }
                        });
                    }
                };

                Thread thread = new Thread(runnable);
                thread.start();
                return true;
            }
        });
    }

    private void deleteAllPictures() {
        try {
            mPicDao.open();
            List<ContentValues> images =  mPicDao.getAll();

            if(images != null && images.size() > 0) {
                for(ContentValues image : images) {
                    FileHelper.deleteFile(image.getAsString(PictureSQLiteHelper.KEY_FILENAME));
                }
            }
            mPicDao.deleteAll();
            mPicDao.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
