package su.whs.call.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import su.whs.call.Constants;
import su.whs.call.R;
import su.whs.call.models.ExecutorSubcategory;
import su.whs.call.models.UserInfo;
import su.whs.call.views.RateStarsView;
import su.whs.call.views.RoundedImageView;

public class ExecutorSubcategoriesAdapter extends BaseAdapter {

	private List<ExecutorSubcategory> subcategories;
	private LayoutInflater inflater;
    private Context context;
    private ReviewsBtnClickListener reviewsBtnClickListener;

	public ExecutorSubcategoriesAdapter(Context context, List<ExecutorSubcategory> subcategories) {
        this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.subcategories = subcategories;
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
		TextView lastDays;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.executor_subcategory_list_item, parent, false);
			holder = new Holder();
            holder.categoryName = (TextView) convertView.findViewById(R.id.categoryName);
            holder.lastDays = (TextView) convertView.findViewById(R.id.lastDays);
            holder.rate = (RateStarsView) convertView.findViewById(R.id.rate);
            holder.reviews = (Button) convertView.findViewById(R.id.reviewsBtn);
			convertView.setTag(holder);
		} else {
			holder = (Holder)convertView.getTag();
		}

		final ExecutorSubcategory subcategory = getItem(position);
		holder.reviews.setText(String.format(context.getString(R.string.reviews_cap_with_count),
                subcategory.getReviewCount()));
        holder.rate.setStars(subcategory.getRate());




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

    public interface ReviewsBtnClickListener {
        public void onClick(ExecutorSubcategory subcategory);
    }
}
