package su.whs.call.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import su.whs.call.R;
import su.whs.call.adapters.PagerAdapter;
import su.whs.call.dialog.InfoDialog;
import su.whs.call.models.CallsExpert;
import su.whs.call.utils.BackPressed;

public class CallsFragment extends BaseSearchTabFragment implements View.OnClickListener, BackPressed.OnBackPressedListener  {

    private final static String CALLS_ARG = "year_arg";

    private ViewPager pager;
    private TextView currentYear;
    private ImageView imgLeft;
    private ImageView imgRight;
    private List<CallsExpert> mCallsList = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null ) {
            mCallsList = (List<CallsExpert>) getArguments().getSerializable(CALLS_ARG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calls_fragment, container, false);

        if (mCallsList == null)
            throw new Error("mCallsList can not be null !!!");

        currentYear = (TextView) rootView.findViewById(R.id.currentYear);
        imgLeft = (ImageView) rootView.findViewById(R.id.imgLeft);
        imgLeft.setOnClickListener(this);
        imgRight = (ImageView) rootView.findViewById(R.id.imgRight);
        imgRight.setOnClickListener(this);
        pager = (ViewPager) rootView.findViewById(R.id.pager);

        final PagerAdapter pagerAdapter =
                new PagerAdapter(getActivity().getSupportFragmentManager(), mCallsList);

        pagerAdapter.setCountPages(CallsExpert.getCountYear(mCallsList));

        pager.setAdapter(pagerAdapter);

        imgLeft.setVisibility(View.INVISIBLE);

        if(pagerAdapter.getCount() == 1) {
                currentYear.setText(String.valueOf(pagerAdapter.getYear(0)));
                imgRight.setVisibility(View.VISIBLE);
        }

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {

                    currentYear.setText(pagerAdapter.getYear(position));

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
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        mContentView = rootView;

        BackPressed.setOnBackPressedFragmentListener(this);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        setContentShown(true);
        super.onResume();
    }

    @Override
    public boolean onHomeIconClick() {
        return onBack();
    }

    protected boolean onBack() {
        openFragment(ExecutorSubcategoriesFragment.getInstanceFromPool());
        return true;
    }

    @Override
    public String getTitle() {
        return getString(R.string.number_of_calls).toUpperCase();
    }

    public synchronized static CallsFragment newInstance(List<CallsExpert> listCalls) {
        Bundle arguments = new Bundle();

        arguments.putSerializable(CALLS_ARG, (ArrayList<CallsExpert>)listCalls);
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

    @Override
    public void onBackPressed() {
        onBack();
    }
}
