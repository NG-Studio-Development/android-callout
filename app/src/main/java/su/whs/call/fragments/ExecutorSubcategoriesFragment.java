package su.whs.call.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import su.whs.call.Constants;
import su.whs.call.R;
import su.whs.call.adapters.ExecutorSubcategoriesAdapter;
import su.whs.call.dialog.InfoDialog;
import su.whs.call.form.CabinetActivity;
import su.whs.call.models.ExecutorSubcategory;
import su.whs.call.models.RegisteredYear;
import su.whs.call.models.UserExtra;
import su.whs.call.models.UserInfo;
import su.whs.call.net.ConnectionHandler;
import su.whs.call.register.User;

public class ExecutorSubcategoriesFragment extends BaseFragment {

    private static final String SUBCATEGORIES_ARGS = "subcats";
    private static final String USERINFO_ARGS = "userinfo";

    private ListView mList;

    private static UserInfo mUserInfo;
    private static List<ExecutorSubcategory> mSubcategories;
    private static ExecutorSubcategoriesAdapter adapter;

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


    public static ExecutorSubcategoriesFragment getInstanceFromPool() {

        if (mInstance == null)
            mInstance = new ExecutorSubcategoriesFragment();

        return mInstance;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.subcategories_fragment, container, false);



        mList = (ListView) v.findViewById(R.id.listView);

        final ExecutorSubcategoriesAdapter.BtnClickListener executorAdapterListener = new ExecutorSubcategoriesAdapter.BtnClickListener() {
            @Override
            public void onReviewsClick(ExecutorSubcategory subcategory) {
                openFragment(SubcategoryReviewsFragment.newInstance(subcategory.getReviews()));
            }

            public void onDescriptionClick(ExecutorSubcategory subcategory) {
                openFragment(ExecutorEditDescriptionFragment.newInstance(subcategory));
            }

            public void onCountCallClick(ExecutorSubcategory subcategory) {
                openFragment(CallsFragment.newInstance(subcategory.getCallsList()));
            }

            public void onChangeState(ExecutorSubcategory subcategory) {
                ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());
                handler.postStatus(User.create(getActivity()).getToken(), subcategory/*subcategory.getId(), !subcategory.getStatus()*/);

            }

        };


        final ConnectionHandler.OnExecutorCategoriesListener execotorCategoryListener =
                new ConnectionHandler.OnExecutorCategoriesListener() {
                    @Override
                    public void onCategoriesResponse(ArrayList<ExecutorSubcategory> subcategories) {
                        mSubcategories = subcategories;

                        adapter = new ExecutorSubcategoriesAdapter(getActivity(), mUserInfo, mSubcategories/*, "4"*/);
                        mList.setAdapter(adapter);
                        adapter.setBtnClickListener(executorAdapterListener);
                        setContentShown(true);
                    }
                };


        if ( mUserInfo == null || mSubcategories==null ) {
            ( (CabinetActivity) getActivity() ).loadUserInformation(new ConnectionHandler.OnUserInfoListener() {
                @Override
                public void onUserInfoReady(List<UserExtra> allUsers, UserExtra ui) {
                    mUserInfo = ui.getUserInfo();

                    ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());
                    handler.queryExecutorCategories( User.create(getActivity()).getToken(), execotorCategoryListener );
                }

                @Override
                public void onFail() {
                    Toast.makeText(getActivity(), "Fail in loadUserInformation()", Toast.LENGTH_LONG).show();
                }
            } );
        } else {
            adapter.setBtnClickListener(executorAdapterListener);
        }

        if (mUserInfo != null)
            setProfileUsername(mUserInfo.getUserName());

        mContentView = v;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void setProfileUsername(String username) {
        CabinetActivity activity = (CabinetActivity) getActivity();
        if (activity != null
                && username != null
                && !username.equals("null")
                && username.length() > 0) {
            activity.getTitleBar().setTile(username.toUpperCase());
        }
    }

    @Override
    public int getCustomHomeIconId() {
        return android.R.drawable.ic_menu_share;
    }


    @Override
    public boolean onHomeIconClick() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "?????????? ??????? ? ????? ?????????!\n\n\n" + Constants.URL_GOOGLE_PLAY);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.app_name)));

        return true;
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
        super.onResume();

        if (mUserInfo != null && mSubcategories != null) {

            if (mList.getAdapter() == null) {
                mList.setAdapter(adapter);

                setContentShown(true);
            }

        }

    }

    @Override
    public String getTitle() {
        return getString(R.string.cabinet).toUpperCase();
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