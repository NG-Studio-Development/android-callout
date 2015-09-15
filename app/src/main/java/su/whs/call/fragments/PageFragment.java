package su.whs.call.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import su.whs.call.R;
import su.whs.call.adapters.CallsAdapter;
import su.whs.call.models.CallsExpert;

public class PageFragment extends Fragment {

    private static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    private static final String ARGUMENT_LIST_CALLS = "arg_list_calls";
    public  int pageNumber;
    private List<CallsExpert> mCallsList = null;
    private String[] thumbs;

    public PageFragment() {
    }

    public static PageFragment newInstance(int position, List<CallsExpert> mCallsList) {
        PageFragment pageFragment = new PageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, position);
        arguments.putSerializable(ARGUMENT_LIST_CALLS, (ArrayList<CallsExpert>)mCallsList);
        pageFragment.setArguments(arguments);

        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("CCCCCCCCCCCC", "2222222222");

        if (getArguments() != null ) {
            pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
            mCallsList = (ArrayList<CallsExpert>) getArguments().getSerializable(ARGUMENT_LIST_CALLS);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pv_fragment, null);
        Log.d("CCCCCCCCCCCC", "333333");

        GridView month = (GridView) view.findViewById(R.id.gridView);

        //try {

            //RegisteredYear year = new RegisteredYear(ConnectionHandler.jsonYears.getJSONObject(pageNumber));
            //CallsFragment.currentYear.setText(yesrs.getJSONObject(pageNumber).getString("years"));

            Log.d("CCCCCCCCCCCC", "11111111111");

            //CallsAdapter mAdapter = new CallsAdapter(getActivity(), CallsExpert.createListDEBUG());
            CallsAdapter mAdapter = new CallsAdapter(getActivity(), mCallsList);
            month.setAdapter(mAdapter);
        //} catch (JSONException e) {            e.printStackTrace();}
        //catch (ParseException e) {
            //e.printStackTrace();
        //}

        return view;
    }



}
