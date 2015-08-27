package su.whs.call.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import su.whs.call.R;
import su.whs.call.adapters.SubcategoryReviewsAdapter;
import su.whs.call.dialog.InfoDialog;
import su.whs.call.models.SubCategoryReview;

public class SubcategoryReviewsFragment extends BaseSearchTabFragment {

    private static final String REVIEWS_ARG = "reviews";

    private ListView mList;
	private List<SubCategoryReview> mReviews;
	
	public static SubcategoryReviewsFragment newInstance(ArrayList<SubCategoryReview> reviews) {
		Bundle arguments = new Bundle();
		if (reviews != null) {
			arguments.putSerializable(REVIEWS_ARG, reviews);
		}
		SubcategoryReviewsFragment f = new SubcategoryReviewsFragment();
		f.setArguments(arguments);

        return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.subcategory_reviews_fragment, container, false);
        mList = (ListView) rootView.findViewById(R.id.reviews);

		Bundle args = getArguments();
		if (args.containsKey(REVIEWS_ARG)) {
			mReviews =  (List<SubCategoryReview>) args.getSerializable(REVIEWS_ARG);
            Collections.reverse(mReviews);
            mList.setAdapter(new SubcategoryReviewsAdapter(getActivity(), mReviews));
		}

        mContentView = rootView;
		return super.onCreateView(inflater, container, savedInstanceState);
	}

    @Override
    public void onResume() {
        setContentShown(true);
        super.onResume();
    }

	@Override
	public boolean onHomeIconClick() {
        openFragment(ExecutorSubcategoriesFragment.restoreInstance());
		return true;
	}

	@Override
	public String getTitle() {
        return getString(R.string.reviews_cap).toUpperCase();
	}

    @Override
    public void onInfoIconClick() {
        InfoDialog infoDialog = new InfoDialog(getActivity());
        infoDialog.setTitle(getString(R.string.info_reviews));
        infoDialog.show();
    }


}
