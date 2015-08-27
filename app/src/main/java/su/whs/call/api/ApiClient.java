package su.whs.call.api;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.EventListener;

import su.whs.call.Constants;
import su.whs.call.helper.Strings;
import su.whs.call.helper.Url;
import su.whs.call.helper.url.listeners.FailListener;
import su.whs.call.helper.url.listeners.SuccessListener;
import su.whs.call.register.User;

/**
 * Created by featZima on 30.08.2014.
 */
public class ApiClient {
    public static final String API_URL = Constants.API;
    private static final String TOKENS = "api/tokens.json";

    public static void login(final Context context,String email, String password, final LoginListener loginListener) {
        //user[email]=g21234dfsdf@ggg.com&user[password]=11111111&user[password_confirmation]=11111111&user[name]=op

        //String params = "email={email}&password={password}&password_confirmation={password_confirmation}&name={name}";

        String params = "email={email}&password={password}";
        params = Strings.create(params)
                .setParam("email", email)
                .setParam("password", password)
                .make();


        Url.create(API_URL, TOKENS).postAsync(params, new SuccessListener() {
            @Override
            public void success(String response) {
                try {
                    JSONObject resp = new JSONObject(response);
                    if (resp.getBoolean("result")) {
                        User.create(context)
                                .setIsLoggedIn(true)
                                .setUserName(resp.getJSONObject("data").getString("name"));
                        loginListener.complete();
                    } else {
                        loginListener.fail(resp.getString("message"));
                    }
                } catch (JSONException e) {
                    loginListener.fail("Ошибка");
                }

            }
        }, new FailListener() {
            @Override
            public void fail() {

                loginListener.inetFail();
            }
        });

    }

    public interface LoginListener extends EventListener {
        void complete();

        void fail(String message);

        void inetFail();
    }
}
