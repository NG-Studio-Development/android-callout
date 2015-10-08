package su.whs.call.form;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.util.VKUtil;

import java.util.ArrayList;
import java.util.List;

import su.whs.call.CallApp;
import su.whs.call.Constants;
import su.whs.call.R;
import su.whs.call.database.DBConnector;
import su.whs.call.fragments.SocialFragment;
import su.whs.call.register.User;
import su.whs.call.utils.BackPressed;


public class MainActivity extends TabActivity implements View.OnClickListener, GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";

    private static final String TAG_REGISTER = "register";
    private static final String TAG_LOGIN = "login";
    public static final String TAG_SEARCH = "search";
    private static final String TAG_FAVORITES = "favorites";
    private static final String TAG_EXIT = "exit";

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static TabHost mTabHost;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    public static Button btnCabinet, btnFavorites, btnExit;
    private ImageButton btnSearch, btnRegistration;

    private LocationClient mLocationClient;
    private static Location mLastLocation;


    private static List<TabHost.TabSpec> mTabSpecs = new ArrayList(5);
    //private TabHost.TabSpec spec;

    public static MainActivity main;


    public static DBConnector mDB;
    private SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mDB = new DBConnector(this, Constants.DATABASE_NAME);
        db = mDB.getWritableDatabase();


        main = this;

        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
        Log.e("test", fingerprints[0]);

        btnRegistration = (ImageButton) findViewById(R.id.btnRegistration);
        btnRegistration.setOnClickListener(this);
        btnCabinet = (Button) findViewById(R.id.btnCabinet);
        btnCabinet.setOnClickListener(this);
        btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        btnFavorites = (Button) findViewById(R.id.btnFavorites);
        btnFavorites.setOnClickListener(this);
        btnExit = (Button) findViewById(R.id.btnExit);
        btnExit.setOnClickListener(this);
        mTabHost = getTabHost();

        initTabHost();



        mLocationClient = new LocationClient(this, this, this);
        servicesConnected();

        User user = User.create(this);
        if (user.isLoggedIn() && user.isExecutor()) {
            mTabHost.setCurrentTab(2);
            //btnCabinet.setSelected(true);
        } else {
            mTabHost.setCurrentTab(3);
            //btnSearch.setSelected(true);
        }
    }

    private static void initTabHost() {
        setupTab(TAG_REGISTER, R.drawable.ic_tab_ic_tab_login, R.string.register, RegistrationActivtiy.class);
        setupTab(TAG_LOGIN, R.drawable.ic_tab_ic_tab_user, R.string.cabinet, CabinetActivity.class);
        setupTab("cabinet", R.drawable.ic_tab_ic_tab_user, R.string.cabinet, CabinetActivity.class);

        setupTab(TAG_SEARCH, R.drawable.ic_tab_ic_tab_search, R.string.search, SearchActivity.class);

        setupTab(TAG_FAVORITES, R.drawable.ic_tab_ic_tab_star, R.string.favorites, FavoritesActivtiy.class);
        setupTab(TAG_EXIT, R.drawable.ic_tab_ic_tab_exit, R.string.exit, ExitActivity.class);


        if ( User.create(CallApp.getInstance()).isLoggedIn() )
            mTabHost.getTabWidget().getChildAt(1).setVisibility(View.GONE);
        else
            mTabHost.getTabWidget().getChildAt(2).setVisibility(View.GONE);

    }

    public static Location getLastLocation() {
        return mLastLocation;
    }


    public static void setupTab(String tag, int drawable, int string, java.lang.Class<?> cls) {


        View tabview = createTabView(mTabHost.getContext(), drawable, string);

        if (TAG_SEARCH.equals(tag)) {
            tabview.setBackgroundResource(R.drawable.tab_indicator_search_bg_light);
        }


        User user = User.create(main);
        if(!user.isLoggedIn()) {

            if (TAG_FAVORITES.equals(tag) || TAG_EXIT.equals(tag) ) {
                tabview.setBackgroundResource(R.drawable.tab_indicator_disabled);
                tabview.setEnabled(false);
            }
        } else if(user.isLoggedIn() && user.isExecutor()) {

            if(TAG_SEARCH.equals(tag)) {
                tabview.setBackgroundResource(R.drawable.tab_indicator_search_disabled);
                tabview.setEnabled(false);
            }

            if (TAG_FAVORITES.equals(tag)  || TAG_REGISTER.equals(tag) ) {
                tabview.setBackgroundResource(R.drawable.tab_indicator_disabled);
                tabview.setEnabled(false);
            }
        } else {
            if (TAG_SEARCH.equals(tag)) {
                tabview.setBackgroundResource(R.drawable.tab_indicator_search_bg_light);
            } else {
                tabview.setBackgroundResource(R.drawable.tab_indicator_bg_light);
            }
        }


        if(TAG_EXIT.equals(tag)) {
            tabview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    if (action == MotionEvent.ACTION_DOWN) {
                        return true;
                    }
                    if (action == MotionEvent.ACTION_UP) {
                        if (User.create(main).isLoggedIn()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(main);
                            builder.setMessage(main.getString(R.string.exit_confirm)).setPositiveButton(main.getString(R.string.yes), dialogClickListener)
                                    .setNegativeButton(R.string.no, dialogClickListener).show();
                        }
                    }
                    return true;
                }
            });
        }

        if(TAG_LOGIN.equals(tag)) {
            tabview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    if (action == MotionEvent.ACTION_DOWN) {
                        return true;
                    }
                    if (action == MotionEvent.ACTION_UP) {
                        if (User.create(main).isLoggedIn()) {
                            mTabHost.setCurrentTab(2);
                            mTabHost.getTabWidget().getChildAt(1).setVisibility(View.GONE);
                        } else {
                            mTabHost.setCurrentTab(1);
                            mTabHost.getTabWidget().getChildAt(2).setVisibility(View.GONE);
                        }
                    }
                    return true;
                }
            });
        }

        TabHost.TabSpec spec = mTabHost.newTabSpec(tag).setContent(new Intent(main, cls)).setIndicator(tabview);
        mTabSpecs.add(spec);
        mTabHost.addTab(spec);
    }

    private static View createTabView(final Context context, final int drawable, int string) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_indicator, null);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        TextView title = (TextView) view.findViewById(R.id.tabTitle);
        image.setImageResource(drawable);
        title.setText(string, TextView.BufferType.NORMAL);
        return view;
    }


    static DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:

                    if(SocialFragment.mSimpleFacebook != null && SocialFragment.mSimpleFacebook.getSession() != null) {
                        SocialFragment.mSimpleFacebook.getSession().closeAndClearTokenInformation();
                    }

                    User user = User.create(main);
                    if (user.isLoggedIn()) {
                        user.logout();
                        //user.isExecutor();
                    }
                    updateTabs();
                    setCurrentTabByTag("search");
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    public static void setCurrentTabByTag(String tag) {
        updateTabs();
        mTabHost.setCurrentTabByTag(tag);
    }

    public static void setCurrentTabByTagHack() {
        updateTabs();
        mTabHost.setCurrentTab(3);
        mTabHost.setCurrentTab(1);
    }


    public static void updateTabs() {

        clearAllTabs();
        initTabHost();

    }

    public static void updateTabs(int current) {
        updateTabs();

        if (current == 2) {
            btnCabinet.setSelected(true);
        }
    }

    public static void clearAllTabs() {
        mTabHost.getTabWidget().removeAllViews();
        mTabSpecs.clear();

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);

        //Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.realtabcontent);
        //if (fragment != null) fragment.onActivityResult(requestCode, resultCode, data);

        // Decide what to do based on the original request code

    }

    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        mLastLocation = mLocationClient.getLastLocation();
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
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
                //errorFragment.show(getSupportFragmentManager(),
                //"Location Updates");
            }
            return false;
        }
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
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



    @Override
    public void onBackPressed() {
        Log.d("ON_BACK_PRESS", "onBackPressed() ");

        if (BackPressed.getListener() != null) {
            BackPressed.getListener().onBackPressed();
        }

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnRegistration:
                mTabHost.setCurrentTab(0);
                break;
            case R.id.btnCabinet:
                User user = User.create(this);

                if (!user.isLoggedIn()) {
                    mTabHost.setCurrentTab(1);
                    mTabHost.getTabWidget().getChildAt(2).setVisibility(View.GONE);

                } else {
                    mTabHost.setCurrentTab(2);
                    mTabHost.getTabWidget().getChildAt(1).setVisibility(View.GONE);
                }
                break;
            case R.id.btnSearch:

                mTabHost.setCurrentTab(3);

                break;
            case R.id.btnFavorites:
                mTabHost.setCurrentTab(4);
                break;
            case R.id.btnExit:
                //mTabHost.setCurrentTab(4);

                if (User.create(getApplicationContext()).isLoggedIn()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(getString(R.string.exit_confirm)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                            .setNegativeButton(R.string.no, dialogClickListener).show();
                }


                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //updateTabs();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // super.onSaveInstanceState(outState);
    }

}