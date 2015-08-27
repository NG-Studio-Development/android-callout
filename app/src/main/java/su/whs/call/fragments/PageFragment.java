package su.whs.call.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;

import su.whs.call.Constants;
import su.whs.call.R;
import su.whs.call.adapters.CallsAdapter;
import su.whs.call.models.RegisteredYear;
import su.whs.call.net.ConnectionHandler;


/**
 * Created by Infinity-PC on 28.01.2015.
 */
public class PageFragment extends Fragment {

    private static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    public   int pageNumber;

    private String[] thumbs;

    public PageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("CCCCCCCCCCCC", "2222222222");
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pv_fragment, null);
        Log.d("CCCCCCCCCCCC", "333333");

        GridView month = (GridView) view.findViewById(R.id.gridView);

        try {

            RegisteredYear year = new RegisteredYear(ConnectionHandler.jsonYears.getJSONObject(pageNumber));
            //CallsFragment.currentYear.setText(yesrs.getJSONObject(pageNumber).getString("years"));

            Log.d("CCCCCCCCCCCC", "11111111111");

            CallsAdapter mAdapter = new CallsAdapter(getActivity(), year);
            month.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
    }



}
