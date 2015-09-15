package su.whs.call.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import su.whs.call.R;
import su.whs.call.dialog.InfoDialog;
import su.whs.call.views.EmailLoginView;
import su.whs.call.views.IconButton;

public class LoginFragment extends SocialFragment {

    private final static String TAG = "LoginFragment";

    private IconButton selectEmail;
    private View mSelector;
    private EmailLoginView mEmailForm;

    public LoginFragment() {
        isHomeButtonDisabled = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        mSelector = view.findViewById(R.id.loginSelector);
        mEmailForm = (EmailLoginView) view.findViewById(R.id.emailLoginView);
        selectEmail = (IconButton) view.findViewById(R.id.selectEmail);
        selectEmail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showEmailForm();
            }
        });

        findSocialButtons(view);

        mContentView = view;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void showEmailForm() {
        mSelector.setVisibility(View.GONE);
        mEmailForm.setVisibility(View.VISIBLE);
        mTitleBar.getHomeButton().setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onHomeIconClick() {
        if (mEmailForm.getVisibility() == View.VISIBLE) {
            mEmailForm.setVisibility(View.GONE);
            mSelector.setVisibility(View.VISIBLE);
            mTitleBar.getHomeButton().setVisibility(View.GONE);
        }
        return super.onHomeIconClick();
    }

    @Override
    public String getTitle() {
        return getString(R.string.signin_cap);
    }

    public static Fragment newInstance() {
        LoginFragment result = new LoginFragment();
        result.isHomeButtonDisabled = true;
        return result;
    }

    @Override
    public void onInfoIconClick() {
        InfoDialog infoDialog = new InfoDialog(getActivity());
        if (mEmailForm.getVisibility() == View.VISIBLE) {
            infoDialog.setTitle(getString(R.string.info_login_email));
        } else {
            infoDialog.setTitle(getString(R.string.info_login));
        }
        infoDialog.show();
    }


}
