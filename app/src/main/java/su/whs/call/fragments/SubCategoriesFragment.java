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
import su.whs.call.adapters.SubCategoryAdapter;
import su.whs.call.dialog.InfoDialog;
import su.whs.call.models.SubCategory;

public class SubCategoriesFragment extends BaseSearchTabFragment implements OnItemClickListener {
	private ListView mList;
	private List<SubCategory> mData = null;
	private static SubCategoriesFragment mInstance = null;
	
	public static SubCategoriesFragment newInstance(ArrayList<SubCategory> data) {
		Bundle arguments = new Bundle();
		if (data!=null) {
			arguments.putSerializable("subcats", data);

		}

		SubCategoriesFragment f = new SubCategoriesFragment();
		f.setArguments(arguments);
		mInstance = f;

        return f;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.subcategories_fragment, container, false);
		Bundle args = getArguments();
		getTitleBar().setCustomView(getCustomView());
		if (args.containsKey("subcats")) {
			mData =  (List<SubCategory>) args.getSerializable("subcats");
		}
		mList = (ListView) v.findViewById(R.id.listView);
		if (mData!=null) {
			mList.setAdapter(new SubCategoryAdapter(getActivity(), mData));
			mList.setOnItemClickListener(this);
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
		openFragment(CategoriesFragment.restoreInstance());
		return true;
	}

	@Override
	public String getTitle() {
        return getString(R.string.subcategories).toUpperCase();
	}

	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		getApplication().setSubCategory(mData.get(position));
		SubCategory subcat = mData.get(position);
		openFragment(FavoritesFragment.newInstance(subcat));
	}
	
	public static Fragment restoreInstance() {
		return mInstance;
	}

    @Override
    public void onInfoIconClick() {
        InfoDialog infoDialog = new InfoDialog(getActivity());
        infoDialog.setTitle(getString(R.string.info_subcategories));
        infoDialog.show();
    }

}
