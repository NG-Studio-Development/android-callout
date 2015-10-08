package su.whs.call.fragments;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import su.whs.call.R;
import su.whs.call.adapters.FavoritesAdapter;
import su.whs.call.adapters.UsersAdapter;
import su.whs.call.dialog.FavoritesFilterInfoDialog;
import su.whs.call.dialog.InfoDialog;
import su.whs.call.form.MainActivity;
import su.whs.call.models.FavoriteItem;
import su.whs.call.models.RecentCall;
import su.whs.call.models.SubCategory;
import su.whs.call.models.UserExtra;
import su.whs.call.models.UserInfo;
import su.whs.call.net.ConnectionHandler;
import su.whs.call.register.User;
import su.whs.call.utils.BackPressed;
import su.whs.call.views.FilterPanel;

public class FavoritesFragment extends BaseFavoritesTabFragment implements OnItemClickListener, BackPressed.OnBackPressedListener {
    private ListView mListView;
    private BaseAdapter mAdapter;
    private FilterPanel mFilterPanel;
    private boolean asList = false;
    private SubCategory mSubCat = null;
    private Location lastLocation;
    private List<FavoriteItem> mFavoritesItems;

    List<RecentCall> rc = new ArrayList<RecentCall>();

    public FavoritesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.favorites_fragment, container, false);
        mListView = (ListView) view.findViewById(R.id.favoritesList);

        lastLocation = MainActivity.getLastLocation();

        Bundle args = getArguments();
        if (args != null && args.containsKey("subcat")) {

            SubCategory subcat = (SubCategory) args.getSerializable("subcat");
            mSubCat = subcat;
            asList = true;
            UsersAdapter adapter = new UsersAdapter(getActivity(), subcat.getUsers());
            adapter.setLastLocation(lastLocation);
            adapter.setSortType(UsersAdapter.SORT_BY_DISTANCE);
            mListView.setAdapter(adapter);
            mAdapter = adapter;
            isHomeButtonDisabled = false;
        } else {
            // open fragment via tabs manager,
            // so check that user is logged in
            // else redirect to main
            isHomeButtonDisabled = true;

            final User user = User.create(getActivity());
            if (user.isGuest() || user.isExecutor()) {
                if (user.isExecutor()) {
                    MainActivity.mTabHost.setCurrentTab(1);
                } else {
                    MainActivity.mTabHost.setCurrentTab(2);
                    Toast.makeText(getActivity(), getString(R.string.favorites_only_for_registered), Toast.LENGTH_LONG).show();
                }
            } else {



                //setContentShown(true);
                ConnectionHandler connection = ConnectionHandler.getInstance(getActivity());
                connection.queryUsers(new ConnectionHandler.OnUsersInfoListener() {
                    @Override
                    public void onUsersInfoReady(List<UserExtra> list) {
                       /* List<UserInfo> favoritesUsers = new ArrayList<UserInfo>();
                        // filter user for favorites
                        mFavoritesItems = MainActivity.mDB.getFavorites(user.getToken());

                        for (UserExtra userExtra : list) {
                            boolean inFavorites = false;
                            for (FavoriteItem favoriteItem : mFavoritesItems) {
                                if (userExtra.getUserInfo().getId() == favoriteItem.userId) {
                                    inFavorites = true;
                                }
                            }
                            if (inFavorites) favoritesUsers.add(userExtra.getUserInfo());
                        }

                        mAdapter = new RecentUsersAdapter(getActivity(), favoritesUsers, mFavoritesItems);
                        mListView.setAdapter(mAdapter);
                        ajustListViewTopMargin();*/


                        rc = MainActivity.mDB.getFavorites(user.getToken());
                        sortUsers(rc);
                        FavoritesAdapter adapter = new FavoritesAdapter(getActivity(), rc);


                       // mAdapter = new RecentUsersAdapter(getActivity(), rc);

                        //Log.d("2222222222222", "" + mAdapter.getCount());

                        mListView.setAdapter(adapter);
                        //ajustListViewTopMargin();
                        setContentShown(true);
                    }

                    @Override
                    public void onFail() {
                        Toast.makeText(getActivity(), getString(R.string.loading_error), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        mListView.setOnItemClickListener(this);
        mFilterPanel = new FilterPanel(getActivity(), null);
        ajustListViewTopMargin();

        mContentView = view;

        BackPressed.setOnBackPressedFragmentListener(this);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private boolean isFavoriteList() {
        return mSubCat == null;
    }

    @Override
    public void onResume() {
        setFavorites();
        setContentShown(mAdapter != null);
        mFilterPanel.setFilterListener(filterListener);
        //ajustListViewTopMargin();
        super.onResume();
    }

    private void setFavorites() {


        Bundle args = getArguments();
        if (args != null && args.containsKey("subcat")) {

        } else {
            isHomeButtonDisabled = true;

            final User user = User.create(getActivity());
            if (user.isGuest() || user.isExecutor()) {
                if (user.isExecutor()) {
                    MainActivity.mTabHost.setCurrentTab(1);
                } else {
                    MainActivity.mTabHost.setCurrentTab(3);
                    Toast.makeText(getActivity(), getString(R.string.favorites_only_for_registered), Toast.LENGTH_LONG).show();
                }
            } else {


                ConnectionHandler connection = ConnectionHandler.getInstance(getActivity());
                connection.queryUsers(new ConnectionHandler.OnUsersInfoListener() {
                    @Override
                    public void onUsersInfoReady(List<UserExtra> list) {
                       /* List<UserInfo> favoritesUsers = new ArrayList<UserInfo>();
                        mFavoritesItems = MainActivity.mDB.getFavorites(user.getToken());


                        Log.d("11111 = ", "mFavoritesItemsmFavoritesItems " + mFavoritesItems.size());

                        for (UserExtra userExtra : list) {
                            boolean inFavorites = false;
                            for (FavoriteItem favoriteItem : mFavoritesItems) {
                                if (userExtra.getUserInfo().getId() == favoriteItem.userId) {
                                    inFavorites = true;
                                }
                            }
                            if (inFavorites) favoritesUsers.add(userExtra.getUserInfo());
                        }
*/
                        rc = MainActivity.mDB.getFavorites(user.getToken());

                        sortUsers(rc);
                        FavoritesAdapter adapter = new FavoritesAdapter(getActivity(), rc);
                        Log.d("2222222222222", "" + rc.size());

                        //mAdapter = new RecentUsersAdapter(getActivity(), rc);
                        mListView.setAdapter(adapter);
                        //ajustListViewTopMargin();
                        setContentShown(true);
                    }

                    @Override
                    public void onFail() {
                        //Toast.makeText(activity, getString(R.string.loading_error), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }


    public void sortUsers(List<RecentCall> rc) {
        Collections.sort(rc, new Comparator<RecentCall>() {
            @Override
            public int compare(RecentCall userInfo, RecentCall userInfo2) {
                Boolean isBusy = userInfo.isBusy();
                Boolean isBusy2 = userInfo2.isBusy();

                if (isBusy != isBusy2) {
                    return isBusy.compareTo(isBusy2);
                }
                return 0;
            }
        });
    }

    @Override
    public void onPause() {
        mFilterPanel.removeFilterListener(filterListener);
        super.onPause();
    }


    @Override
    public void onCreateView() {
        super.onCreateView();
        //ajustListViewTopMargin();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter = null;
        mFavoritesItems = null;
    }

    private FilterPanel.FilterListener filterListener = new FilterPanel.FilterListener() {
        @Override
        public void onLeftButtonActivated() {
            if (mAdapter instanceof UsersAdapter) {

                ((UsersAdapter) mAdapter).setSortType(UsersAdapter.SORT_BY_DISTANCE);
            }
        }

        @Override
        public void onRightButtonActivated() {
            if (mAdapter instanceof UsersAdapter) {

                ((UsersAdapter) mAdapter).setSortType(UsersAdapter.SORT_BY_RATING);
            }
        }

    };

    private void ajustListViewTopMargin() {
        if (mListView == null)
            return;

        LayoutParams lp = (LayoutParams) mListView.getLayoutParams();
        // lp.topMargin = getTitleBar().getBarHeight();
        try {
            int marginTop = getTitleBar().getBarHeight() + (int) getResources().getDimension(R.dimen.filterPanelHeight);
            lp.setMargins(0, marginTop, 0, 0);
            mListView.setLayoutParams(lp);
            //mListView.setLayoutParams(lp);
            //mListView.setLayoutParams(lp);

        } catch (NullPointerException e) {

        }
    }

    @Override
    public String getTitle() {
        if (asList && mSubCat != null) {
            return mSubCat.getName().toUpperCase();
        } else {
            return getResources().getString(R.string.favorites).toUpperCase();
        }
    }

    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        fragment.isHomeButtonDisabled = true;
        return fragment;
    }

    public static FavoritesFragment newInstance(SubCategory subcat) {
        FavoritesFragment f = newInstance();
        Bundle args = new Bundle();
        args.putSerializable("subcat", subcat);
        f.setArguments(args);
        return f;
    }

    public FavoriteItem getSubCategoryForFavoriteUser(UserInfo ui) {
        if (mFavoritesItems != null) {
            for (FavoriteItem favoriteItem : mFavoritesItems) {
                if (favoriteItem.userId == ui.getId()) return favoriteItem;
            }
        }
        return null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



       // RecentCall ui = (RecentCall) mAdapter.getItem(position);
        if (mSubCat != null) {

            UserInfo ui = (UserInfo) mAdapter.getItem(position);
            getApplication().setSubCategory(mSubCat);
            openFragment(PerformerFragment.newInstance(ui, asList, mSubCat.getName(), mSubCat.getId()));
        } else {
            RecentCall ui = rc.get(position);
            //FavoriteItem favoriteItem = getSubCategoryForFavoriteUser(ui);
            openFragment(PerformerFragment.newInstance(ui, asList, ui.getSubCategoryTitle(), ui.getSubCategoryId()));
        }
    }

    @Override
    public View getCustomView() {
        //ajustListViewTopMargin();
        if (mSubCat != null)
            return mFilterPanel;
        else
            return null;
    }

    @Override
    public boolean isHomeIconVisible() {
        return asList;
    }

    @Override
    public boolean isCustomViewVisible() {
        return !isFavoriteList();
    }

    @Override
    public boolean onHomeIconClick() {
        return onBack();
    }

    private boolean onBack() {
        if (asList) {
            openFragment(SubCategoriesFragment.restoreInstance());
            return true;
        }
        return false;
    }

    @Override
    public void onInfoIconClick() {
        if (asList) {
            new FavoritesFilterInfoDialog(getActivity()).show();
        } else {
            InfoDialog infoDialog = new InfoDialog(getActivity());
            infoDialog.setTitle(getString(R.string.info_favorites));
            infoDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

}
