package su.whs.call.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import su.whs.call.R;
import su.whs.call.adapters.ExecutorSubcategoriesAdapter;
import su.whs.call.adapters.SubCategoryAdapter;
import su.whs.call.dialog.InfoDialog;
import su.whs.call.models.ExecutorSubcategory;
import su.whs.call.models.SubCategory;
import su.whs.call.register.User;

public class ExecutorSubcategoriesFragment extends BaseSearchTabFragment {
    private static final String SUBCATEGORIES_ARGS = "subcats";

    private ListView mList;
    private List<ExecutorSubcategory> mSubcategories;
    private static ExecutorSubcategoriesFragment mInstance = null;

    public static ExecutorSubcategoriesFragment newInstance(ArrayList<ExecutorSubcategory> data) {
        Bundle arguments = new Bundle();
        if (data != null) {
            arguments.putSerializable(SUBCATEGORIES_ARGS, data);
        }
        ExecutorSubcategoriesFragment f = new ExecutorSubcategoriesFragment();
        f.setArguments(arguments);
        mInstance = f;

        return f;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.subcategories_fragment, container, false);

        mList = (ListView) v.findViewById(R.id.listView);

        Bundle args = getArguments();
        if (args.containsKey(SUBCATEGORIES_ARGS)) {
            mSubcategories = (List<ExecutorSubcategory>) args.getSerializable(SUBCATEGORIES_ARGS);
            ExecutorSubcategoriesAdapter adapter = new ExecutorSubcategoriesAdapter(getActivity(), mSubcategories);
            mList.setAdapter(adapter);
            adapter.setReviewsBtnClickListener(new ExecutorSubcategoriesAdapter.ReviewsBtnClickListener() {
                @Override
                public void onClick(ExecutorSubcategory subcategory) {
                    openFragment(SubcategoryReviewsFragment.newInstance(subcategory.getReviews()));
                }
            });
        }

        mContentView = v;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        setContentShown(true);
        super.onResume();
    }

    @Override
    public boolean onHomeIconClick() {
        openFragment(CabinetFragment.newInstance());
        return true;
    }

    @Override
    public String getTitle() {
        return getString(R.string.my_subcategories).toUpperCase();
    }

    public static Fragment restoreInstance() {
        return mInstance;
    }

    @Override
    public void onInfoIconClick() {
        InfoDialog infoDialog = new InfoDialog(getActivity());
        infoDialog.setTitle(getString(R.string.info_executor_subcategories));
        infoDialog.show();
    }

}
