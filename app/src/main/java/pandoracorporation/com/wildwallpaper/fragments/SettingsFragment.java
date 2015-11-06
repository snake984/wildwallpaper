package pandoracorporation.com.wildwallpaper.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pandoracorporation.com.wildwallpaper.R;


public class SettingsFragment extends PreferenceFragment {


    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }
    public SettingsFragment() {
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
        //TODO
        View rootView =  inflater.inflate(R.layout.fragment_parameters, container, false);

        rootView.setBackgroundColor(Color.WHITE);
        return rootView;
    }

}
