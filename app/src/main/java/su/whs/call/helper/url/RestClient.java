package su.whs.call.helper.url;


import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import su.whs.call.helper.url.ssl.VerifyAllSSLSocketFactory;


public class RestClient {

    public static String get(String url, int timeout) {
        try {
            AbstractHttpClient client = getClient(timeout);
            HttpGet httpGetRequest = new HttpGet(url);
            HttpResponse httpResponse = client.execute(httpGetRequest);
            return makeString(httpResponse.getEntity().getContent());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getJson(String url, int timeout) {
        String resp = get(url, timeout);
        if (resp == null)
            return null;
        try {
            return new JSONObject(resp);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static JSONObject post(String url, int timeout) {
        try {
            AbstractHttpClient client = getClient(timeout);
            HttpPost httpPostRequest = new HttpPost(url);
            HttpResponse httpResponse = client.execute(httpPostRequest);
            return new JSONObject(makeString(httpResponse.getEntity().getContent()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static String makeString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuilder builder = new StringBuilder();
        String aux = "";
        while ((aux = reader.readLine()) != null) {
            builder.append(aux);
        }
        return builder.toString();
    }

    public static String post(String url, String data) {
        try {
            AbstractHttpClient httpclient = getClient(5000);
            HttpPost httpPostRequest = new HttpPost(url);

            StringEntity se;
            se = new StringEntity(data);

            // Set HTTP parameters
            httpPostRequest.setEntity(se);
            httpPostRequest.setHeader("Accept", "application/json");
            httpPostRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPostRequest.setHeader("Accept-Encoding", "gzip"); // only set this parameter if you would like to use gzip compression

            HttpResponse response = httpclient.execute(httpPostRequest);

            // Get hold of the response entity (-> the data):
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Read the content stream
                InputStream instream = entity.getContent();
                Header contentEncoding = response.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    instream = new GZIPInputStream(instream);
                }

                // convert content stream to a String
                String resultString = convertStreamToString(instream);
                instream.close();
                return resultString;
            }
            if (response.getStatusLine().getStatusCode() != 404)
                return " ";
        } catch (Exception e) {
            // More about HTTP exception handling in another tutorial.
            // For now we just print the stack trace.
            e.printStackTrace();
        }
        return null;
    }

    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 *
		 * (c) public domain: http://senior.ceng.metu.edu.tr/2009/praeda/2009/01/11/a-simple-restful-client-at-android/
		 */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static AbstractHttpClient getClient(final int timeout) {
        final AbstractHttpClient client = new DefaultHttpClient() {
            @Override
            protected ClientConnectionManager createClientConnectionManager() {
                SchemeRegistry registry = new SchemeRegistry();
                registry.register(
                        new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                registry.register(
                        new Scheme("https", VerifyAllSSLSocketFactory.get(), 443));
                HttpParams params = getParams();
                HttpConnectionParams.setConnectionTimeout(params, timeout);
                HttpConnectionParams.setSoTimeout(params, timeout);
                //HttpProtocolParams.setUserAgent(params, getUserAgent(HttpProtocolParams.getUserAgent(params)));
                return new ThreadSafeClientConnManager(params, registry);
            }
        };
        return client;
    }

}
