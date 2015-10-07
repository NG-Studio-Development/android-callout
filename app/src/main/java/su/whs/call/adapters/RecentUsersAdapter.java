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

import com.joooonho.SelectableRoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import su.whs.call.Constants;
import su.whs.call.R;
import su.whs.call.fragments.CabinetFragment;
import su.whs.call.models.RecentCall;
import su.whs.call.models.UserInfo;
import su.whs.call.net.ConnectionHandler;
import su.whs.call.register.User;
import su.whs.call.views.RateStarsView;


public class RecentUsersAdapter extends BaseAdapter {

    public static final int SORT_BY_DISTANCE = 1;
    public static final int SORT_BY_RATING = 2;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options  = new DisplayImageOptions.Builder()
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
    private List<RecentCall> favoriteItems;


    public void sortUsers()
    {
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
                        return Float.compare(userInfo2.getRate(), userInfo.getRate());
                    }
                    return 0;
                }
            }
        });
    }

	public RecentUsersAdapter(Context context,  List<RecentCall> favoriteItems) {
        this.favoriteItems = favoriteItems;
		mConn = ConnectionHandler.getInstance(context);
        mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        User user = User.create(context);
       /* if (user.isLoggedIn()) {
            recentCalls = user.getRecentCalls();
        }*/
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
		SelectableRoundedImageView mAvatar;
		ImageView mBusyMark;
		TextView mCategory;
		TextView mUserName;
		RateStarsView mRate;
		TextView mDate;
        TextView mTime;
	}
	
	private Holder createHolder(View row) {
		Holder h = new Holder();
		h.mAvatar = (SelectableRoundedImageView) row.findViewById(R.id.avatarView);
		h.mBusyMark = (ImageView) row.findViewById(R.id.busyMark);
		h.mCategory = (TextView) row.findViewById(R.id.textCategory);
		h.mUserName = (TextView) row.findViewById(R.id.textUserName);
		h.mDate = (TextView) row.findViewById(R.id.textDate);
		h.mTime = (TextView) row.findViewById(R.id.textTime);
		h.mRate = (RateStarsView) row.findViewById(R.id.rate);
		return h;
	}

    public RecentCall getLastCall(int userId) {
        if (CabinetFragment.calls != null) {
            for (RecentCall rc : CabinetFragment.calls) {
                if (rc.getUserId() == userId) return rc;
            }
        }
        return null;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

        Log.d("2222222222222", "" + favoriteItems.get(position).getUserName());
		final Holder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.users_recent_list_item_simple, parent, false);
			holder = createHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (Holder)convertView.getTag();
		}

		//UserInfo ui = mUsers.get(position);
		if (favoriteItems.get(position).getAvatar() != null) {
            imageLoader.displayImage(Constants.API + favoriteItems.get(position).getAvatar(), holder.mAvatar, options, animateFirstListener);
        }
		holder.mUserName.setText(favoriteItems.get(position).getUserName());
		holder.mRate.setStars(favoriteItems.get(position).getRate());


        holder.mBusyMark.setImageResource(favoriteItems.get(position).isBusy() ? R.drawable.ic_circle_red_small : R.drawable.ic_circle_green_small);



        try {
            holder.mCategory.setText(favoriteItems.get(position).getSubCategoryTitle());
            holder.mDate.setText("" + favoriteItems.get(position).getDateAsText());
            holder.mTime.setText(favoriteItems.get(position).getTimeAsText());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        /*RecentCall call = getLastCall(ui.getId());
        if (call != null) {
            try {

                Log.d("wwwwwwwwwwwww = ", "call != null");

                holder.mCategory.setText(call.getSubCategoryTitle());
                holder.mDate.setText(call.getDateAsText());
                holder.mTime.setText(call.getTimeAsText());
            } catch (ParseException e) {}
       } else {
            Log.d("wwwwwwwwwwwww = ", "call ==== null");
            holder.mCategory.setText(mContext.getString(R.string.empty));
            holder.mDate.setText(mContext.getString(R.string.empty));
            holder.mTime.setText(mContext.getString(R.string.empty));
        }
*/
        /*if (favoriteItems != null) {
            for (FavoriteItem favorite : favoriteItems) {
                if (favorite.userId == ui.getId()) {
                    holder.mCategory.setText(favorite.subCategoryTitle);
                }
            }
        }*/

		return convertView;
	}


   /* private RecentCall getRecentCall(int userId) {
        RecentCall recentCall = null;

        for(RecentCall rc : CabinetFragment.calls) {
            if(rc.getUserId() == userId ) {
                recentCall = new RecentCall()
            }
        }


    }*/

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
