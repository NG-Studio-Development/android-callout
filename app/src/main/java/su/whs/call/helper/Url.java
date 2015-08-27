package su.whs.call.helper;

import org.json.JSONException;
import org.json.JSONObject;

import su.whs.call.helper.task.Task;
import su.whs.call.helper.task.listeners.AsyncProvider;
import su.whs.call.helper.url.RestClient;
import su.whs.call.helper.url.listeners.FailListener;
import su.whs.call.helper.url.listeners.SuccessJsonListener;
import su.whs.call.helper.url.listeners.SuccessListener;

/**
 * Created by featZima on 30.08.2014.
 */
public class Url {
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String SLASH = "/";
    private static final int DEF_TIMEOUT = 10000;
    public SuccessJsonListener successJsonListener = null;
    public FailListener failListener = null;
    private String url = "";
    private String method = "";
    private String params = "";
    private boolean https;
    private String QUESTION = "?";

    private Url(boolean https, String url, String method) {
        this.url = url;
        this.method = method;
        this.https = https;
    }

    public static Url create(boolean https, String url, String method) {
        return new Url(https, url, method);
    }

    public static Url create(String url, String method) {
        return new Url(false, url, method);
    }

    public static Url create(String url, boolean https) {
        return new Url(https, url, "");
    }

    public static Url create(String url) {
        return new Url(false, url, "");
    }

    public static Url create() {
        return new Url(false, "", "");
    }

    public static Task getJsonAsync(final String url, final int timeout, final SuccessJsonListener successListener, final FailListener failListener) {
        final JSONObject[] resp = {null};
        return Task.async(new AsyncProvider() {
            @Override
            public void asyncWork() {
                resp[0] = RestClient.getJson(url, timeout);
            }

            @Override
            public void after() {
                if (resp[0] != null) {
                    if (successListener != null)
                        successListener.success(resp[0]);
                } else {
                    if (failListener != null)
                        failListener.fail();
                }
            }
        });
    }

    public static Task getAsync(final String url, final int timeout, final SuccessListener successListener, final FailListener failListener) {
        final String[] resp = {null};
        return Task.async(new AsyncProvider() {
            @Override
            public void asyncWork() {
                resp[0] = RestClient.get(url, timeout);
            }

            @Override
            public void after() {
                if (resp[0] != null) {
                    if (successListener != null)
                        successListener.success(resp[0]);
                } else {
                    if (failListener != null)
                        failListener.fail();
                }
            }
        });
    }

    public Url setMethod(String method) {
        this.method = method;
        return this;
    }

    public Url setHttps(boolean https) {
        this.https = https;
        return this;
    }

    public Url setUrl(String url) {
        this.url = url;
        return this;
    }

    public Url addParam(String param, String value) {
        if (params.length() != 0)
            params += "&";
        params += param + "=" + value;
        return this;
    }

    public Url setUrlParams(String paramStr) {
        if (params.length() != 0)
            params += "&";
        params += paramStr;
        return this;
    }

    public Url add(String param, Integer value) {
        return addParam(param, String.valueOf(value));
    }

    public String make() {
        if (!url.substring(url.length() - 1, url.length()).equals(SLASH))
            url += SLASH;
        return fix(url + method + QUESTION + params);
    }

    public String get() {
        return RestClient.get(make(), DEF_TIMEOUT);
    }

    public String get(int timeout) {
        return RestClient.get(make(), timeout);
    }

    public JSONObject getJson() {
        return RestClient.getJson(make(), DEF_TIMEOUT);
    }

    public JSONObject getJson(int timeout) throws JSONException {
        return RestClient.getJson(make(), timeout);
    }

    public void clearParams() {
        params = "";
    }

    public Task getJsonAsync() {
        return getJsonAsync(make(), DEF_TIMEOUT, successJsonListener, failListener);
    }

    public void getJsonAsync(SuccessJsonListener successListener) {
        getJsonAsync(make(), DEF_TIMEOUT, successListener, null);
    }

    public void getJsonAsync(SuccessJsonListener successListener, FailListener failListener) {
        getJsonAsync(make(), DEF_TIMEOUT, successListener, failListener);
    }

    private String fix(String url) {
        if (!url.contains(HTTP) && !url.contains(HTTPS)) {
            String tempurl = url;
            url = "";
            if (https)
                url += HTTPS + tempurl;
            else
                url += HTTP + tempurl;
        } else {
            if (url.contains(HTTP) && https) {
                url = url.replace(HTTP, HTTPS);
            }
            if (url.contains(HTTPS) && !https) {
                url = url.replace(HTTPS, HTTP);
            }
        }
        return url;
    }

    public Url setUrlParam(String param, String value) {
        url = replaceParam(url, param, value);
        return this;
    }

    public Url setUrlParam(String param, Integer value) {
        setUrlParam(param, String.valueOf(value));
        return this;
    }

    public Url setMetodParam(String param, String value) {
        method = replaceParam(method, param, value);
        return this;
    }

    public Url setMetodParam(String param, Integer value) {
        setMetodParam(param, String.valueOf(value));
        return this;
    }

    private String replaceParam(String str, String param, String value) {
        String completeParam = "{" + param + "}";
        if (!str.contains(completeParam))
            throw new ParamNotExistsException(completeParam, str);
        str = str.replace(completeParam, value);
        return str;
    }

    public Task postAsync(final String data, final SuccessListener successListener, final FailListener failListener) {
        final String[] resp = {null};
        return Task.async(new AsyncProvider() {
            @Override
            public void asyncWork() {
                resp[0] = RestClient.post(make(), data);
            }

            @Override
            public void after() {
                if (resp[0] != null) {
                    if (successListener != null)
                        successListener.success(resp[0]);
                } else {
                    if (failListener != null)
                        failListener.fail();
                }
            }
        });
    }

    public Url setListener(SuccessJsonListener successJsonListener) {
        this.successJsonListener = successJsonListener;
        return this;
    }

    public Url setListener(FailListener failListener) {
        this.failListener = failListener;
        return this;
    }

    public class ParamNotExistsException extends RuntimeException {

        public ParamNotExistsException(String param, String formattingString) {
            super("Couldn't find param \"" + param + "\" in string \"" + formattingString + "\".");
        }
    }
}