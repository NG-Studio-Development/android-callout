package su.whs.call.fragments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.map.MapEvent;
import ru.yandex.yandexmapkit.map.OnMapListener;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import ru.yandex.yandexmapkit.utils.ScreenPoint;
import su.whs.call.AddressInfoAdapter;
import su.whs.call.R;
import su.whs.call.dialog.MapInfoDialog;
import su.whs.call.net.AddressAutocomplete;
import su.whs.call.net.AddressAutocomplete.AddressInfo;
import su.whs.call.net.AddressAutocomplete.AutocompleteListener;
import su.whs.call.views.MapControlLayer;
import su.whs.call.views.SearchPanel;
import su.whs.call.views.SearchPanel.SearchPanelListener;

public class ChooseLocationFragment extends BaseSearchTabFragment implements SearchPanelListener {
    private static final String TAG = "ChooseLocation";
    private MapControlLayer mControls = null;
    private MapView mMap = null;
    private MapController mMapController = null;
    private GeoPoint mMarkerGeoPoint = null;
    private ScreenPoint mMarkerScreenPoint = null;
    private SearchPanel mSearchPanel = null;
    private String mCity = "Москва";
    HttpClient mHttpClient;

    public class ClickOverlay extends Overlay {
        public ClickOverlay(MapController arg0) {
            super(arg0);
        }

        @Override
        public boolean onSingleTapUp(float X, float Y) {
            onClick(X, Y);
            return true;
        }

        @Override
        public int compareTo(Object o) {
            return 0;
        }
    }

