package su.whs.call.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.text.ParseException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import su.whs.call.Constants;
import su.whs.call.R;
import su.whs.call.models.CallItem;
import su.whs.call.models.FavoriteItem;
import su.whs.call.models.RecentCall;
import su.whs.call.net.ConnectionHandler;
import su.whs.call.register.User;
import su.whs.call.views.RateStarsView;
import su.whs.call.views.RoundedImageView;

public class RecentCallsAdapter extends BaseAdapter {

    public static final int SORT_BY_DISTANCE = 1;
    public static final int SORT_BY_RATING = 2;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options  = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .build();

	private List<RecentCall> mCalls;
	private LayoutInflater mInflater;
	private ConnectionHandler mConn;
    private Location mLastLocation;
    private Context mContext;
    private int mSortType = SORT_BY_DISTANCE;
    private List<CallItem> recentCalls;
    private List<FavoriteItem> favoriteItems;


	public RecentCallsAdapter(Context context, List<RecentCall> calls) {
        this.favoriteItems = favoriteItems;
		mConn = ConnectionHandler.getInstance(context);
        mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mCalls = calls;
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        User user = User.create(context);
        if (user.isLoggedIn()) {
            recentCalls = user.getRecentCalls();
        }
	}

    public void setLastLocation(Location lastLocation) {
        mLastLocation = lastLocation;
    }

	@Override
	public int getCount() {
		return mCalls.size();
	}

	@Override
	public Object getItem(int position) {
		return mCalls.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	public class Holder {

		TextView mDistance;
		RoundedImageView mAvatar;
		ImageView mBusyMark;
		TextView mCategory;
		TextView mUserName;
		RateStarsView mRate;
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

			if ( mDate != null )
				mDate.setAlpha(alpha);

			if ( mTime != null )
				mTime.setAlpha(alpha);
		}
	}

	private Holder createHolder(View row) {
		Holder h = new Holder();
		h.mAvatar = (RoundedImageView) row.findViewById(R.id.avatarView);
		h.mBusyMark = (ImageView) row.findViewById(R.id.busyMark);
		h.mCategory = (TextView) row.findViewById(R.id.textCategory);
		h.mUserName = (TextView) row.findViewById(R.id.textUserName);
		h.mDate = (TextView) row.findViewById(R.id.textDate);
		h.mTime = (TextView) row.findViewById(R.id.textTime);
		h.mRate = (RateStarsView) row.findViewById(R.id.rate);
		return h;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.users_recent_list_item_simple, parent, false);
			holder = createHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (Holder)convertView.getTag();
		}

		RecentCall call = mCalls.get(position);
		if (call.getAvatar() != null) {
            imageLoader.displayImage(Constants.API + call.getAvatar(), holder.mAvatar, options, animateFirstListener);
		}
		holder.mUserName.setText(call.getUserName());
		holder.mRate.setStars(call.getRate());
		holder.mBusyMark.setImageResource(call.isBusy() ? R.drawable.ic_circle_red_small : R.drawable.ic_circle_green_small);

		holder.setAlphaForAllView(call.isBusy() ? Constants.ALPHA_VIEW_FOR_BUSY : 1f);

        holder.mCategory.setText(call.getSubCategoryTitle());
        try {
            holder.mDate.setText(call.getDateAsText());
            holder.mTime.setText(call.getTimeAsText());
        } catch (ParseException e) { }

		return convertView;
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
