package su.whs.call.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.yandex.yandexmapkit.utils.GeoPoint;
import su.whs.call.dialog.InfoDialog;
import su.whs.call.models.Category;
import su.whs.call.R;
import su.whs.call.models.SubCategory;
import su.whs.call.adapters.GridCategoriesAdapter;
import su.whs.call.net.ConnectionHandler;
import su.whs.call.net.ConnectionHandler.OnDistanceResponseListener;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CategoriesFragment extends BaseSearchTabFragment implements OnDistanceResponseListener, OnItemClickListener {
    // private PaginatedGridView mGridView;
    private GridView gridView;
    private List<Category> mCategories;
    private GridCategoriesAdapter mAdapter;
    private static CategoriesFragment mInstance = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.categories_fragment, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        Bundle arguments = getArguments();
        if (arguments.containsKey("location")) {
            Location loc = arguments.getParcelable("location");
            load(loc);
        }
        mContentView = rootView;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        if (mAdapter != null) mAdapter.notifyDataSetChanged();
        super.onResume();
    }

    private void load(Location loc) {
        ConnectionHandler.getInstance(getActivity()).queryDistance(loc, this);
    }

    @Override
    public boolean onHomeIconClick() {
        openFragment(ChooseLocationFragment.newInstance());
        return true;
    }

    @Override
    public String getTitle() {
        return getString(R.string.categories).toUpperCase();
    }

    public synchronized static CategoriesFragment newInstance(GeoPoint gp) {
        Location loc = new Location("");
        loc.setLatitude(gp.getLat());
        loc.setLongitude(gp.getLon());
        Bundle arguments = new Bundle();
        arguments.putParcelable("location", loc);
        mInstance = new CategoriesFragment();
        mInstance.setArguments(arguments);
        return mInstance;
    }

    public synchronized static CategoriesFragment newInstance() {
        CategoriesFragment f = new CategoriesFragment();
        return f;
    }

    @Override
    public void onDistanceResponse(JSONObject json) {
        final List<Category> categories = new ArrayList<Category>();
        if (json.has("data")) {
            JSONArray data;
            try {
                data = json.getJSONArray("data");
            } catch (JSONException e) {
                onError();
                return;
            }
            for (int i = 0; i < data.length(); i++) {
                JSONObject category;
                try {
                    category = data.getJSONObject(i);
                } catch (JSONException e) {
                    continue;
                }
                categories.add(new Category(category));
            }
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCategories = categories;
                Category comingSoon = new Category();
                comingSoon.mName = "скоро";
                mCategories.add(comingSoon);
                mAdapter = new GridCategoriesAdapter(getActivity(), categories);
                mAdapter.setOnItemClickListener(CategoriesFragment.this);
                gridView.setAdapter(mAdapter);
                setContentShown(true);
            }
        });
    }

    private void onError() {
        Toast.makeText(getActivity(), getString(R.string.loading_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mCategories != null && mCategories.size() > position) {
            ArrayList<SubCategory> subs = (ArrayList<SubCategory>) mCategories.get(position).getSubCategories();
            if (subs != null && subs.size() > 0) {
                openFragment(SubCategoriesFragment.newInstance(subs));
            } else {
                //Toast.makeText(getActivity(), "empty category", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static Fragment restoreInstance() {
        if (mInstance == null) {
            mInstance = newInstance();
        }
        return mInstance;
    }

    @Override
    public void onInfoIconClick() {
        InfoDialog infoDialog = new InfoDialog(getActivity());
        infoDialog.setTitle(getString(R.string.info_select_category));
        infoDialog.show();
    }

}