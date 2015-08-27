package su.whs.call.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import su.whs.call.Constants;
import su.whs.call.R;
import su.whs.call.models.RecentCall;
import su.whs.call.views.RateStarsView;
import su.whs.call.views.RoundedImageView;

/**
 * Created by ProgLife-1 on 01.04.2015.
 */
public class FavoritesAdapter extends ArrayAdapter<RecentCall> {

    private Activity mActivity;
    private List<RecentCall> item;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options  = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .build();

    public FavoritesAdapter(Activity mActivity, List<RecentCall> item) {
        super(mActivity, R.layout.users_recent_list_item_simple, item);
        this.mActivity = mActivity;
        this.item= item;
    }

    /*public class Holder {

    }*/


    public class Holder {
        RoundedImageView mAvatar;
        ImageView mBusyMark;
        TextView mCategory;
        TextView mUserName;
        RateStarsView mRate;
        TextView mDistance;

        TextView mDate;
        TextView mTime;

        void setAlphaForAllView(float alpha) {

            if ( mAvatar != null )
                mAvatar.setAlpha(alpha);

            if ( mCategory != null )
                mCategory.setAlpha(alpha);

            if ( mUserName != null )
                mUserName.setAlpha(alpha);

            if ( mRate != null )
                mRate.setAlpha(alpha);

            if ( mDistance != null )
                mDistance.setAlpha(alpha);
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder h;
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater =  mActivity.getLayoutInflater();
            row = inflater.inflate(R.layout.users_recent_list_item_simple, null, true);
            h = new Holder();

            h.mAvatar = (RoundedImageView) row.findViewById(R.id.avatarView);
            h.mBusyMark = (ImageView) row.findViewById(R.id.busyMark);
            h.mCategory = (TextView) row.findViewById(R.id.textCategory);
            h.mUserName = (TextView) row.findViewById(R.id.textUserName);
            h.mDate = (TextView) row.findViewById(R.id.textDate);
            h.mTime = (TextView) row.findViewById(R.id.textTime);
            h.mRate = (RateStarsView) row.findViewById(R.id.rate);

            row.setTag(h);
        } else {
            h = (Holder) row.getTag();
        }

        final RecentCall item = getItem(position);

        if (item.getAvatar() != null) {
            imageLoader.displayImage(Constants.API + item.getAvatar(), h.mAvatar, options, animateFirstListener);
        }
        h.mUserName.setText(item.getUserName());
        h.mRate.setStars(item.getRate());


        h.mBusyMark.setImageResource(item.isBusy() ? R.drawable.ic_circle_red_small : R.drawable.ic_circle_green_small);
        h.setAlphaForAllView(item.isBusy() ? Constants.ALPHA_VIEW_FOR_BUSY : 1f );


            h.mCategory.setText(item.getSubCategoryTitle());
            h.mDate.setText(item.getDate());
            h.mTime.setText(item.getTime());

        return row;
    }


    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }


}
