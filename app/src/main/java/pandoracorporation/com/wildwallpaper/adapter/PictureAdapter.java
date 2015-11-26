package pandoracorporation.com.wildwallpaper.adapter;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.github.jreddit.entity.Submission;
import com.squareup.picasso.Callback;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import pandoracorporation.com.wildwallpaper.R;
import pandoracorporation.com.wildwallpaper.activities.MainActivity;
import pandoracorporation.com.wildwallpaper.dao.PictureDao;
import pandoracorporation.com.wildwallpaper.utils.FileHelper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by snake984 on 08/05/2015.
 */
public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {

    private final static String NON_THIN = "[^iIl1\\.,']";
    private List<Submission> picturesLinks;
    private Picasso picasso;
    private Context mContext;
    private boolean ellipsized;
    private WallpaperTarget mWallpaperTarget;
    private PictureDao mPictureDao;

    //Adapter constructor
    public PictureAdapter(Context context, List<Submission> pics) {
        mContext = context;
        picturesLinks = pics;
        ellipsized = false;

        //Setting Picasso
        Picasso.Builder builder = new Picasso.Builder(mContext);
        OkHttpDownloader downloader = new OkHttpDownloader(mContext);
        builder.downloader(downloader);
        picasso = builder.build();
        picasso.setLoggingEnabled(true);

        mPictureDao = new PictureDao(context);
    }

    //Create new views
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int parentType) {
        //Create a new view
        CardView v = (CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.picture_card, viewGroup,
                false);

        ViewHolder vh = new ViewHolder(v);
        vh.mImageView = (ImageView) v.findViewById(R.id.item_image);
        vh.mTextView = (TextView) v.findViewById(R.id.item_title);
        vh.mProgressBar = (ProgressBar) v.findViewById(R.id.image_progress_bar);
        vh.mProgressBar.setVisibility(View.VISIBLE);

        vh.mSaveButton = (ImageButton) v.findViewById(R.id.save_button);
        vh.mSetButton = (ImageButton) v.findViewById(R.id.set_wallpaper_button);

        return vh;
    }

    //Replace the contents of a view
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        ellipsized = true;
        String text = ellipsize(picturesLinks.get(position).getTitle(), 50);
        viewHolder.mTextView.setText(text);
        picasso.load(picturesLinks.get(position).getUrl()).resize(width, width).centerInside().into(
                viewHolder.mImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        viewHolder.mProgressBar.setVisibility(View.GONE);
                        Log.d("Picasso", "Picture " + picturesLinks.get(position).getUrl() + " loaded");
                    }

                    @Override
                    public void onError() {
                        viewHolder.mProgressBar.setVisibility(View.GONE);
                        Log.d("Picasso", "Picture " + picturesLinks.get(position).getUrl() + " failed to load");
                    }
                });

        viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ellipsized) {
                    viewHolder.mTextView.setText(picturesLinks.get(position).getTitle());
                    ellipsized = !ellipsized;
                } else {
                    viewHolder.mTextView.setText(ellipsize(picturesLinks.get(position).getTitle(), 50));
                    ellipsized = !ellipsized;
                }

            }
        });

        viewHolder.mSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeWallpaper(position, picturesLinks.get(position).getUrl());
            }
        });

        viewHolder.mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveWallpaper(position, picturesLinks.get(position).getUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return picturesLinks.size();
    }

    private int textWidth(String str) {
        return (int) (str.length() - str.replaceAll(NON_THIN, "").length() / 2);
    }

    public String ellipsize(String text, int max) {

        if (textWidth(text) <= max) {
            return text;
        }

        // Start by chopping off at the word before max
        // This is an over-approximation due to thin-characters...
        int end = text.lastIndexOf(' ', max - 3);

        // Just one long word. Chop it off.
        if (end == -1) {
            return text.substring(0, max - 3) + "...";
        }

        // Step forward as long as textWidth allows.
        int newEnd = end;
        do {
            end = newEnd;
            newEnd = text.indexOf(' ', end + 1);

            // No more spaces.
            if (newEnd == -1) {
                newEnd = text.length();
            }

        } while (textWidth(text.substring(0, newEnd) + "...") < max);

        return text.substring(0, end) + "...";
    }

    private void saveWallpaper(int position, String url) {
        try {
            mWallpaperTarget = new WallpaperTarget(position, false);
            picasso.load(url).into(mWallpaperTarget);
        } catch (NullPointerException e) {
            Log.e("changeWallpaper", "NPE err: " + e.getLocalizedMessage());
            Toast.makeText(mContext, "Something wrong happened", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeWallpaper(int position, String url) {
        try {
            mWallpaperTarget = new WallpaperTarget(position, true);
            picasso.load(url).into(mWallpaperTarget);
        } catch (NullPointerException e) {
            Log.e("changeWallpaper", "NPE err: " + e.getLocalizedMessage());
            Toast.makeText(mContext, "Something wrong happened", Toast.LENGTH_SHORT).show();
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView mCardView;
        public ImageView mImageView;
        public TextView mTextView;
        public ImageButton mSaveButton;
        public ImageButton mSetButton;
        public ProgressBar mProgressBar;

        public ViewHolder(CardView v) {
            super(v);
            mCardView = v;
        }
    }


    public class WallpaperTarget implements Target {

        private Bitmap mPicture;
        private boolean mSetWall;
        private int mPosition;

        public WallpaperTarget(int position, boolean setWall) {
            mPosition = position;
            mSetWall = setWall;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mPicture = bitmap;

            if (mSetWall) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        setWallpaper();
                    }
                };

                Thread thread = new Thread(runnable);
                thread.start();
            } else {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        savePicture();
                    }
                };

                Thread thread = new Thread(runnable);
                thread.start();
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Toast.makeText(mContext, "Something wrong happened", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }

        public void setWallpaper() {
            try {
                WallpaperManager.getInstance(mContext).setBitmap(mPicture);
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "Wallpaper set", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void savePicture() {
            String filename = FileHelper.createDirectoryAndSaveFile(mPicture);
            if (filename != null && filename.length() > 0) {
                try {
                    mPictureDao.open();
                    mPictureDao.add(filename, picturesLinks.get(mPosition));
                    mPictureDao.close();

                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainActivity) mContext).refreshNumberOfPics();
                            Toast.makeText(mContext, "Picture saved", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
            }
        }

        public Bitmap getPicture() {
            return mPicture;
        }

        public void setPicture(Bitmap mPicture) {
            this.mPicture = mPicture;
        }
    }
}
