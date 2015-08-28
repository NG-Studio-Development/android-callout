package su.whs.call.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import su.whs.call.Constants;
import su.whs.call.R;
import su.whs.call.models.ExecutorSubcategory;
import su.whs.call.models.RegisteredYear;
import su.whs.call.models.UserInfo;
import su.whs.call.views.RateStarsView;
import su.whs.call.views.RoundedImageView;

//import su.whs.call.tools.ImageLoader;

public class ExecutorSubcategoriesAdapter extends BaseAdapter {

	private List<ExecutorSubcategory> subcategories;
	UserInfo mUserInfo;
	private LayoutInflater inflater;
    private Context context;
    private ReviewsBtnClickListener reviewsBtnClickListener;
	private CountCallClickListener countCallClickListener;
	private String numberOfCalls;

	public ExecutorSubcategoriesAdapter(Context context, UserInfo mUserInfo, List<ExecutorSubcategory> subcategories, RegisteredYear year, String numberOfCalls ) {
        this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.subcategories = subcategories;
		this.mUserInfo = mUserInfo;
		this.numberOfCalls = numberOfCalls;
	}



	@Override
	public int getCount() {
		return subcategories.size();
	}

	@Override
	public ExecutorSubcategory getItem(int position) {
		return subcategories.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public class Holder {
		TextView categoryName;
		RateStarsView rate;
        Button reviews;
		Button lastDays;
		Button numberOfCallsBtn;
		RoundedImageView executorAvatar;


		private void setUserAvatar(UserInfo userInfo) {
			if (userInfo.getAvatarURL() != null) {

				ImageLoader.getInstance().loadImage(Constants.API + userInfo.getAvatarURL(), new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						if (loadedImage != null) {
							applyAvatar(loadedImage);
						}
					}
				});
			}
		}

		private void applyAvatar(Bitmap bitmapAvatar) {
			if (bitmapAvatar != null) {

				//clientAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
				//clientAvatar.setImageBitmap(bitmapAvatar);
				executorAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
				executorAvatar.setImageBitmap(bitmapAvatar);
			} else {
				//clientAvatar.setImageResource(R.drawable.avatar_icon);
				executorAvatar.setImageResource(R.drawable.avatar_icon);
			}
		}


	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if (convertView == null) {
			//convertView = inflater.inflate(R.layout.executor_subcategory_list_item, parent, false);
			convertView = inflater.inflate(R.layout.executor_subcat_item, parent, false);
			holder = new Holder();
            holder.categoryName = (TextView) convertView.findViewById(R.id.categoryNameTv);
            holder.lastDays = (Button) convertView.findViewById(R.id.daysBtn);
            holder.rate = (RateStarsView) convertView.findViewById(R.id.rate);
            holder.reviews = (Button) convertView.findViewById(R.id.reviewsBtn);
			holder.numberOfCallsBtn = (Button) convertView.findViewById(R.id.numberOfCallsBtn);
			holder.executorAvatar = (RoundedImageView) convertView.findViewById(R.id.executorAvatar);
			convertView.setTag(holder);
		} else {
			holder = (Holder)convertView.getTag();
		}

		final ExecutorSubcategory subcategory = getItem(position);

		holder.setUserAvatar(mUserInfo);

		holder.reviews.setText(String.format(context.getString(R.string.reviews_cap_with_count),
				subcategory.getReviewCount()));
        holder.rate.setStars(subcategory.getRate());

		holder.numberOfCallsBtn.setText(numberOfCalls);

		holder.numberOfCallsBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				countCallClickListener.onCountCallClickListener();
			}
		});


		holder.categoryName.setText(subcategory.getName());
        holder.reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reviewsBtnClickListener != null) {
                    reviewsBtnClickListener.onClick(subcategory);
                }
            }
        });
        holder.lastDays.setText(subcategory.getLastDays() + " день");
		
		return convertView;
	}

    public void setReviewsBtnClickListener(ReviewsBtnClickListener listener) {
        reviewsBtnClickListener = listener;
    }

	public void setCountCallClickListener(CountCallClickListener countCallClickListener) {
		this.countCallClickListener = countCallClickListener;
	}


	public interface CountCallClickListener {
		public void onCountCallClickListener();
	}


    public interface ReviewsBtnClickListener {
        public void onClick(ExecutorSubcategory subcategory);
    }
}