    public static ChooseLocationFragment newInstance() {
        ChooseLocationFragment fragment = new ChooseLocationFragment();
        fragment.isShadowBackground = true;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSearchPanel = new SearchPanel(getActivity(), null);
        mSearchPanel.setListener(this);
        mSearchPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });
        mHttpClient = new DefaultHttpClient();
        if (getTitleBar() != null)
            getTitleBar().setCustomView(mSearchPanel);
        View v = inflater.inflate(R.layout.map_view, container, false);
        mControls = (MapControlLayer) v.findViewById(R.id.mapControls);
        mMap = (MapView) v.findViewById(R.id.map);
        mMapController = mMap.getMapController();
        mMapController.getOverlayManager().addOverlay(new ClickOverlay(mMapController));
        new ClickOverlay(mMapController);


        final Location loc = ((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE)).getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (loc != null) {

            /** Point of start position */
            GeoPoint gp = new GeoPoint(55.7501525,37.6242858);

            mMapController.setPositionNoAnimationTo(gp);
            (new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (addresses != null && addresses.size() > 0)
                        return addresses.get(0).getLocality();
                    return null;
                }

                @Override
                protected void onPostExecute(String result) {
                    if (result != null)
                        mCity = result;
                }
            }).execute();
        }
        initControls();
        mContentView = v;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initControls() {
        mControls.setOnMapClickListener(new MapControlLayer.OnMapClickListener() {
                                            @Override
                                            public void onClick(float X, float Y) {
                                                Location location = ((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE)).getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                                if (location != null) {
                                                    GeoPoint gp = new GeoPoint(location.getLatitude(), location.getLongitude());
                                                    mMapController.setPositionAnimationTo(gp);
                                                    ScreenPoint sp = mMapController.getScreenPoint(gp);
                                                    ChooseLocationFragment.this.onClick(sp.getX(), sp.getY());
                                                }
                                            }
                                        }
        );
        mMapController.addMapListener(new OnMapListener() {
                                          @Override
                                          public void onMapActionEvent(MapEvent arg0) {
                                              switch (arg0.getMsg()) {
                                                  case MapEvent.MSG_SCROLL_BEGIN:
                                                  case MapEvent.MSG_ZOOM_BEGIN:
                                                  case MapEvent.MSG_SCALE_BEGIN:
                                                      savePoint();
                                                      break;
                                                  case MapEvent.MSG_SCROLL_END:
                                                  case MapEvent.MSG_ZOOM_END:
                                                  case MapEvent.MSG_SCALE_END:
                                                      restorePoint();
                                                      break;
                                                  default:
                                                      restorePoint();
                                              }
                                          }
                                      }

        );
        // hide keyboard by touch on search result
        mSearchPanel.getSearchResultsList().setOnTouchListener(new View.OnTouchListener() {
                                                                   @Override
                                                                   public boolean onTouch(View view, MotionEvent motionEvent) {
                                                                       EditText editSearch = (EditText) getActivity().findViewById(R.id.editSearch);
                                                                       if (editSearch != null) {
                                                                           InputMethodManager imm = (InputMethodManager) editSearch.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                                                           imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
                                                                       }
                                                                       return false;
                                                                   }
                                                               }

        );

    }

    @Override
    public void onResume() {
        setContentShown(true);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        super.onResume();
    }

    @Override
    public void onPause() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onPause();
    }

    @Override
    public void onActivate() {
        super.onActivate();
    }

    @Override
    public View getCustomView() {
        return mSearchPanel;
    }

    @Override
    public boolean onHomeIconClick() {
        openFragment(SearchChooseLocationPromptFragment.newInstance());
        return true;
    }

    @Override
    public String getTitle() {
        return getString(R.string.search).toUpperCase();
    }

    @Override
    public boolean isCustomViewVisible() {
        return true;
    }

    public void onClick(float X, float Y) {
        mControls.setMarkerPosition(X, Y);
        mMarkerScreenPoint = new ScreenPoint(X, Y);
        mMarkerGeoPoint = mMapController.getGeoPoint(mMarkerScreenPoint);
        (new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String url = String.format("http://nominatim.openstreetmap.org/reverse?format=json&lat=%s&lon=%s", mMarkerGeoPoint.getLat(), mMarkerGeoPoint.getLon());
                try {
                    return EntityUtils.toString(mHttpClient.execute(new HttpGet(url)).getEntity());
                } catch (Exception ex) {
                    Log.e(ChooseLocationFragment.class.getSimpleName(), ex.toString());
                    return null;
                }

                /*Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(mMarkerGeoPoint.getLat(), mMarkerGeoPoint.getLon(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses != null && addresses.size() > 0)
                    return addresses.get(0);
                return null;
                */
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        JSONObject jsonResult = new JSONObject(result);
                        mCity = jsonResult.getJSONObject("address").optString("city");
                        //mCity = result.getLocality();

                        EditText editSearch = (EditText) getActivity().findViewById(R.id.editSearch);
                        if (editSearch != null) {
                        /*
                        String addressText = String.format(
                                "%s, %s, %s",
                                // If there's a street address, add it
                                result.getMaxAddressLineIndex() > 0 ?
                                        result.getAddressLine(0) : "",
                                // Locality is usually a city
                                result.getLocality() == null ? "" : result.getLocality(),
                                // The country of the address
                                result.getCountryName());
                                */
                            editSearch.setText(jsonResult.getString("display_name"));
                        }
                    } catch (Exception ex) {
                        Log.e(ChooseLocationFragment.class.getSimpleName(), ex.toString());
                    }
                }
            }
        }).execute();
    }

    private void   savePoint() {

    }

    private void restorePoint() {
        if (mControls.isMarkerVisible()) {
            mMarkerScreenPoint = mMapController.getScreenPoint(mMarkerGeoPoint);
            mControls.setMarkerPosition(mMarkerScreenPoint.getX(), mMarkerScreenPoint.getY());
        }
    }

    @Override
    public void onQueryChanged(String query) {
        Log.e("test", query);
        if (query == null || query.length() < 3)
            return;
        AddressAutocomplete aac = AddressAutocomplete.getInstance(getActivity());
        aac.requestAsync(mCity, query, new AutocompleteListener() {
            @Override
            public void onUpdated(final List<AddressInfo> results) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showSearchResults(results);
                    }
                });
            }
        });
    }

    @Override
    public void onFindButtonClicked() {
        if (mControls.isMarkerVisible()) {
            openFragment(CategoriesFragment.newInstance(mMarkerGeoPoint));
        } else {
            Toast.makeText(getActivity(), getString(R.string.choose_location_before), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSearchResultClicked(int position) {
        AddressInfo info = (AddressInfo) (mSearchPanel.getAdapter().getItem(position));
        GeoPoint gp = new GeoPoint(info.location().getLatitude(), info.location().getLongitude());
        mMapController.setPositionNoAnimationTo(gp);
        mMarkerGeoPoint = gp;
        ScreenPoint sp = mMapController.getScreenPoint(gp);
        mControls.setMarkerPosition(sp.getX(), sp.getY());
        mSearchPanel.setQuery(info.address());
        mSearchPanel.foldResults();
    }

    private void showSearchResults(List<AddressInfo> data) {
        ArrayAdapter<AddressInfo> adapter = new AddressInfoAdapter(getActivity(), R.layout.address_info_list_item, data);
        mSearchPanel.setAdapter(adapter);
    }

    @Override
    public void onInfoIconClick() {
        new MapInfoDialog(getActivity()).show();
    }

}
