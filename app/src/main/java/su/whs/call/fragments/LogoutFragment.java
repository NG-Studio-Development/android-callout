package su.whs.call.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import su.whs.call.R;
import su.whs.call.form.MainActivity;
import su.whs.call.register.User;

/**
 * Created by featZima on 31.08.2014.
 */
public class LogoutFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //View rootView = inflater.inflate(R.layout.login_fragment, container,false);

       /* User user = User.create(getActivity());
        if (user.isLoggedIn()) {
            user.logout();
        }

       // MainActivity.mTabHost.setCurrentTab(4);
*/
        return null;
    }

    @Override
    public String getTitle() {
        return "Logout";
    }
}
