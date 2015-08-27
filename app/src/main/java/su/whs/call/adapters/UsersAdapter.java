package su.whs.call.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import su.whs.call.Constants;
import su.whs.call.R;
import su.whs.call.models.UserInfo;
import su.whs.call.net.ConnectionHandler;
import su.whs.call.views.RateStarsView;
import su.whs.call.views.RoundedImageView;

public class UsersAdapter extends BaseAdapter {

    public static final int SORT_BY_DISTANCE = 1;
    public static final int SORT_BY_RATING = 2;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .build();

    private List<UserInfo> mUsers = new ArrayList<UserInfo>();
    private LayoutInflater mInflater;
    private ConnectionHandler mConn;
    private Location mLastLocation;
    private Context mContext;
    private int mSortType = SORT_BY_DISTANCE;

    public void sortUsers() {
        Collections.sort(mUsers, new Comparator<UserInfo>() {
            @Override
            public int compare(UserInfo userInfo, UserInfo userInfo2) {
                Boolean isBusy = userInfo.isBusy();
                Boolean isBusy2 = userInfo2.isBusy();

                if (isBusy != isBusy2) {
                    return isBusy.compareTo(isBusy2);
                } else {
                    if (mSortType == SORT_BY_DISTANCE) {
                        if (mLastLocation == null) {

                            return 0;
                        } else if (userInfo.getLocation() == null && userInfo2.getLocation() == null) {

                            return 0;
                        } else if (userInfo.getLocation() == null) {

                            return 1;
                        } else if (userInfo2.getLocation() == null) {

                            return -1;
                        } else {
                            float location = userInfo.getLocation().distanceTo(mLastLocation);
                            float location2 = userInfo2.getLocation().distanceTo(mLastLocation);
                            return Float.compare(location, location2);
                        }
                    }
                    if (mSortType == SORT_BY_RATING) {

                        Log.d("456789", " = userInfo2.getRate() " + userInfo2.getUserName() + " " + userInfo2.getRate());

                        return Double.compare(userInfo2.getRate(), userInfo.getRate());
                    }
                    return 0;
                }
            }
        });
    }

    public UsersAdapter(Context context, List<UserInfo> users) {
        mConn = ConnectionHandler.getInstance(context);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mUsers = users;
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    public void setSortType(int sortType) {
        mSortType = sortType;
        sortUsers();
        notifyDataSetChanged();
    }

    public void setLastLocation(Location lastLocation) {
        mLastLocation = lastLocation;
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        RoundedImageView mAvatar;
        ImageView mBusyMark;
        TextView mCategory;
        TextView mUserName;
        RateStarsView mRate;
        TextView mDistance;

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

    private Holder createHolder(View row) {
        Holder h = new Holder();
        h.mAvatar = (RoundedImageView) row.findViewById(R.id.avatarView);
        h.mBusyMark = (ImageView) row.findViewById(R.id.busyMark);
        h.mCategory = (TextView) row.findViewById(R.id.textCategory);
        h.mUserName = (TextView) row.findViewById(R.id.textUserName);
        h.mDistance = (TextView) row.findViewById(R.id.textDistance);
        h.mRate = (RateStarsView) row.findViewById(R.id.rate);
        return h;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.users_list_item_simple, parent, false);
            holder = createHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        UserInfo ui = mUsers.get(position);
        if (ui.getAvatarURL() != null) {
            holder.mAvatar.setImageResource(R.drawable.avatar_icon);
            imageLoader.displayImage(Constants.API + ui.getAvatarURL(), holder.mAvatar, options, animateFirstListener);
        } else {
            holder.mAvatar.setImageResource(R.drawable.avatar_icon);
        }
        holder.mUserName.setText(ui.getUserName());
        holder.mRate.setStars(ui.getRate());
        holder.mBusyMark.setImageResource(ui.isBusy() ? R.drawable.ic_circle_red_small : R.drawable.ic_circle_green_small);
        //holder.mBusyMark.setImageResource(ui.isBusy() ? R.drawable.ic_circle_red_small : R.drawable.ic_circle_red_small);
        holder.setAlphaForAllView(ui.isBusy() ? Constants.ALPHA_VIEW_FOR_BUSY : 1f);
        //if (ui.isBusy())
            //holder.setAlphaForAllView(0.3f);
        //else
            //holder.mAvatar.setEnabled(false);

        if (mLastLocation != null && ui.getLocation() != null) {
            int distance = (int) mLastLocation.distanceTo(ui.getLocation());
            if (distance < 1000) {
                holder.mDistance.setText(String.format(mContext.getString(R.string.distance_m), distance));
            } else {
                holder.mDistance.setText(String.format(mContext.getString(R.string.distance_km), (int) (distance / 1000)));
            }

        } else {
            holder.mDistance.setText(mContext.getString(R.string.empty));
        }

        return convertView;
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        // static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            ImageView imageView = (ImageView) view;
            if (loadedImage != null) {
                imageView.setImageBitmap(loadedImage);
                /*boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }*/
            } else {
                imageView.setImageResource(R.drawable.avatar_icon);
            }
        }
    }

}
