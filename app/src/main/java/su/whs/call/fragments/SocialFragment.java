package su.whs.call.fragments;

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

import su.whs.ErrorSender;
import su.whs.call.R;
import su.whs.call.api.ApiClient;
import su.whs.call.form.MainActivity;
import su.whs.call.models.OdnkUser;
import su.whs.call.net.ConnectionHandler;
import su.whs.call.views.EmailRegisterView;
import su.whs.call.views.IconButton;

public class SocialFragment extends BaseFragment {

    private final static String TAG = "SocailFragment";
    private final static String VK_ACCESS_TOKEN = "VK_ACCESS_TOKEN";

    private IconButton mSelectVK;
    private IconButton mSelectFacebook;
    private IconButton mSelectOdnoklassniki;

   public static  SimpleFacebook mSimpleFacebook;

    private static Permission[] facebookPermissions = new Permission[]{Permission.EMAIL,};
    private static String[] vkPermissions = new String[]{VKScope.OFFLINE};

    public void findSocialButtons(View rootView) {
        mSelectVK = (IconButton) rootView.findViewById(R.id.selectVkontakte);
        mSelectVK.setOnClickListener(mSelectVKListener);

        mSelectFacebook = (IconButton) rootView.findViewById(R.id.selectFacebook);
        mSelectFacebook.setOnClickListener(mSelectFacebookListener);

        mSelectOdnoklassniki = (IconButton) rootView.findViewById(R.id.selectOdnoklassniki);
        mSelectOdnoklassniki.setOnClickListener(mSelectOdnoklassnikiListener);

        // init VK SDK
        VKSdk.initialize(
                vkSdkListener,
                getString(R.string.vk_app_id),
                VKAccessToken.tokenFromSharedPreferences(getActivity(), VK_ACCESS_TOKEN));

        // init Facebook SDK
        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(getString(R.string.facebook_app_id))
                .setNamespace(getString(R.string.facebook_app_namespace))
                .setPermissions(facebookPermissions)
                .build();
        SimpleFacebook.setConfiguration(configuration);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("Compale", "1111122222");

        mSimpleFacebook.onActivityResult(getActivity(), requestCode, resultCode, data);
        VKUIHelper.onActivityResult(getActivity(), requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        setContentShown(true);
        mSimpleFacebook = SimpleFacebook.getInstance(getActivity());
        VKUIHelper.onResume(getActivity());
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mSimpleFacebook.getSession().closeAndClearTokenInformation();
        VKUIHelper.onDestroy(getActivity());
        super.onDestroy();
    }

    ApiClient.LoginListener socialLoginListener = new ApiClient.LoginListener() {
        @Override
        public void complete() {

            MainActivity.mTabHost.setCurrentTab(5);
            MainActivity.updateTabs();

            //MainActivity mainActivity = (MainActivity) getActivity();
            //if (mainActivity != null) mainActivity.setCurrentTabByTagHack();
        }

        @Override
        public void fail(String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void inetFail() {
            Toast.makeText(getActivity(), getString(R.string.loading_error), Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * Facebook
     */
    private OnClickListener mSelectFacebookListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            Session mSession = mSimpleFacebook.getSession();
            if (mSession != null) mSession.close();

            mSimpleFacebook.login(onLoginListener);

        }
    };

    OnLoginListener onLoginListener = new OnLoginListener() {
        @Override
        public void onLogin() {
            // change the state of the button or do whatever you want
            Request request = Request.newMeRequest(mSimpleFacebook.getSession(), new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {

                    if (mSimpleFacebook.getSession() == Session.getActiveSession()) {
                        if (user != null) {


                            String token = mSimpleFacebook.getSession().getAccessToken();

                            String user_ID = user.getId();//user id
                            String profileName = user.getName();//user's profile name
                            String avatarUrl = String.format("http://graph.facebook.com/%s/picture?type=large", user_ID);

                            ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());
                            handler.registerViaSocial("fb", token, user_ID, profileName, avatarUrl, socialLoginListener);

                        }
                    }
                }
            });


            Request.executeBatchAsync(request);
        }

        @Override
        public void onNotAcceptingPermissions(Permission.Type type) {
            // user didn't accept READ or WRITE permission
            Log.w(TAG, String.format("You didn't accept %s facebookPermissions", type.name()));
        }

        @Override
        public void onThinking() {

        }

        @Override
        public void onException(Throwable throwable) {

        }

        @Override
        public void onFail(String s) {

        }
    };

    /**
     * VK
     */
    private OnClickListener mSelectVKListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            try {
                VKSdk.authorize(vkPermissions, true, false);
            } catch (Exception ex) {
                ErrorSender.sendErrorViaEmail(ex.toString(), getActivity());
                Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_LONG).show();
            }
        }
    };

    public VKSdkListener vkSdkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {

        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {

        }

        @Override
        public void onAccessDenied(VKError authorizationError) {

        }

        @Override
        public void onReceiveNewToken(final VKAccessToken newToken) {
            final VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "contacts,photo_200"));
            request.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {

                    VKApiUserFull user = ((VKList<VKApiUserFull>) response.parsedModel).get(0);
                    ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());
                    handler.registerViaSocial("vk", newToken.accessToken, newToken.userId, user.first_name + " " + user.last_name, user.photo_200, socialLoginListener);
                }
            });
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {

        }

        @Override
        public void onRenewAccessToken(VKAccessToken token) {

        }
    };

    /**
     * Odnoklassniki
     */
    private OnClickListener mSelectOdnoklassnikiListener = new OnClickListener() {

        private static final String CLIENT_APP_ID = "1101146112";
        private static final String CLIENT_APP_KEY = "CBAJCDKCEBABABABA";
        private static final String SECRET_APP_KEY = "B709B9B8AE9A8982B70F7519";

        private String getOAuthUrl() {
            try {
                return String.format("http://www.odnoklassniki.ru/oauth/authorize?client_id=%s&scope=&response_type=token&redirect_uri=%s&layout=m",
                        CLIENT_APP_ID, //client_id
                        URLEncoder.encode("http://localhost/", "utf8")
                );
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }
        }

        public final String md5(final String s) {
            final String MD5 = "MD5";
            try {
                // Create MD5 Hash
                MessageDigest digest = MessageDigest
                        .getInstance(MD5);
                digest.update(s.getBytes());
                byte messageDigest[] = digest.digest();

                // Create Hex String
                StringBuilder hexString = new StringBuilder();
                for (byte aMessageDigest : messageDigest) {
                    String h = Integer.toHexString(0xFF & aMessageDigest);
                    while (h.length() < 2)
                        h = "0" + h;
                    hexString.append(h);
                }
                return hexString.toString();

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        public void onClick(View view) {
            final Dialog auth_dialog = new Dialog(getActivity());
            auth_dialog.setContentView(R.layout.auth_dialog);
            WebView web = (WebView) auth_dialog.findViewById(R.id.webv);
            web.getSettings().setJavaScriptEnabled(true);
            web.loadUrl(getOAuthUrl());
            web.setWebViewClient(new WebViewClient() {
                boolean authComplete = false;
                Intent resultIntent = new Intent();

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                public String getAccessToken(Uri uri) {
                    String url = uri.toString();
                    String hash = url.substring(url.indexOf("#") + 1);
                    String[] pairs = hash.split("&");
                    for (int i = 0; i < pairs.length; i++) {
                        String[] parts = pairs[i].split("=");
                        String key = parts[0];
                        String value = parts[1];
                        if (key.equals("access_token")) return value;
                    }
                    return "";
                }

                String authCode;
                String accessToken;
                String sessionSecretKey;

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (url.contains("access_token=") && authComplete != true) {
                        Uri uri = Uri.parse(url);
                        // accessToken = uri.getQueryParameter("access_token");
                        // sessionSecretKey = uri.getQueryParameter("session_secret_key");
                        final String accessToken = getAccessToken(uri);
                        auth_dialog.dismiss();

                        String rest_profile = String.format("application_key=%sfields=UID,NAME,PIC_3method=users.getCurrentUser", CLIENT_APP_KEY);
                        String md5 = md5(accessToken + SECRET_APP_KEY);

                        String sign = md5(rest_profile + md5);

                        String restRequest = String.format("http://api.odnoklassniki.ru/fb.do?application_key=%s&method=users.getCurrentUser&access_token=%s&fields=UID,NAME,PIC_3&sig=%s",
                                CLIENT_APP_KEY,
                                accessToken,
                                sign);

                        final ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());
                        handler.getCurrentUserOdnk(restRequest, new ConnectionHandler.OnOdnGetCurrentUserListener() {
                            @Override
                            public void OnOdnGetCurrentUser(OdnkUser user) {
                                handler.registerViaSocial("odnk", accessToken, user.getUid(), user.getName(), user.getAvatarUrl(), socialLoginListener);
                            }

                            @Override
                            public void onFail() {

                            }
                        });


                        /*Log.i("", "CODE : " + authCode);
                        authComplete = true;
                        resultIntent.putExtra("code", authCode);
                        MainActivity.this.setResult(Activity.RESULT_OK, resultIntent);
                        setResult(Activity.RESULT_CANCELED, resultIntent);
                        SharedPreferences.Editor edit = pref.edit();
                        edit.putString("Code", authCode);
                        edit.commit();
                        auth_dialog.dismiss();
                        new TokenGet().execute();
                        Toast.makeText(getApplicationContext(),"Authorization Code is: " +authCode, Toast.LENGTH_SHORT).show();*/
                    } else { //if(url.contains("error=access_denied")){
                        /*Log.i("", "ACCESS_DENIED_HERE");
                        resultIntent.putExtra("code", authCode);
                        authComplete = true;
                        setResult(Activity.RESULT_CANCELED, resultIntent);
                        Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                        auth_dialog.dismiss();*/
                    }
                }
            });
            auth_dialog.show();
            // auth_dialog.setTitle("Authorize Learn2Crack");
            auth_dialog.setCancelable(true);
        }
    };

    @Override
    public String getTitle() {
        return getString(R.string.empty);
    }

    //ya.ishutin@me.com
    //Pass: simplifylife

}

