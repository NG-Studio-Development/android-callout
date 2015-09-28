package su.whs.call.form;


import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;

import su.whs.call.R;
import su.whs.call.fragments.BaseFragment;
import su.whs.call.fragments.LoginFragment;
import su.whs.call.fragments.SearchChooseLocationPromptFragment;
import su.whs.call.register.User;
import su.whs.call.views.TitleBar;

/**
 * Created by ProgLife-1 on 19.02.2015.
 */
public class SearchActivity extends FragmentActivity  implements
        TabHost.OnTabChangeListener,
        TitleBar.TitleBarActivity,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        View.OnClickListener {

    public interface OnBackPressedListener {
        public void onBackPressed();
    }

    private View mSplash = null;
    private FragmentTabHost mTabHost;
    private TabWidget tabWidget;
    private TitleBar mTitleBar;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private String mLastTab = "search";

    public static final String TAG_SEARCH = "search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_search);
        mSplash = findViewById(R.id.splash);

        mTabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        tabWidget = mTabHost.getTabWidget();

        mTitleBar = (TitleBar) findViewById(R.id.titleBar);
        assert (mTitleBar != null);

        /** Add fragment with MAP */
        setupTab(TAG_SEARCH, R.drawable.ic_tab_ic_tab_search, R.string.search, SearchChooseLocationPromptFragment.class);

        servicesConnected();
        mTabHost.setOnTabChangedListener(this);


        Log.e("onCreate", "Vie = " + tabWidget.getTabCount());

    }





    public void setupTab(String tag, int drawable, int string, Class<? extends BaseFragment> fragment) {

        View tabview = createTabView(mTabHost.getContext(), drawable, string);
        if ("search".equals(tag)) {

            tabview.setBackgroundResource(R.drawable.tab_indicator_search_bg_light);
        }
        tabview.setTag(tag);
        TabHost.TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview);


        if (fragment != null) {
            mTabHost.addTab(setContent, fragment, null);

        } else {
            mTabHost.addTab(setContent, LoginFragment.class, null);

        }
    }

    private static View createTabView(final Context context, final int drawable, int string) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_indicator, null);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        TextView title = (TextView) view.findViewById(R.id.tabTitle);
        image.setImageResource(drawable);
        title.setText(string, TextView.BufferType.NORMAL);
        return view;
    }


    public void updateTabs() {
        TabWidget tabWidget = mTabHost.getTabWidget();

        for (int i = 0; i < tabWidget.getTabCount(); i++) {
            View v = tabWidget.getChildAt(i);
            View tabView = tabWidget.getChildTabViewAt(i);
            User user = User.create(this);

            if (v.getTag().equals(TAG_SEARCH)) {
                if (user.isExecutor()) {
                    v.setBackgroundResource(R.drawable.tab_indicator_search_disabled);
                    tabView.setEnabled(false);
                } else {
                    v.setBackgroundResource(R.drawable.tab_indicator_search_bg_light);
                    tabView.setEnabled(true);
                }
            }


        }
    }



    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
          /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
        }

    }


    public void setCurrentTabByTag(String tag) {
        mTabHost.setCurrentTabByTag(tag);
    }

    public void setCurrentTabByTagHack() {
        updateTabs();
        mTabHost.setCurrentTabByTag("search");
        mTabHost.setCurrentTabByTag("login");
    }


    @Override
    public void onTabChanged(String tag) {
        mLastTab = tag;
    }

    OnBackPressedListener listener = null;
    public void setOnBackPressedFragmentListener(OnBackPressedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBackPressed() {
        Log.d("ON_BACK_PRESS", "onBackPressed() ");
        /*if (listener != null) {
            listener.onBackPressed();
        } else if (!mTitleBar.navigateBack()) {
            super.onBackPressed();
        } */
    }

    @Override
    public TitleBar getTitleBar() {
        return mTitleBar;
    }

    @Override
    public void fragmentReady(String tag) {
        if (mSplash.getVisibility() == View.VISIBLE) {
            mSplash.setVisibility(View.GONE);

        }

    }



    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason.
            // resultCode holds the error code.
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(getSupportFragmentManager(),
                        "Location Updates");
            }
            return false;
        }
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTabs();
    }
}
