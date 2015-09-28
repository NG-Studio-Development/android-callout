package su.whs.call.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import su.whs.call.Constants;
import su.whs.call.R;
import su.whs.call.helper.Screen;
import su.whs.call.models.Category;

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
        final Holder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.categories_grid_item, null);
            holder = new Holder();
            //holder.iconButton = (IconButton) convertView.findViewById(R.id.iconButton);
            //holder.iconImage = (ImageView) convertView.findViewById(R.id.categoryIcon);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.categoryName);
            holder.iconButton = (ImageButton) convertView.findViewById(R.id.buttonIcon);

            int h = (Screen.getDisplayHeight(mContext) / 8);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(h, h);
            params.addRule(RelativeLayout.CENTER_IN_PARENT );
            holder.iconButton.setLayoutParams(params);
            holder.iconButton.setBackground(mContext.getResources().getDrawable(R.drawable.icon_button_active));
            holder.iconButton.setBackground(mContext.getResources().getDrawable(R.drawable.icon_button_light_normal));



            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        Category cat = getItem(position);
        holder.tvTitle.setText(cat.getName());

        if (cat.getIconURL() != null) {
            Log.d("GRID_CAT", "Url: " + cat.getIconURL());
            imageLoader.loadImage(Constants.API + cat.getIconURL(), options, new ScaleImageLoadingListener(holder.iconButton));
            //holder.iconImage.setImageDrawable(mContext.getResources().getDrawable(mapIcons.get(cat.getIconURL())));
            //holder.iconButton.setImageDrawable(mContext.getResources().getDrawable(mapIcons.get(cat.getIconURL())));
        }






        holder.iconButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    holder.iconButton.setColorFilter(Color.argb(255, 255, 255, 255));
                    holder.iconButton.setBackground(mContext.getResources().getDrawable(R.drawable.icon_button_active));
                    //holder.iconImage.setColorFilter(Color.argb(0, 0, 0, 0));
                } else if(event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
                    holder.iconButton.setColorFilter(Color.argb(0, 0, 0, 0));
                    holder.iconButton.setBackground(mContext.getResources().getDrawable(R.drawable.icon_button_light_normal));

                    if (onItemClickListener != null) {
                        int position = (Integer) view.getTag();
                        onItemClickListener.onItemClick(null, view, position, 0);
                    }

                }

                return true;
            }
        });


        /*
         holder.iconImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    holder.iconImage.setColorFilter(Color.argb(255, 255, 255, 255));
                    //holder.iconImage.setColorFilter(Color.argb(0, 0, 0, 0));
                } else if(event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
                    holder.iconImage.setColorFilter(Color.argb(0, 0, 0, 0));
                }
                Log.d("ACTION_CAT", "Action: " + event.getAction());

                return true;
            }
        });
        */

        holder.iconButton.setTag(position);
        /* holder.iconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ACTION_CAT", "On itmem");
                if (onItemClickListener != null) {
                    int position = (Integer) view.getTag();
                    onItemClickListener.onItemClick(null, view, position, 0);
                }
            }
        }); */
        return convertView;
    }

    private class Holder {
        //public ImageView iconImage;
        public TextView tvTitle;
        public ImageButton iconButton;
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
