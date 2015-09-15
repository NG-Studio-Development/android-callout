package su.whs.call.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;

import su.whs.call.CallApp;
import su.whs.call.R;
import su.whs.call.views.SearchPanel;
import su.whs.call.views.TitleBar;
import su.whs.call.views.TitleBar.TitleBarActivity;

public abstract class BaseFragment extends ProgressFragment implements NavigationBarClient {
    private TitleBarActivity mActivity = null;
    public TitleBar mTitleBar = null;
    private boolean mViewCreated = false;
    private Runnable onAttachRun = null;
    protected View mContentView;

    public BaseFragment base = this;

    private OnClickListener mHomeIconClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            BaseFragment.this.onHomeIconClick();
        }
    };

    private OnClickListener mInfoIconClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            BaseFragment.this.onInfoIconClick();
        }
    };
    protected boolean isHomeButtonDisabled;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);



        if (activity instanceof TitleBarActivity) {
            mActivity = (TitleBarActivity) activity;
            Log.d("VIEWVIEW", "TitleBarActivity");
            mTitleBar = mActivity.getTitleBar();
        }
        if (onAttachRun != null) {
            onAttachRun.run();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_custom_progress, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        onCreateView();
        setContentView(mContentView);
        super.onActivityCreated(savedInstanceState);
    }

    public void onActivate() {
        try {
            onCreateView();
        } catch (Exception e) {

        }
    }

    protected TitleBar getTitleBar() {
        return mTitleBar;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mActivity != null)
            mActivity.fragmentReady(getTag());
    }

    public void onCreateView() {
        mTitleBar.setListener(this);
        mTitleBar.btnReview.setVisibility(View.GONE);
        int visibility = View.VISIBLE;
        if (!isTitleBarVisible())
            visibility = View.GONE;
        mTitleBar.setVisibility(visibility);
        if (isHomeIconVisible()) {
            mTitleBar.setHomeIconResource(getCustomHomeIconId());
            mTitleBar.setHomeIconClickListener(mHomeIconClickListener);
        } else {
            mTitleBar.setHomeIconClickListener(null);
        }
        if (!SearchPanel.isColorTitle) {

            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getActivity().getTheme();
            theme.resolveAttribute(R.attr.actionBarBackground, typedValue, true);
            mTitleBar.setBackgroundColor(typedValue.data);

            mTitleBar.getTitle().setTextColor(Color.WHITE);
            mTitleBar.getHomeButton().setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_left_white));
            mTitleBar.setCustomViewVisibility(View.VISIBLE);
        } else {
            mTitleBar.setBackgroundColor(Color.TRANSPARENT);
            mTitleBar.setBackgroundResource(android.R.color.transparent);
            mTitleBar.getTitle().setTextColor(Color.BLACK);
            mTitleBar.setHomeIconResource(getCustomHomeIconId());
            mTitleBar.setCustomViewVisibility(View.GONE);
        }

        if (isHomeButtonDisabled) {
            mTitleBar.getHomeButton().setVisibility(View.GONE);
        } else {
            mTitleBar.getHomeButton().setVisibility(View.VISIBLE);
        }


        mTitleBar.setTile(getTitle());
        mTitleBar.setInfoIconResource(getCustomInfoIconId());
        mTitleBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                return;
            }
        });
        mTitleBar.setCustomView(getCustomView());
        boolean customViewVis = isCustomViewVisible();
        mTitleBar.setCustomViewVisibility(customViewVis ? View.VISIBLE : View.GONE);

        /*mTitleBar.setTile(getTitle());
        mTitleBar.setInfoIconResource(getCustomInfoIconId());
        mTitleBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onReviewsClick(View view) {

                return;
            }
        });
        //mTitleBar.setCustomView(getCustomView());
        boolean customViewVis = isCustomViewVisible();
        if (mTitleBar != null) {

        }*/
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        String tag = getTag();
        Fragment old = getActivity().getSupportFragmentManager().findFragmentByTag(tag);
        if (old != null) {
           EditText editSearch = (EditText) getActivity().findViewById(R.id.editSearch);
            if (editSearch != null) {
                InputMethodManager imm = (InputMethodManager) editSearch.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
            }
            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            ft.detach(old);
        }
        if (fragment.isDetached()) {

            ft.attach(fragment);
            if (fragment instanceof BaseFragment) {
                ((BaseFragment) fragment).onActivate();
            }
        } else {

            ft.replace(R.id.realtabcontent, fragment, tag);
            if (fragment instanceof BaseFragment) {
                ((BaseFragment) fragment).onActivate();
            }
        }

        ft.commit();
    }

    public boolean isTitleBarVisible() {
        return true;
    }

    protected boolean isShadowBackground = false;

    @Override
    public int getCustomHomeIconId() {
        return R.drawable.ic_back_button;
    }

    @Override
    public int getCustomInfoIconId() {
        if (isShadowBackground) {
            SearchPanel.isColorTitle = false;
            return R.drawable.ic_info_white;
        } else {
            SearchPanel.isColorTitle = true;
            return R.drawable.ic_info_button;
        }
    }

    @Override
    public boolean isHomeIconVisible() {
        return true;
    }

    @Override
    public boolean isCustomViewVisible() {
        return false;
    }

    @Override
    public View getCustomView() {
        return null;
    }

    @Override
    public boolean onHomeIconClick() {
        return false;
    }

    @Override
    public void onInfoIconClick() {
        Toast.makeText(getActivity(), "help not implemented yet", Toast.LENGTH_LONG).show();
    }

    public CallApp getApplication() {
        return ((CallApp) getActivity().getApplication());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //first saving my state, so the bundle wont be empty.
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

}
