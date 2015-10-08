package su.whs.call.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.yandexmapkit.utils.GeoPoint;
import su.whs.call.R;
import su.whs.call.adapters.GridCategoriesAdapter;
import su.whs.call.dialog.InfoDialog;
import su.whs.call.models.Category;
import su.whs.call.models.SubCategory;
import su.whs.call.net.ConnectionHandler;
import su.whs.call.net.ConnectionHandler.OnDistanceResponseListener;
import su.whs.call.utils.BackPressed;
import su.whs.call.utils.SearchLocation;

public class CategoriesFragment extends BaseSearchTabFragment implements OnDistanceResponseListener, OnItemClickListener, BackPressed.OnBackPressedListener {
    // private PaginatedGridView mGridView;
    private GridView gridView;
    private TextView tvEmptyList;
    private List<Category> mCategories;
    private GridCategoriesAdapter mAdapter;
    private static CategoriesFragment mInstance = null;

    public static boolean wasLoad = false;
    static List<Category> categories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.categories_fragment, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        tvEmptyList = (TextView) rootView.findViewById(R.id.tvEmptyList);

        Bundle arguments = getArguments();
        if (arguments.containsKey("location")) {
            Location loc = arguments.getParcelable("location");
            if (!wasLoad)
                load(loc);
        }
        mContentView = rootView;

        BackPressed.setOnBackPressedFragmentListener(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (wasLoad)
            loadView();
            //setContentShown(true);
    }

    @Override
    public void onResume() {
        if (mAdapter != null) mAdapter.notifyDataSetChanged();
        super.onResume();

    }

    String city = SearchLocation.getInstance().getChosenCity();
    private void load(Location loc) {
        ConnectionHandler.getInstance(getActivity()).queryDistance(city, loc, this);
        //ConnectionHandler.getInstance(getActivity()).queryDistance(loc, this);
    }

    @Override
    public boolean onHomeIconClick() {
        onBack();
        return true;
    }

    private void onBack() {
        openFragment(ChooseLocationFragment.newInstance());
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
        categories = new ArrayList<Category>();
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

        loadView();
    }


    private void loadView() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (categories.size() == 0) {
                    gridView.setVisibility(View.INVISIBLE);
                    tvEmptyList.setVisibility(View.VISIBLE);
                    tvEmptyList.setText(getString(R.string.not_avialable_in_chosen_city));
                }

                mCategories = categories;
                Category comingSoon = new Category();
                comingSoon.mName = "скоро";

                if ( !wasLoad )
                    mCategories.add(comingSoon);

                mAdapter = new GridCategoriesAdapter(getActivity(), categories);
                mAdapter.setOnItemClickListener(CategoriesFragment.this);
                gridView.setAdapter(mAdapter);
                setContentShown(true);
                wasLoad = true;
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

    @Override
    public void onBackPressed() {
        onBack();
    }
}
