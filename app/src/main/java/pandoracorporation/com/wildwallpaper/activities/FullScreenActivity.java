package pandoracorporation.com.wildwallpaper.activities;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import pandoracorporation.com.wildwallpaper.R;
import pandoracorporation.com.wildwallpaper.dao.PictureDao;
import pandoracorporation.com.wildwallpaper.utils.FileHelper;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by TSMIRANI on 16/07/2015. Title Description
 */
public class FullScreenActivity extends AppCompatActivity {

    public static final int FULLSCREEN_ACTIVITY = 1;


    Toolbar mToolbar;
    ImageView imageView;
    PictureDao mPictureDao;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        //Setting the transition if api level >= 21
        if(Build.VERSION.SDK_INT >= 21) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setAllowEnterTransitionOverlap(true);
            Transition transitionExplode =
                    TransitionInflater.from(this).inflateTransition(R.transition.explode);
            getWindow().setEnterTransition(transitionExplode);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);




        mPictureDao = new PictureDao(this);

        final String filename = getIntent().getStringExtra("filename");
        imageView = (ImageView) findViewById(R.id.imageView);
        Thread bitmapLoadingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = FileHelper.openFullSizePicFromFile(filename);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        });
        bitmapLoadingThread.start();

        //region Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setBackgroundColor(Color.TRANSPARENT);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //endregion

        //region Status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }
        //endregion

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSupportActionBar().isShowing()) {
                    getSupportActionBar().hide();
                } else {
                    getSupportActionBar().show();
                }
            }
        });

        //endregion
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fullscreen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_delete:
                try {
                    String filename = getIntent().getStringExtra("filename");
                    mPictureDao.open();
                    mPictureDao.delete(filename);
                    mPictureDao.close();

                    deletePicture(filename);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("filename", filename);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return true;

            case R.id.action_set_wallpaper:
                String filename = getIntent().getStringExtra("filename");
                setWallpaper(filename);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setWallpaper(final String filename) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = FileHelper.openFullSizePicFromFile(filename);
                    WallpaperManager.getInstance(FullScreenActivity.this).setBitmap(bitmap);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FullScreenActivity.this, getString(R.string.wallpaper_changed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void deletePicture(final String filename) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!FileHelper.deleteFile(filename)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getString(R.string.file_deleted),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }
}
