package su.whs.call.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joooonho.SelectableRoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import su.whs.call.Constants;
import su.whs.call.R;
import su.whs.call.models.ExecutorSubcategory;
import su.whs.call.models.UserInfo;
import su.whs.call.views.RateStarsView;


public class ExecutorSubcategoriesAdapter extends BaseAdapter {

	private List<ExecutorSubcategory> subcategories;
	//UserInfo mUserInfo;
	private LayoutInflater inflater;
    private Context context;
    private BtnClickListener btnClickListener;

	public ExecutorSubcategoriesAdapter(Context context, UserInfo mUserInfo, List<ExecutorSubcategory> subcategories /*RegisteredYear year,*/ /*String numberOfCalls*/ ) {
        this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.subcategories = subcategories;
		//this.mUserInfo = mUserInfo;
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
		TextView tvModerationState;
		RateStarsView rate;
        Button reviews;
		Button lastDays;
		Button numberOfCallsBtn;
		Button buttonStateSwitcher;
		Button descriptionBtn;
		SelectableRoundedImageView executorAvatar;


		private void setUserAvatar(ExecutorSubcategory subcategory) {
			if (subcategory.getAvatar() != null) {

				/*ImageLoader.getInstance().loadImage(Constants.API + "/" + subcategory.getAvatar(), new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						if (loadedImage != null) {
							applyAvatar(loadedImage);
						}
					}
				}); */

				ImageLoader.getInstance().displayImage(Constants.API + "/" + subcategory.getAvatar(), executorAvatar);
			}
		}

		/*private void applyAvatar(Bitmap bitmapAvatar) {
			if (bitmapAvatar != null) {
				executorAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
				executorAvatar.setImageBitmap(bitmapAvatar);
			} else {
				executorAvatar.setImageResource(R.drawable.avatar_icon);
			}
		} */

		private void setBusyStatus(boolean isBusy) {
			StateListDrawable stateListDrawable = (StateListDrawable) buttonStateSwitcher.getBackground();
			LayerDrawable layerDrawable = (LayerDrawable) stateListDrawable.getCurrent();
			GradientDrawable grayDrawable = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.gray_background);
			if (isBusy) {
				buttonStateSwitcher.setText(context.getString(R.string.current_state_busy));
				grayDrawable.setColor(context.getResources().getColor(R.color.state_busy));
				//busyMark.setImageResource(R.drawable.ic_circle_red_small);
			} else {
				buttonStateSwitcher.setText(context.getString(R.string.current_state_free));
				grayDrawable.setColor(context.getResources().getColor(R.color.state_free));
				//busyMark.setImageResource(R.drawable.ic_circle_green_small);
			}

		}

	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.executor_subcat_item, parent, false);
			holder = new Holder();
            holder.categoryName = (TextView) convertView.findViewById(R.id.categoryNameTv);
            holder.lastDays = (Button) convertView.findViewById(R.id.daysBtn);
            holder.rate = (RateStarsView) convertView.findViewById(R.id.rate);
            holder.reviews = (Button) convertView.findViewById(R.id.reviewsBtn);
			holder.numberOfCallsBtn = (Button) convertView.findViewById(R.id.numberOfCallsBtn);
			holder.buttonStateSwitcher = (Button) convertView.findViewById(R.id.currentStateSwitcher);
			holder.descriptionBtn = (Button) convertView.findViewById(R.id.descriptionBtn);
			holder.executorAvatar = (SelectableRoundedImageView) convertView.findViewById(R.id.executorAvatar);
			holder.tvModerationState = (TextView) convertView.findViewById(R.id.tvModerationState);
			convertView.setTag(holder);
		} else {
			holder = (Holder)convertView.getTag();
		}



		final ExecutorSubcategory subcategory = getItem(position);

		final View clickedView = convertView;

		holder.setUserAvatar(subcategory);

		holder.reviews.setText(String.format(context.getString(R.string.reviews_cap_with_count),
				subcategory.getReviewCount()));
        holder.rate.setStars(subcategory.getRate());

		if ( !subcategory.isPublished() )
			holder.tvModerationState.setVisibility(View.VISIBLE);
		else
			holder.tvModerationState.setVisibility(View.INVISIBLE);

		holder.numberOfCallsBtn.setText(context.getString(R.string.calls) + "(" + String.valueOf(subcategory.getCountCall()) + ")");

		holder.numberOfCallsBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btnClickListener != null)
					btnClickListener.onCountCallClick(subcategory);
			}
		});

		holder.executorAvatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//holder.executorAvatar.setVisibility(View.INVISIBLE);
				if (btnClickListener != null)
					btnClickListener.onAvatarClick(clickedView, subcategory);
			}
		});

		holder.setBusyStatus(subcategory.getStatus());
		holder.buttonStateSwitcher.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btnClickListener != null) {
					holder.setBusyStatus(!subcategory.getStatus());
					btnClickListener.onChangeState(subcategory);
					//notify();
				}
			}
		});

		holder.descriptionBtn.setText(context.getString(R.string.description));

		holder.descriptionBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btnClickListener != null) {
					btnClickListener.onDescriptionClick(clickedView, subcategory);
				}
			}
		});

		holder.categoryName.setText(subcategory.getName());
        holder.reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnClickListener != null) {
                    btnClickListener.onReviewsClick(subcategory);
                }
            }
        });
        holder.lastDays.setText(subcategory.getLastDays() + " день");
		
		return convertView;
	}

    public void setBtnClickListener(BtnClickListener listener) {
        btnClickListener = listener;
    }

	public interface BtnClickListener {
        void onReviewsClick(ExecutorSubcategory subcategory);
        void onDescriptionClick(View view, ExecutorSubcategory subcategory);
		void onCountCallClick(ExecutorSubcategory subcategory);
		void onChangeState(ExecutorSubcategory subcategory);
		void onAvatarClick(View view, ExecutorSubcategory subcategory);
    }



	public static ExecutorSubcategoriesAdapter fillLinearLayout( Context context, UserInfo mUserInfo, List<ExecutorSubcategory> subcategories, LinearLayout linearLayout ) {
		ExecutorSubcategoriesAdapter adapter = new ExecutorSubcategoriesAdapter(context, mUserInfo, subcategories);

		final int adapterCount = adapter.getCount();

		for (int i = 0; i < adapterCount; i++) {
			View itemView = adapter.getView(i, null, null);
			linearLayout.addView(itemView);
		}
		return adapter;
	}

}
