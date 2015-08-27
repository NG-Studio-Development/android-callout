package su.whs.call.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import su.whs.call.R;
import su.whs.call.dialog.InfoDialog;
import su.whs.call.dialog.SearchInfoDialog;
import su.whs.call.register.User;

public class SearchChooseLocationPromptFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.search_splash, container, false);
        User user = User.create(getActivity());
        if (user.isExecutor()) {
           // final MainActivity activity = (MainActivity) ;
           // activity.setCurrentTabByTag("login");
        }
        (v.findViewById(R.id.chooseLocationBtn)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(ChooseLocationFragment.newInstance());
            }
        });
        (v.findViewById(R.id.buttonInfo)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new SearchInfoDialog(getActivity()).show();
            }
        });
        mContentView = v;

        //Log.d("onCreate", "dwdw");

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {

        setContentShown(true);
        super.onResume();
    }

    @Override
    public boolean isHomeIconVisible() {
        return false;
    }


    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public boolean isCustomViewVisible() {
        return false;
    }

    @Override
    public boolean isTitleBarVisible() {
        return false;
    }

    public static Fragment newInstance() {
        Fragment result = new SearchChooseLocationPromptFragment();
        return result;
    }

    @Override
    public void onInfoIconClick() {
        //TODO Make cool search info
        InfoDialog infoDialog = new InfoDialog(getActivity());
        infoDialog.setTitle(getString(R.string.info_search_main));
        infoDialog.show();
    }

}
