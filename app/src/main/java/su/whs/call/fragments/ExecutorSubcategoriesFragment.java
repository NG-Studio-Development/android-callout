package su.whs.call.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import su.whs.call.R;
import su.whs.call.adapters.ExecutorSubcategoriesAdapter;
import su.whs.call.dialog.InfoDialog;
import su.whs.call.models.ExecutorSubcategory;
import su.whs.call.models.RegisteredYear;
import su.whs.call.models.UserInfo;
import su.whs.call.net.ConnectionHandler;

public class ExecutorSubcategoriesFragment extends BaseSearchTabFragment {
    private static final String SUBCATEGORIES_ARGS = "subcats";
    private static final String USERINFO_ARGS = "userinfo";

    private ListView mList;
    private UserInfo mUserInfo;
    private List<ExecutorSubcategory> mSubcategories;
    private static ExecutorSubcategoriesFragment mInstance = null;
    private RegisteredYear mYear;

    public static ExecutorSubcategoriesFragment newInstance(ArrayList<ExecutorSubcategory> data, UserInfo userInfo) {
        Bundle arguments = new Bundle();
        if (data != null && userInfo != null) {
            arguments.putSerializable(SUBCATEGORIES_ARGS, data);
            arguments.putSerializable(USERINFO_ARGS, userInfo);
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
            mUserInfo = (UserInfo) args.getSerializable(USERINFO_ARGS);

            ExecutorSubcategoriesAdapter adapter = new ExecutorSubcategoriesAdapter(getActivity(), mUserInfo, mSubcategories/*, "4"*/);

            mList.setAdapter(adapter);

            adapter.setBtnClickListener(new ExecutorSubcategoriesAdapter.BtnClickListener() {
                @Override
                public void onReviewsClick(ExecutorSubcategory subcategory) {
                    openFragment(SubcategoryReviewsFragment.newInstance(subcategory.getReviews()));
                }

                public void onDescriptionClick(ExecutorSubcategory subcategory) {
                    //openFragment(SubcategoryReviewsFragment.newInstance(subcategory.getReviews()));
                    openFragment(ExecutorEditDescriptionFragment.newInstance(subcategory));
                }

                public void onCountCallClick(ExecutorSubcategory subcategory) {
                    openFragment(CallsFragment.newInstance(subcategory.getCallsList()));
                }

            });
        }



        /*ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());
        handler.queryExecutiveCalls(User.create(getActivity()).getToken(), new ConnectionHandler.OnCallsListener() {
            @Override
            public void onCallsResponse(RegisteredYear year) {
                mYear = year;
                String numberOfCalls = String.format("%S (%d)",getString(R.string.number_of_calls),getTotalCalls());

                ExecutorSubcategoriesAdapter adapter = new ExecutorSubcategoriesAdapter(getActivity(), mUserInfo, mSubcategories, year, numberOfCalls);

                adapter.setBtnClickListener(new ExecutorSubcategoriesAdapter.ReviewsBtnClickListener() {
                    @Override
                    public void onReviewsClick(ExecutorSubcategory subcategory) {
                        openFragment(SubcategoryReviewsFragment.newInstance(subcategory.getReviews()));
                    }
                });

                adapter.setCountCallClickListener(new ExecutorSubcategoriesAdapter.CountCallClickListener() {
                    @Override
                    public void onCountCallClickListener() {
                        if (mYear == null) return;
                        openFragment(CallsFragment.newInstance(mYear));
                    }
                });

                mList.setAdapter(adapter);

            }
        }); */



        mContentView = v;
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public int getTotalCalls() {
        int total = 0;

        try {
            for(int i = 0; i < ConnectionHandler.jsonYears.length(); i++) {
                JSONObject object = ConnectionHandler.jsonYears.getJSONObject(i);
                JSONArray month = object.getJSONArray("months");


                for(int j = 0; j < month.length(); j++) {
                    JSONObject o = month.getJSONObject(j);
                    JSONArray calls =  o.getJSONArray("calls");
                    total += calls.length();

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return total;
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
