package su.whs.call.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import su.whs.call.helper.Screen;
import su.whs.call.models.Category;
import su.whs.call.Constants;
import su.whs.call.R;
import su.whs.call.views.IconButton;

public class GridCategoriesAdapter extends BaseAdapter {

    private static final double ICON_SCALE_FACTOR = 0.4;

    private LayoutInflater mInflater;
    private Context mContext;
    private List<Category> mCategories;
    private AdapterView.OnItemClickListener onItemClickListener = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .build();

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public GridCategoriesAdapter(Context context, List<Category> categories) {
        super();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mCategories = categories;
    }

    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public Category getItem(int position) {
        return mCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.categories_grid_item, null);
            holder = new Holder();
            //holder.iconButton = (IconButton) convertView.findViewById(R.id.iconButton);
            holder.iconImage = (ImageView) convertView.findViewById(R.id.categoryIcon);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.categoryName);
            holder.iconButton = (ImageView) convertView.findViewById(R.id.buttonIcon);

            int h = (Screen.getDisplayHeight(mContext) / 8);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(h, h);
            params.addRule(RelativeLayout.CENTER_IN_PARENT );
            holder.iconButton.setLayoutParams(params);



            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        Category cat = getItem(position);
        holder.tvTitle.setText(cat.getName());

        if (cat.getIconURL() != null) {
            imageLoader.loadImage(Constants.API + cat.getIconURL(), options, new ScaleImageLoadingListener(holder.iconImage));
        }
        holder.iconButton.setTag(position);
        holder.iconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    int position = (Integer) view.getTag();
                    onItemClickListener.onItemClick(null, view, position, 0);
                }
            }
        });
        return convertView;
    }

    private class Holder {
        public ImageView iconImage;
        public TextView tvTitle;
        public ImageView iconButton;
    }

    public class ScaleImageLoadingListener extends SimpleImageLoadingListener {

        private ImageView mImage;

        public ScaleImageLoadingListener(ImageView image) {
            mImage = image;
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            int iconWidthPX = (int) mContext.getResources().getDimension(R.dimen.iconButtonShadowDiameter);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(loadedImage,
                    (int) (iconWidthPX * ICON_SCALE_FACTOR),
                    (int) (iconWidthPX * ICON_SCALE_FACTOR),
                    true);
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = mContext.getTheme();
            theme.resolveAttribute(R.attr.iconNormalColor, typedValue, true);
            changeBitmapColor(scaledBitmap, mImage, typedValue.data);
        }

        private void changeBitmapColor(Bitmap sourceBitmap, ImageView image, int color) {
            Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
            Paint p = new Paint();
            p.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
            image.setImageBitmap(resultBitmap);
            Canvas canvas = new Canvas(resultBitmap);
            canvas.drawBitmap(resultBitmap, 0, 0, p);
        }
    }
}
