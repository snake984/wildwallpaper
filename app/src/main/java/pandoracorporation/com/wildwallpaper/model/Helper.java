package pandoracorporation.com.wildwallpaper.model;

import android.app.WallpaperManager;
import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.pocketreddit.library.datasources.DataSourceException;
import com.pocketreddit.library.datasources.RedditDataSource;
import com.pocketreddit.library.things.Link;
import com.pocketreddit.library.things.Listing;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import pandoracorporation.com.wildwallpaper.R;

/**
 * Created by BruceWayne on 06/11/2014.
 */
public class Helper {

    private static final String SUBREDDIT = "EarthPorn/";
    private ArrayList<Link> wallpapers;

    public static void saveWallpaper(View v, File directory)
    {
        ImageView img = (ImageView) v.findViewById(R.id.item_image);
        File file = new File(directory, img.getContentDescription().toString());
    }

    public static void changeWallpaper(String url, Context context) {
        URL address = null;
        try {
            address = new URL(url);
        } catch (MalformedURLException e1) {
            Log.e("changeWallpaper", "Wrong URL: " + e1.getLocalizedMessage());
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) address.openConnection();
            WallpaperManager.getInstance(context).setStream(conn.getInputStream());
        } catch (IOException e) {
            Log.e("changeWallpaper", "IO err: " + e.getLocalizedMessage());
        }
    }

    public static List<Link> getWallpapers(List<Link> wallpapers) {
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

    public static List<Link> searchWallpapers(String searchString, List<Link> filtered, List<Link> wallpapers) {
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

    private static boolean domainAllowed(String domain) {
        if (domain.contains("EarthPorn")
                || (domain.contains("flickr.com") && !domain.contains("static")))
            return false;
        return true;
    }

}
