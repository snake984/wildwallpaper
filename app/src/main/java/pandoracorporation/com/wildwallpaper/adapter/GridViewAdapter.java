package pandoracorporation.com.wildwallpaper.adapter;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import pandoracorporation.com.wildwallpaper.R;
import pandoracorporation.com.wildwallpaper.database.PictureSQLiteHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TSMIRANI on 15/07/2015. Title Description
 */
public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.ViewHolder> {

    private ArrayList pictures = new ArrayList();
    private List<ContentValues> picsInfo;
    private ViewHolder.OnItemClickListener mItemClickListener;

    public GridViewAdapter(ArrayList data, List<ContentValues> info, ViewHolder.OnItemClickListener listener) {
        this.pictures = data;
        this.picsInfo = info;
        this.mItemClickListener = listener;
    }


    public void remove(String filename) {
        Log.d("ADA", "deleting: " + filename);

        for (int i = 0; i < picsInfo.size(); i++) {
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.imageTitle = (TextView) v.findViewById(R.id.image_description);
        viewHolder.image = (ImageView) v.findViewById(R.id.image_view);
        if (Build.VERSION.SDK_INT >= 21) {
            viewHolder.image.setTag(viewHolder.image.getTransitionName());
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ImageItem item = (ImageItem) pictures.get(position);
        holder.imageTitle.setText(item.getTitle());
        holder.image.setImageBitmap(item.getImage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(v, position);
            }
        });
    }

    public List<ContentValues> getPicsInfos() {
        return picsInfo;
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView imageTitle;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }
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
