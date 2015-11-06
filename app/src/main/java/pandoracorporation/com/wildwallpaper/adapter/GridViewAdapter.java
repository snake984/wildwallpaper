package pandoracorporation.com.wildwallpaper.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pandoracorporation.com.wildwallpaper.R;
import pandoracorporation.com.wildwallpaper.database.PictureSQLiteHelper;

/**
 * Created by TSMIRANI on 15/07/2015.
 * Title
 * Description
 */
public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList pictures = new ArrayList();
    private List<ContentValues> picsInfo;

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList data, List<ContentValues> info) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.pictures = data;
        this.picsInfo = info;
    }


    public void remove(String filename) {
        Log.d("ADA", "deleting: "+filename);

        for(int i=0; i < picsInfo.size(); i++) {
            if (picsInfo.get(i).getAsString(PictureSQLiteHelper.KEY_FILENAME).equals(filename)) {
                picsInfo.remove(i);
                pictures.remove(i);
                notifyDataSetChanged();
                Log.d("ADA", "pic removed");
                break;
            }
        }
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ImageItem item = (ImageItem) pictures.get(position);
        holder.imageTitle.setText(item.getTitle());
        holder.image.setImageBitmap(item.getImage());
        return row;
    }

    public List<ContentValues> getPicsInfo() {
        return picsInfo;
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }

    public static class ImageItem {
        private Bitmap image;
        private String title;

        public ImageItem(Bitmap image, String title) {
            super();
            this.image = image;
            this.title = title;
        }

        public Bitmap getImage() {
            return image;
        }

        public void setImage(Bitmap image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
