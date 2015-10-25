package pandoracorporation.com.wildwallpaper.model;

import android.widget.ImageView;

/**
 * Created by BruceWayne on 06/11/2014.
 */
public class Wallpaper {

    private ImageView imageView;
    private String imageId;
    private String imageTitle;

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

}
