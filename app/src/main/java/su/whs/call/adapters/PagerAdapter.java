package su.whs.call.adapters;





import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import su.whs.call.fragments.PageFragment;


/**
 * Created by Infinity-PC on 27.01.2015.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    private int countPages = 2;

    private static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    private JSONArray json;

    public PagerAdapter(FragmentManager fm, JSONArray array) {
        super(fm);
        this.json = array;
    }

    @Override
    public Fragment getItem(int position) {
        PageFragment pageFragment = new PageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, position);
        pageFragment.setArguments(arguments);

        return pageFragment;
    }



    public JSONObject getJSONObject(int position) {

        try {
            return json.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }



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

