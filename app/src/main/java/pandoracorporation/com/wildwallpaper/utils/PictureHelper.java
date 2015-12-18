package pandoracorporation.com.wildwallpaper.utils;

import android.content.Context;
import com.github.jreddit.entity.Submission;
import com.github.jreddit.retrieval.Submissions;
import com.github.jreddit.retrieval.params.SubmissionSort;
import com.github.jreddit.utils.restclient.PoliteHttpRestClient;
import com.github.jreddit.utils.restclient.RestClient;
import pandoracorporation.com.wildwallpaper.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Created by BruceWayne on 06/11/2014.
 */
public class PictureHelper {

    private static final String SUBREDDIT = "EarthPorn/";
    private static final String[] pictureExtensions = {
            "jpg",
            "png",
            "bmp"
    };

    ExecutorService executorService;
    CompletionService<List<Submission>> completionService;
    Future completedFuture;
    private ArrayList<Submission> wallpapers;


    public PictureHelper() {

        executorService = Executors.newSingleThreadExecutor();
        completionService = new ExecutorCompletionService<>(executorService);
        wallpapers = new ArrayList<>();

        completedFuture = executorService.submit(new Runnable() {
            @Override
            public void run() {
                RestClient restClient = new PoliteHttpRestClient();

                // Handle to Submissions, which offers the basic API submission functionality
                Submissions subms = new Submissions(restClient);

                // Retrieve submissions of a submission
                List<Submission> list = subms.ofSubreddit("earthporn", SubmissionSort.NEW, -1, 100, null, null, true);

                for (Submission submission : list) {
                    if (domainAllowed(submission.getDomain()) && checkExtension(submission.getUrl())) {
                        wallpapers.add(submission);
                    }
                }
            }
        });


    }

    private static boolean domainAllowed(String domain) {
        if (domain.contains("EarthPorn") || (domain.contains("flickr.com") && !domain.contains("static"))) {
            return false;
        }
        return true;
    }

    public void fetchWallpapers(WallpapersFetchingListener listener) {

        try {
            completedFuture.get();
            executorService.shutdown();
            listener.onWallpapersFetched(wallpapers);
        } catch (InterruptedException e) {
            e.printStackTrace();
            listener.onFetchingError();
        } catch (ExecutionException e) {
            e.printStackTrace();
            listener.onFetchingError();
        }
    }

    //TODO - Enlever le context d'ici
    private boolean checkExtension(String url) {

        for (String extension : pictureExtensions) {
            if (url.contains(extension)) {
                return true;
            }

        }

        return false;
    }

    public ArrayList<Submission> getWallpapers() {
        return wallpapers;
    }


    public interface WallpapersFetchingListener {
        void onWallpapersFetched(List<Submission> wallpapers);

        void onFetchingError();
    }

}
