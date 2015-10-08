package su.whs.call.form;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;

import su.whs.call.R;
import su.whs.call.fragments.BaseFragment;
import su.whs.call.fragments.CabinetFragment;
import su.whs.call.fragments.ExecutorSubcategoriesFragment;
import su.whs.call.fragments.LoginFragment;
import su.whs.call.models.UserExtra;
import su.whs.call.net.ConnectionHandler;
import su.whs.call.register.User;
import su.whs.call.utils.BackPressed;
import su.whs.call.views.TitleBar;

/**
 * Created by ProgLife-1 on 19.02.2015.
 */
public class CabinetActivity extends FragmentActivity implements
        TabHost.OnTabChangeListener,
        TitleBar.TitleBarActivity,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        View.OnClickListener {


    private View mSplash = null;
    public static FragmentTabHost mTabHost;
    private TabWidget tabWidget;
    private TitleBar mTitleBar;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private String mLastTab = "search";
    private UserExtra mUi;

    private static final String TAG_LOGIN = "login";


    public static Class<? extends BaseFragment> fragment = CabinetFragment.class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_registration);

        Toast.makeText(this, "CabinetActivity()", Toast.LENGTH_LONG).show();


        /*((Button)findViewById(R.id.btnCabinet)).setSelected(true);
        ((Button)findViewById(R.id.btnCabinet)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CabinetActivity.this, "BTN_CABINET", Toast.LENGTH_LONG).show();
            }
        }); */

        mSplash = findViewById(R.id.splash);

        mTabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        tabWidget = mTabHost.getTabWidget();

        mTitleBar = (TitleBar) findViewById(R.id.titleBar);
        assert (mTitleBar != null);


        User user = User.create(this);
        Class<? extends BaseFragment> fragment = user.isExecutor() ? ExecutorSubcategoriesFragment.class : CabinetFragment.class;

        setupTab(TAG_LOGIN, R.drawable.ic_tab_ic_tab_user, R.string.cabinet, fragment);

        servicesConnected();
        mTabHost.setOnTabChangedListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "Cabinet onStart()", Toast.LENGTH_LONG).show();
    }

    public void loadUserInformation(ConnectionHandler.OnUserInfoListener listener) {
        ConnectionHandler connection = ConnectionHandler.getInstance(this);
        connection.queryUser(User.create(this).getToken(), listener);
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
        //updateTabs();
        mTabHost.setCurrentTabByTag("search");
        mTabHost.setCurrentTabByTag("login");
    }


    @Override
    public void onTabChanged(String tag) {
        mLastTab = tag;
    }

    @Override
    public void onBackPressed() {
        Log.d("ON_BACK_PRESS", "onBackPressed()");

        if (BackPressed.getListener() != null) {
            BackPressed.getListener().onBackPressed();
        }

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
    protected void onResume() {
        super.onResume();
        //updateTabs();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.realtabcontent);
        if (fragment != null) fragment.onActivityResult(requestCode, resultCode, data);

        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                /*
                 * Try the request again
                 */
                        break;
                }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    /*@Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Toast.makeText(this, "CabinetAct onRestInstStat", Toast.LENGTH_LONG).show();

    } */
}