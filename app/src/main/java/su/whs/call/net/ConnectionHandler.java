package su.whs.call.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import su.whs.call.Constants;
import su.whs.call.R;
import su.whs.call.api.ApiClient;
import su.whs.call.models.CallItem;
import su.whs.call.models.ExecutorSubcategory;
import su.whs.call.models.OdnkUser;
import su.whs.call.models.RecentCall;
import su.whs.call.models.RegisteredYear;
import su.whs.call.models.Review;
import su.whs.call.models.UserExtra;
import su.whs.call.register.User;

public class ConnectionHandler {

    public static final int SOCIAL_VK = 1;

    private static final Integer HTTP_OK = 200;
    private static final String TAG = "ConnectionHandler";
    private static ConnectionHandler mInstance = null;

    private AQuery mQuery = null;
    private Context mContext;

    public static JSONArray jsonYears = new JSONArray();

    public interface OnRegistrationListener {
        void onRegisterResult(JSONObject json, AjaxStatus status);
    }

    public interface OnLoginListener {
        void onLoginResult(JSONObject json, AjaxStatus status);
    }

    public interface OnDistanceResponseListener {
        void onDistanceResponse(JSONObject json);
    }

    public interface OnReviewsListener {
        void onReviewsResponse(List<Review> reviews);
    }

    public interface OnCallsListener {
        void onCallsResponse(RegisteredYear year);
    }

    public interface OnClientCallsListener {
        void onCallsResponse(List<RecentCall> calls);
    }

    public interface OnPostReviewListener {
        void onSuccessPostReview();

        void onFailPostReview();
    }

    public interface OnOdnGetCurrentUserListener {
        void OnOdnGetCurrentUser(OdnkUser user);

        void onFail();
    }

    public interface OnExecutorCategoriesListener {
        void onCategoriesResponse(ArrayList<ExecutorSubcategory> subcategories);
    }

    public interface OnUsersInfoListener {
        void onUsersInfoReady(List<UserExtra> list);

        void onFail();
    }

    public interface OnUserInfoListener {
        void onUserInfoReady(List<UserExtra> allUsers, UserExtra userInfo);

        void onFail();
    }

    private ConnectionHandler(Context context) {
        mQuery = new AQuery(context);
        mContext = context;
    }

    public synchronized static ConnectionHandler getInstance(Context context) {
        if (mInstance == null)
            mInstance = new ConnectionHandler(context);
        return mInstance;
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity =
                (ConnectivityManager) mContext.getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public void register(String email, String password, String passwordConfirmation, String name, final ApiClient.LoginListener listener) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("email", email);
        params.put("password", password);
        params.put("password_confirmation", passwordConfirmation);
        params.put("name", name);

        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String uri, JSONObject response, AjaxStatus status) {
                Log.v(TAG, "register response: " + (response == null ? "null" : response.toString()));
                if (response != null) {
                    try {
                        int error_code = response.getInt("error_code");
                        if (error_code == 0) {
                            User user = User.create(mContext);
                            if (user.isLoggedIn()) user.logout();
                            user.setIsLoggedIn(true)
                                    .setUserName(response.getJSONObject("data").getString("name"))
                                    .setExecutor(result.getJSONObject("data").has("status"))
                                    .setToken(response.getJSONObject("data").getString("session_token"));
                            listener.complete();
                            return;
                        } else {
                            JSONArray messages = response.getJSONArray("message");
                            String firstMessage = messages.getString(0);
                            listener.fail(firstMessage);
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.inetFail();
                        return;
                    }
                } else {
                    listener.inetFail();
                    return;
                }
            }
        };
        cb.header("Accept", "application/json");

