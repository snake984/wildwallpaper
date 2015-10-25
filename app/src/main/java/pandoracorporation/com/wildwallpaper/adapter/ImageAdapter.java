package pandoracorporation.com.wildwallpaper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pocketreddit.library.things.Link;
import com.squareup.picasso.Picasso;

import java.util.List;

import pandoracorporation.com.wildwallpaper.R;

/**
 * Created by BruceWayne on 03/10/2014.
 */
public class ImageAdapter extends BaseAdapter {

    private List<Link> wallpapers = null;
    private List<Link> filtered = null;
    private List<Link> data = null;
    protected static Context context;
    private Filter filter;

    public ImageAdapter(Context c, List<Link> walls) {
        context = c;
        this.wallpapers = walls;
    }

    @Override
    public int getCount() {
        return this.wallpapers.size();
    }

    @Override
    public Object getItem(int position) {
        return this.wallpapers.get(position);
    }

    @Override
    public long getItemId(int position) {
        //return this.wallpapers.get(position).hashCode();
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get display width
        int width = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth();

        // Inflate from the XML
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.image_item, null);

        //Set the text
        TextView txt = (TextView) view.findViewById(R.id.item_title);
        txt.setText(this.wallpapers.get(position).getTitle());
        // Set the image
        ImageView img = (ImageView) view.findViewById(R.id.item_image);
        img.setContentDescription(this.wallpapers.get(position).getTitle());
        Picasso.with(context).load(this.wallpapers.get(position).getUrl())
                .resize(width, width).centerInside().into(img);
        return view;
    }
}