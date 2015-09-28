package su.whs.call.net;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class AddressAutocomplete {
    private static final String TAG = "Geocoder.Autocomplete";
    private static final String GOOGLE_GEOCODE = "http://maps.googleapis.com/maps/api/geocode/json?address=%s&sensor=true&components=country:ru";
    private static AddressAutocomplete mInstance = null;
    private Context mContext = null;
    private AQuery mQuery = null;
    private String mLastQuery = null;
    private AutocompleteListener mListener = null;

    public class AddressInfo {
        private Location mLocation;
        private String mAddress;

        public AddressInfo(String address, Location location) {
            mLocation = location;
            mAddress = address;
        }

        public String address() {
            return mAddress;
            //return "HC ADDRESS";
        }

        public Location location() {
            return mLocation;
        }
    }

    private AjaxCallback<JSONObject> mCallback = new AjaxCallback<JSONObject>() {
        @Override
        public void callback(String url, JSONObject json, AjaxStatus status) {
            Log.e("test", "ajax url - " + url);
            if (!currentQuery(url)) // if not - we have new request
                return;
            List<AddressInfo> result = new ArrayList<AddressInfo>();
            Log.e("test", json.toString());
            if (json != null && json.has("results")) {
                JSONArray components;
                try {
                    components = json.getJSONArray("results");
                } catch (JSONException e) {
                    Log.v(TAG, "json exception");
                    return;
                }

                for (int i = 0; i < components.length(); i++) {
                    JSONObject ac;
                    try {
                        ac = components.getJSONObject(i);
                    } catch (JSONException e) {
                        Log.e("test", e.toString());
                        continue;
                    }
                    Location loc = null;
                    if (ac.has("geometry")) {
                        JSONObject geometry = null;
                        try {
                            geometry = ac.getJSONObject("geometry");
                        } catch (JSONException e) {
                        }
                        if (geometry != null && geometry.has("location")) {
                            JSONObject location = null;
                            try {
                                location = geometry.getJSONObject("location");
                            } catch (JSONException e) {

                            }
                            if (location != null) {
                                double lng = location.optDouble("lng");
                                double lat = location.optDouble("lat");
                                loc = new Location("");
                                loc.setLongitude(lng);
                                loc.setLatitude(lat);
                            }
                        }
                    }

                    if (ac.has("formatted_address")) {
                        try {
                            result.add(new AddressInfo(ac.getString("formatted_address"), loc));
                            //result.add(new AddressInfo(ac.getString("formatted_address"), loc));
                        } catch (Exception ex) {
                            Log.e("test", ex.toString());
                        }
                    }

                    if (ac.has("address_components")) {
                        JSONArray aci = null;
                        try {
                            aci = ac.getJSONArray("address_components");
                        } catch (JSONException e) {
                            Log.e("test", e.toString());
                        }
                        if (aci != null) {
                            for (int j = 0; j < aci.length(); j++) {
                                try {
                                    JSONObject comp = aci.getJSONObject(j);
                                    if (comp.has("types") && checkForRoute(comp.getJSONArray("types"))) {
                                        String shortName = null;
                                        if (comp.has("short_name")) {
                                            boolean unique = true;
                                            shortName = comp.optString("short_name");
                                            if (loc != null && shortName != null) {
                                                for (AddressInfo ai : result) {
                                                    String aiaddress = ai.address();
                                                    if (shortName.equals(aiaddress))
                                                        unique = false;
                                                }
                                                if (unique)
                                                    result.add(new AddressInfo(shortName, loc));

                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    Log.e("test", e.toString());
                                    continue;
                                }

                            }
                        }

                    }
                }
                if (currentQuery(url))
                    callListener(result);
            }
        }
    };

    private boolean checkForRoute(JSONArray types) {
        for (int j = 0; j < types.length(); j++) {
            String t = types.optString(j);
            if ("route".equals(t))
                return true;
        }
        return false;
    }

    private synchronized void setQuery(String url, AutocompleteListener l) {
        mLastQuery = url;
        mListener = l;
    }

    private synchronized void callListener(List<AddressInfo> result) {
        if (mListener != null) {
            mListener.onUpdated(result);
        }
        mListener = null;
        mLastQuery = null;
    }

    private synchronized boolean currentQuery(String url) {
        return (url != null && url.equals(mLastQuery));
    }

    public interface AutocompleteListener {
        public void onUpdated(List<AddressInfo> results);
    }

    private AddressAutocomplete(Context context) {
        mContext = context;
        mQuery = new AQuery(mContext);
    }

    public static AddressAutocomplete getInstance(Context context) {
        if (mInstance == null)
            mInstance = new AddressAutocomplete(context);
        return mInstance;
    }

    public synchronized void requestAsync(String area, String query, AutocompleteListener l) {
        try {
            query = URLEncoder.encode(query, "utf-8");
            String aq = String.format(GOOGLE_GEOCODE, query);
            Log.e("test", aq);
            setQuery(aq, l);
            mQuery.ajax(aq, JSONObject.class, mCallback.header("Accept-Language", "ru"));
        } catch (Exception ex) {
            Log.e("test", ex.toString());
        }
    }
}
