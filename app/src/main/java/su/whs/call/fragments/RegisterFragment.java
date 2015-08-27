package su.whs.call.fragments;

import su.whs.call.Constants;
import su.whs.call.R;
import su.whs.call.api.ApiClient;
import su.whs.call.dialog.InfoDialog;
import su.whs.call.form.MainActivity;
import su.whs.call.models.OdnkUser;
import su.whs.call.net.ConnectionHandler;
import su.whs.call.register.User;
import su.whs.call.views.EmailRegisterView;
import su.whs.call.views.IconButton;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKList;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterFragment extends SocialFragment {

    private final static String TAG = "RegisterFragment";

    private IconButton selectEmail;
    EmailRegisterView mEmailForm;
    View mSelector;

    public RegisterFragment() {
        isHomeButtonDisabled = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.register_tab_fragment, container, false);

        mSelector = view.findViewById(R.id.registerSelector);
        mEmailForm = (EmailRegisterView) view.findViewById(R.id.emailRegister);
        mEmailForm.setParentFragment(this);
        selectEmail = (IconButton) view.findViewById(R.id.selectEmail);
        selectEmail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showEmailForm();
            }
        });

        User user = User.create(getActivity());
        if (user.isExecutor()) {
            MainActivity.mTabHost.setCurrentTab(1);
        }

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
    public void onInfoIconClick() {
        InfoDialog infoDialog = new InfoDialog(getActivity());
        if (mEmailForm.getVisibility() == View.VISIBLE) {
            infoDialog.setTitle(getString(R.string.info_register_email));
        } else {
            infoDialog.setTitle(getString(R.string.info_register));
        }
        infoDialog.show();
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
        return getString(R.string.register);
    }

}
