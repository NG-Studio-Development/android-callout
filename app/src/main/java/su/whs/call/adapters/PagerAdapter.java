package su.whs.call.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import su.whs.call.fragments.PageFragment;
import su.whs.call.models.CallsExpert;


/**
 * Created by Infinity-PC on 27.01.2015.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    private int countPages = 2;
    private List<Integer> mListYear;
    private List<CallsExpert> mCallsList;

    private static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    private static final String ARGUMENT_LIST_CALLS = "arg_page_number";

    //private JSONArray json;

    /*public PagerAdapter(FragmentManager fm, JSONArray array) {
        super(fm);
        //this.json = array;
    }*/

    public PagerAdapter(FragmentManager fm, List<CallsExpert> callsList) {
        super(fm);
        mListYear = CallsExpert.getListOfYears(callsList);
        mCallsList = callsList;
        //this.json = array;

    }

    @Override
    public Fragment getItem(int position) {
        /*PageFragment pageFragment = new PageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, position);
        arguments.putSerializable(ARGUMENT_LIST_CALLS, (ArrayList<CallsExpert>)mCallsList);
        pageFragment.setArguments(arguments);*/

        return PageFragment.newInstance(position, mCallsList);
    }

    public int getYear(int position) {
        return mListYear.get(position);
    }

    /*public JSONObject getJSONObject(int position) {

        try {
            return json.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }*/



    @Override
    public int getCount() {
        return getCountPages();
    }

    public int getCountPages() {
        return countPages;
    }

    public void setCountPages(int countPages) {
        this.countPages = countPages;
    }




}

