package su.whs.call.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.yandex.yandexmapkit.utils.GeoPoint;
import su.whs.call.R;
import su.whs.call.adapters.CallsAdapter;
import su.whs.call.adapters.GridCategoriesAdapter;
import su.whs.call.adapters.PagerAdapter;
import su.whs.call.dialog.InfoDialog;
import su.whs.call.form.MainActivity;
import su.whs.call.models.Category;
import su.whs.call.models.RegisteredYear;
import su.whs.call.models.SubCategory;
import su.whs.call.net.ConnectionHandler;
import su.whs.call.net.ConnectionHandler.OnDistanceResponseListener;
import su.whs.call.register.User;

public class CallsFragment extends BaseSearchTabFragment implements View.OnClickListener {

    private final static String YEAR_ARG = "year_arg";

    private ViewPager pager;
    //private  PagerAdapter pagerAdapter;

    private TextView currentYear;
    private ImageView imgLeft;
    private ImageView imgRight;
   // private  CallsFragment mInstance = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calls_fragment, container, false);


        currentYear = (TextView) rootView.findViewById(R.id.currentYear);
        imgLeft = (ImageView) rootView.findViewById(R.id.imgLeft);
        imgLeft.setOnClickListener(this);
        imgRight = (ImageView) rootView.findViewById(R.id.imgRight);
        imgRight.setOnClickListener(this);
        pager = (ViewPager) rootView.findViewById(R.id.pager);

        final PagerAdapter  pagerAdapter = new PagerAdapter(getActivity().getSupportFragmentManager(), ConnectionHandler.jsonYears);
        pagerAdapter.setCountPages(ConnectionHandler.jsonYears.length());
        pager.setAdapter(pagerAdapter);

        imgLeft.setVisibility(View.INVISIBLE);
        if(pagerAdapter.getCount() == 1) {
            try {
                currentYear.setText(pagerAdapter.getJSONObject(0).getString("year"));
                imgRight.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                try {

                    currentYear.setText(pagerAdapter.getJSONObject(position).getString("year"));

                    if(position == 0) {
                        imgLeft.setVisibility(View.INVISIBLE);
                    } else {
                        imgLeft.setVisibility(View.VISIBLE);
                    }

                    if(position == pagerAdapter.getCount() - 1) {
                        imgRight.setVisibility(View.INVISIBLE);
                    } else {
                        imgRight.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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

        //MainActivity.mTabHost.setCurrentTab(2);
        //MainActivity.mTabHost.setCurrentTab(1);
        openFragment(CabinetFragment.newInstance());
        return true;
    }

    @Override
    public String getTitle() {
        return getString(R.string.number_of_calls).toUpperCase();
    }

    public synchronized static CallsFragment newInstance(RegisteredYear year) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(YEAR_ARG, year);
        CallsFragment mInstance = new CallsFragment();
        mInstance.setArguments(arguments);


        return mInstance;
    }

    public synchronized static CallsFragment newInstance() {
        CallsFragment f = new CallsFragment();
        return f;
    }



    @Override
    public void onInfoIconClick() {
        InfoDialog infoDialog = new InfoDialog(getActivity());
        infoDialog.setTitle(getString(R.string.info_calls_total));
        infoDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.imgLeft:
                pager.setCurrentItem(pager.getCurrentItem() - 1);
                break;
            case R.id.imgRight:
                pager.setCurrentItem(pager.getCurrentItem() + 1);
                break;
        }
    }
}