        mQuery.ajax(Constants.API + "/users", params, JSONObject.class, cb);
    }

    public void login(String email, String password, final ApiClient.LoginListener loginListener) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("email", email);
        params.put("password", password);

        mQuery.ajax(Constants.API + "/api/tokens.json", params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String uri, JSONObject response, AjaxStatus status) {
                Log.v(TAG, "login response: " + (response == null ? "null" : response.toString()));
                if (status.getCode() != HTTP_OK) {
                    try {
                        JSONObject result = new JSONObject(status.getError());
                        loginListener.fail(result.getString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        loginListener.fail("Ошибка");
                    }
                } else {
                    try {
                        if (response.getBoolean("result")) {
                            JSONObject result = response.getJSONObject("data");

                            User.create(mContext)
                                    .setIsLoggedIn(true)
                                    .setUserName(result.getJSONObject("data").getString("name"))
                                    .setExecutor(result.getJSONObject("data").has("status"))
                                    .setToken(result.getString("token"));
                            loginListener.complete();


                        } else {
                            loginListener.fail(result.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        loginListener.fail("Ошибка");
                    }
                }
            }
        });
    }

    public void queryUsers(final OnUsersInfoListener listener) {
        mQuery.ajax(Constants.API + "/api/user/info", JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject response, AjaxStatus status) {
                if (response != null) {
                    try {
                        List<UserExtra> usersInfoList = new ArrayList<UserExtra>();
                        JSONArray jsonUsersInfo = response.getJSONArray("data");
                        for (int i = 0; i < jsonUsersInfo.length(); i++) {
                            UserExtra userInfo = new UserExtra(jsonUsersInfo.getJSONObject(i));


                            usersInfoList.add(userInfo);
                        }
                        listener.onUsersInfoReady(usersInfoList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onFail();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        listener.onFail();

                    }
                }
            }
        });
    }

    public void queryUser(final String userToken, final OnUserInfoListener listener) {
        String query = Constants.API + "/api/user/info/" + userToken;
        Log.e("test", "query = " + query );
        mQuery.ajax(query, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject response, AjaxStatus status) {
                if (response != null) {
                    try {

                        JSONObject result = response.getJSONObject("data");
                        JSONObject jsonUserInfo = result.getJSONObject("user_info");
                        final int userId = jsonUserInfo.getInt("id");

                        queryUsers(new OnUsersInfoListener() {
                            @Override
                            public void onUsersInfoReady(List<UserExtra> list) {
                                for (UserExtra userExtra : list) {
                                    if (userExtra.getUserInfo().getId() == userId) {
                                        listener.onUserInfoReady(list, userExtra);
                                        return;
                                    }
                                }
                                listener.onFail();
                            }

                            @Override
                            public void onFail() {
                                listener.onFail();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onFail();
                    }
                } else {
                    listener.onFail();
                }
            }
        });
    }

    public void queryDistance(Location loc, final OnDistanceResponseListener l) {
        String url = "/api/distance/" + String.valueOf(loc.getLatitude()) + "/" + String.valueOf(loc.getLongitude());
        mQuery.ajax(Constants.API + url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (json != null) {
                    l.onDistanceResponse(json);
                }
            }
        });
    }

    public void registerViaSocial(String network, String token, String userId, String userName, String avatarUrl, final ApiClient.LoginListener listener) {
        // validate social network name

        Log.d("SOCIAL", "response = " + network);

        if (network == null
                || !network.equals("fb")
                && !network.equals("odnk")
                && !network.equals("vk")) {
            listener.fail(mContext.getString(R.string.unsupported_social_network));
            return;
        }
        // login request
        try {
            //String url = String.format("%s/api/auth/%s?token=%s&name=%s&uid=%s&avatar=%s",
            JSONObject jsonAvatar = new JSONObject();
            jsonAvatar.put("url", avatarUrl);
            String url = String.format("%s/api/auth/%s/%s/%s/%s/%s",
                    Constants.API,
                    network,
                    token,
                    URLEncoder.encode(userName, "utf8"),
                    userId,
                    //avatarUrl == null ? "" : URLEncoder.encode(jsonAvatar.toString(), "utf8"));
                    //avatarUrl == null ? "" : URLEncoder.encode(avatarUrl, "utf8"));
                    avatarUrl == null ? "" : URLEncoder.encode("ss", "utf8"));
                    //jsonAvatar.toString());

            url.isEmpty();
            mQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject response, AjaxStatus status) {


                    if (response != null) {
                        try {
                            int error_code = response.getInt("error_code");
                            if (error_code == 0) {
                                User user = User.create(mContext);
                                if (user.isLoggedIn()) user.logout();
                                user.setIsLoggedIn(true)
                                        .setUserName(response.getJSONObject("data").getString("name"))
                                        .setExecutor(result.getJSONObject("data").has("status"))
                                        .setToken(response.getJSONObject("data").getString("token"));
                                listener.complete();
                                return;
                            } else {
                                JSONArray messages = response.getJSONArray("message");
                                String firstMessage = messages.getString(0);
                                listener.fail(firstMessage);
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.inetFail();
                            return;
                        }
                    } else {
                        listener.inetFail();
                        return;
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            listener.fail("UnsupportedEncodingException");
            e.printStackTrace();
        } catch (JSONException e) {
            listener.fail("JSONException");
            e.printStackTrace();
        }
    }

    public void getCurrentUserOdnk(String url, final OnOdnGetCurrentUserListener listener) {
        mQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject response, AjaxStatus status) {
                try {
                    if (status.getCode() == 200) {
                        OdnkUser user = new OdnkUser(response);
                        listener.OnOdnGetCurrentUser(user);
                    } else {
                        listener.onFail();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void postCall(String token, int userId, int subCategoryId) {
        String url = String.format("%s/api/calls/inc/%s/%d/%d/?date=%s",
                Constants.API,
                token,
                userId, subCategoryId,
                CallItem.simple121Format.format(new Date())
        );
        mQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                Log.e("test", json.toString());
            }
        });
    }

    public void postAvatar(String token, String imageType, String imageBase64) {
        String url = null;
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            /*url = String.format("%s/api/set/avatar/%s?type=%s&avatar=%s",
                    Constants.API,
                    token,
                    imageType,
                    URLEncoder.encode(imageBase64, "utf8"));*/

            url = String.format("%s/api/set/avatar", Constants.API);

            url.isEmpty();

            params.put("token", token);
            params.put("type",imageType);
            params.put("avatar", URLEncoder.encode(imageBase64, "utf8"));

        } catch (Exception e) {
            e.printStackTrace();
        }




        mQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (json != null && json.has("data")) {
                    Log.d("CONNECTION_HANDLER","Post image json: "+json.toString());
                } else {
                    Log.d("CONNECTION_HANDLER","Post image json is null ");
                }
            }
        });
    }

    public void removeAvatar(String token) {
        String url = null;
        try {
            url = String.format("%s/api/delete/avatar/%s",
                    Constants.API,
                    token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (json != null && json.has("data")) {

                }
            }
        });
    }

    public void queryReviews(int userId, int subcategoryId, final OnReviewsListener listener) {
        String url = String.format("%s/api/rate/%d/%d",
                Constants.API,
                userId,
                subcategoryId);


        mQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (json != null && json.has("data")) {
                    JSONArray jsonReviews = json.optJSONArray("data");

                    List<Review> reviews = new ArrayList<Review>(jsonReviews.length());
                    for (int i = 0; i < jsonReviews.length(); i++) {
                        try {
                            reviews.add(new Review(jsonReviews.getJSONObject(i)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (reviews.size() > 0) {
                        listener.onReviewsResponse(reviews);
                    }
                }
            }
        });
    }

    /*public void queryExecutiveCalls(String userToken, final OnCallsListener listener) {
        String url = String.format("%s/api/calls/get/%s", Constants.API, userToken);
        Log.e("test", url);
        mQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (json != null && json.has("data")) {
                    try {
                        jsonYears = json.getJSONArray("data");
                        if (jsonYears.length() > 0) {
                            RegisteredYear year = new RegisteredYear(jsonYears.getJSONObject(jsonYears.length() - 1));

                            listener.onCallsResponse(year);
                            return;
                        }
                    } catch (Exception e) {
                        Log.e("Test", e.toString());
                    }
                }
            }
        });
    } */




    public void queryClientCalls(String userToken, final OnClientCallsListener listener) {
        String url = String.format("%s/api/calls/get/%s", Constants.API, userToken);
        Log.e("test", url);
        mQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {



                if (json != null && json.has("data")) {
                    try {
                        JSONArray data = json.getJSONArray("data");
                        JSONArray calls = data.getJSONObject(0).getJSONArray("calls");
                        ArrayList<RecentCall> recentCalls = new ArrayList<RecentCall>();
                        for (int i = 0; i < calls.length(); i++) {
                            recentCalls.add(new RecentCall(calls.getJSONObject(i)));
                        }
                        listener.onCallsResponse(recentCalls);
                    } catch (Exception e) {
                        Log.e("Test", e.toString());
                    }
                }
            }
        });
    }

    public void queryExecutorCategories(String userToken, final OnExecutorCategoriesListener listener) {
        String url = String.format("%s/api/get/reviews/%s",
                Constants.API,
                userToken);

        Log.d("232324232222", url);

        mQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                Log.d("QUERY_CATEGORIES", "Json complete");
                if (json != null && json.has("data")) {
                    try {
                        ArrayList<ExecutorSubcategory> subcategories = new ArrayList<ExecutorSubcategory>();
                        JSONArray jsonCategories = json.getJSONArray("data");
                        for (int i = 0; i < jsonCategories.length(); i++) {
                            ExecutorSubcategory subcategory = new ExecutorSubcategory(jsonCategories.getJSONObject(i));
                            subcategories.add(subcategory);
                        }
                        listener.onCategoriesResponse(subcategories);
                    }
                    // catch (ParseException e) { }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("QUERY_CATEGORIES", "Json is null");
                }
            }
        });
    }

    /**
     * Post new user review to server
     * <p/>
     * API: /api/review/user_id/sub_category_id/:token?description=sfsdfsd&name=ddssdfsd&rate=20
     */
    public void postReview(int userId, int subCategoryId, String userToken, Review review, final OnPostReviewListener listener) {
        try {
            String userName = review.getUserName();
            if (userName == null) {
                userName = "unknown";
            }

            String url = Constants.API + "/api/review/" + userId + "/" + subCategoryId + "/" + userToken + "?description=" + URLEncoder.encode(review.getDescription(), "utf-8") + "&name=" + URLEncoder.encode(userName, "utf-8") + "&rate=" + review.getRate();

            mQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    if (json != null && json.has("error_code") && json.optInt("error_code") == 0) {
                        listener.onSuccessPostReview();
                    } else {
                        listener.onFailPostReview();
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            listener.onFailPostReview();
        }
    }

    public void postStatus(String userToken, boolean userIsBusy) {
        String url = String.format("%s/api/user/%s/%s",
                Constants.API,
                userToken,
                userIsBusy ? "true" : "false");


        mQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
        @Override
        public void callback(String url, JSONObject json, AjaxStatus status) {
            try {
                Log.d("2222222 = ", "111 = " + json.toString(4));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (json != null && json.has("data") && json.optString("data").equals("Success")) {

            }
        }
    });
}

    public interface OnImageListener {
        void onImageLoaded(Bitmap bitmap);

    }

    public void queryImage(String url, final OnImageListener l) {
        mQuery.ajax(Constants.API + url, Bitmap.class, new AjaxCallback<Bitmap>() {
            @Override
            public void callback(String uri, Bitmap bitmap, AjaxStatus status) {
                if (l != null && bitmap != null) {
                    l.onImageLoaded(bitmap);
                }
            }
        });

    }
}
