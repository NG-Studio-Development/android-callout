package su.whs.call.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import su.whs.call.R;
import su.whs.call.models.SubCategoryReview;
import su.whs.call.views.RateStarsView;

/**
 * Created by featZima on 08.09.2014.
 */
public class SubcategoryReviewsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<SubCategoryReview> mReviews;

    public SubcategoryReviewsAdapter(Context context, List<SubCategoryReview> reviews) {
        mReviews = reviews;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mReviews.size();
    }

    @Override
    public SubCategoryReview getItem(int position) {
        return mReviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Holder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.reviews_list_item_simple, viewGroup, false);
            holder = new Holder();
            holder.userName = (TextView) convertView.findViewById(R.id.textUserName);
            holder.description = (TextView) convertView.findViewById(R.id.textDescription);
            holder.rate = (RateStarsView) convertView.findViewById(R.id.rate);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        SubCategoryReview review = this.getItem(position);
        holder.userName.setText(review.getUserName());
        holder.description.setText(review.getDescription());
        holder.rate.setStars(review.getRate());

        return convertView;
    }

    public class Holder {
        public TextView userName;
        public TextView description;
        public RateStarsView rate;
    }
}
