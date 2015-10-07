package su.whs.call.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.joooonho.SelectableRoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import su.whs.call.Constants;
import su.whs.call.R;
import su.whs.call.dialog.AdvancedInfoDialogFragment;
import su.whs.call.dialog.PerformerInfoDialog;
import su.whs.call.form.MainActivity;
import su.whs.call.models.RecentCall;
import su.whs.call.models.Review;
import su.whs.call.models.UserInfo;
import su.whs.call.net.ConnectionHandler;
import su.whs.call.register.User;
import su.whs.call.views.RateStarsView;


public class PerformerFragment extends BaseFragment {

    private static final String TAG = "PerformerFragment";
    private static final String USER_INFO_ARG = "userinfo";
    private static final String AS_LIST_ARG = "asList";
    private static final String FROM_HISTORY = "fromHistory";
    private static final String SUBCATEGORY_ID_ARG = "subCategoryId";
    private static final String SUBCATEGORY_TITLE_ARG = "subCategoryTitle";

    private ImageView mCallButton;
    private SelectableRoundedImageView mAvatarView;
    private ImageView mBusyMark;
    private TextView mNameView;
    private Button mReviewsBtn;
    private Button mFavoritesSwitcher;
    private TextView mTextDescription;
    private RateStarsView mRateView;
    private boolean mBackToList;
    private ScrollView parentScrollView;
    private ScrollView childScrollView;
    private static PerformerFragment mInstance = null;
    private List<Review> mReviews = new ArrayList<Review>();

    public static RecentCall mUserInfo;
    public static UserInfo mUserInfo1;
    public static int mSubCategoryId = -1;
    private String mSubCategoryTitle;

    private boolean isFromUserProfile = false;

    private Bundle arguments;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .build();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.performer_fragment, container, false);


        mCallButton = (ImageView) rootView.findViewById(R.id.callButton);
        mAvatarView = (SelectableRoundedImageView) rootView.findViewById(R.id.avatarView);
        mBusyMark = (ImageView) rootView.findViewById(R.id.busyMark);
        mNameView = (TextView) rootView.findViewById(R.id.nameText);
        mRateView = (RateStarsView) rootView.findViewById(R.id.rateView);
        mTextDescription = (TextView) rootView.findViewById(R.id.textDescription);
        mReviewsBtn = (Button) rootView.findViewById(R.id.reviewsBtn);
        mFavoritesSwitcher = (Button) rootView.findViewById(R.id.favoritesSwitcher);
        parentScrollView = (ScrollView) rootView.findViewById(R.id.parentScrollView);
        childScrollView = (ScrollView) rootView.findViewById(R.id.childScrollView);

        parentScrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rootView.findViewById(R.id.childScrollView).getParent()
                        .requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        childScrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of  child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        arguments = getArguments();
        if (arguments.containsKey(USER_INFO_ARG)) {

                if(arguments.getSerializable(USER_INFO_ARG) instanceof  RecentCall) {
                    mUserInfo = (RecentCall) arguments.getSerializable(USER_INFO_ARG);
                    mNameView.setText(mUserInfo.getUserName());
                    mRateView.setStars((int) mUserInfo.getRate());
                    imageLoader.displayImage(Constants.API + mUserInfo.getAvatar(), mAvatarView, options);
                    mTextDescription.setText(mUserInfo.getDescription());
                    mTextDescription.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AdvancedInfoDialogFragment.newInstance(mUserInfo.getDescription()).show(getFragmentManager(), null);
                        }
                    });

                    if (mUserInfo.isBusy()) {
                        mAvatarView.setAlpha(Constants.ALPHA_VIEW_FOR_BUSY);
                        mRateView.setAlpha(Constants.ALPHA_VIEW_FOR_BUSY);
                        mNameView.setAlpha(Constants.ALPHA_VIEW_FOR_BUSY);
                        mCallButton.setAlpha(Constants.ALPHA_VIEW_FOR_BUSY);

                        mBusyMark.setImageResource(R.drawable.ic_circle_red_small);
                        mCallButton.setImageResource(R.drawable.ic_call_button_disabled);
                    } else {
                        mAvatarView.setAlpha(1f);
                        mRateView.setAlpha(1f);
                        mNameView.setAlpha(1f);

                        mBusyMark.setImageResource(R.drawable.ic_circle_green_small);
                        mCallButton.setOnClickListener(callBtnListener);
                        mCallButton.setImageResource(R.drawable.ic_call_button_green);
                    }
                    mSubCategoryId = arguments.getInt(SUBCATEGORY_ID_ARG);
                    mSubCategoryTitle = arguments.getString(SUBCATEGORY_TITLE_ARG);

                    queryReviews(mUserInfo.getUserId(), mSubCategoryId);
                } else {
                    mUserInfo1 = (UserInfo) arguments.getSerializable(USER_INFO_ARG);
                    mNameView.setText(mUserInfo1.getUserName());
                    mRateView.setStars((int) mUserInfo1.getRate());
                    imageLoader.displayImage(Constants.API + mUserInfo1.getAvatarURL(), mAvatarView, options);
                    mTextDescription.setText(mUserInfo1.getDescription());
                    mTextDescription.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AdvancedInfoDialogFragment.newInstance(mUserInfo1.getDescription()).show(getFragmentManager(), null);
                        }
                    });

                    if (mUserInfo1.isBusy()) {
                        mAvatarView.setAlpha(Constants.ALPHA_VIEW_FOR_BUSY);
                        mRateView.setAlpha(Constants.ALPHA_VIEW_FOR_BUSY);
                        mNameView.setAlpha(Constants.ALPHA_VIEW_FOR_BUSY);
                        mCallButton.setAlpha(Constants.ALPHA_VIEW_FOR_BUSY);

                        mBusyMark.setImageResource(R.drawable.ic_circle_red_small);
                        mCallButton.setImageResource(R.drawable.ic_call_button_disabled);

                    } else {

                        mAvatarView.setAlpha(1f);
                        mRateView.setAlpha(1f);
                        mNameView.setAlpha(1f);

                        mBusyMark.setImageResource(R.drawable.ic_circle_green_small);
                        mCallButton.setOnClickListener(callBtnListener);
                        mCallButton.setImageResource(R.drawable.ic_call_button_green);
                    }
                    mSubCategoryId = arguments.getInt(SUBCATEGORY_ID_ARG);
                    mSubCategoryTitle = arguments.getString(SUBCATEGORY_TITLE_ARG);

                    queryReviews(mUserInfo1.getId(), mSubCategoryId);
                }
        }

        isFromUserProfile = arguments.getBoolean(FROM_HISTORY, false);
        mBackToList = arguments.getBoolean(AS_LIST_ARG, false);
        mContentView = rootView;

        mReviewsBtn.setOnClickListener(reviewsBtnListener);

        mFavoritesSwitcher.setOnClickListener(favoritesSwitcherListener);

        if(arguments.getSerializable(USER_INFO_ARG) instanceof  RecentCall) {
            if (mUserInfo != null && mSubCategoryId > -1) {


                User user = User.create(getActivity());
                if (user.isLoggedIn()) {
                    if (MainActivity.mDB.isFavorites(user.getToken(), mUserInfo.getUserId())) {

                        mFavoritesSwitcher.setSelected(true);
                        mFavoritesSwitcher.setText(getString(R.string.from_favorites));
                        mFavoritesSwitcher.setTextColor(Color.WHITE);
                        mFavoritesSwitcher.setBackgroundResource(R.drawable.right_selected);
                    } else {

                        mFavoritesSwitcher.setSelected(false);
                        mFavoritesSwitcher.setText(getString(R.string.to_favorites));
                        mFavoritesSwitcher.setBackgroundResource(R.drawable.right_tab);

                        mFavoritesSwitcher.setTextColor(Color.BLACK);

                    }
                }
            }
        } else {
            if (mUserInfo1 != null && mSubCategoryId > -1) {


                User user = User.create(getActivity());
                if (user.isLoggedIn()) {
                    if (MainActivity.mDB.isFavorites(user.getToken(), mUserInfo1.getId())) {

                        mFavoritesSwitcher.setSelected(true);
                        mFavoritesSwitcher.setText(getString(R.string.from_favorites));
                        mFavoritesSwitcher.setTextColor(Color.WHITE);
                        mFavoritesSwitcher.setBackgroundResource(R.drawable.right_selected);
                    } else {

                        mFavoritesSwitcher.setSelected(false);
                        mFavoritesSwitcher.setText(getString(R.string.to_favorites));
                        mFavoritesSwitcher.setBackgroundResource(R.drawable.right_tab);

                        mFavoritesSwitcher.setTextColor(Color.BLACK);

                    }
                }
            }
        }

        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private View.OnClickListener reviewsBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(arguments.getSerializable(USER_INFO_ARG) instanceof  RecentCall) {
                if (mUserInfo != null && mSubCategoryId > -1) {
                    User user = User.create(getActivity());
                    // if (user.isLoggedIn()) {

                    try {
                        openFragment(ReviewsFragment.newInstance((ArrayList<Review>) mReviews, mUserInfo, mSubCategoryId));
                    } catch (Exception e) {
                        openFragment(ReviewsFragment.newInstance((ArrayList<Review>) mReviews, mUserInfo1, mSubCategoryId));
                    }
                    // } else {
                    //   Toast.makeText(getActivity(), getString(R.string.comment_restrictions), Toast.LENGTH_SHORT).show();
                    //}
                }
            } else {
                if (mUserInfo1 != null && mSubCategoryId > -1) {
                    User user = User.create(getActivity());
                    // if (user.isLoggedIn()) {
                    try {
                        openFragment(ReviewsFragment.newInstance((ArrayList<Review>) mReviews, mUserInfo1, mSubCategoryId));
                    } catch (Exception e) {
                        openFragment(ReviewsFragment.newInstance((ArrayList<Review>) mReviews, mUserInfo, mSubCategoryId));
                    }
                    // } else {
                    //   Toast.makeText(getActivity(), getString(R.string.comment_restrictions), Toast.LENGTH_SHORT).show();
                    //}
                }
            }


        }
    };

    private View.OnClickListener callBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            User user = User.create(getActivity());
            String uri = null;
            if (user.isLoggedIn()) {

                if(arguments.getSerializable(USER_INFO_ARG) instanceof  RecentCall) {

                    if(mUserInfo.getTelephoneNumber().trim().length() < 2) {
                        Toast.makeText(getActivity(), "Номер абонента отсутствует", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    user.postCall(mUserInfo.getUserId(), mSubCategoryTitle, mSubCategoryId);
                    ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());
                    handler.postCall(user.getToken(), mUserInfo.getUserId(), mSubCategoryId);
                    uri = "tel:" + mUserInfo.getTelephoneNumber().trim();

                } else {

                    if(mUserInfo1.getTelephone().trim().length() < 2) {
                        Toast.makeText(getActivity(), "Номер абонента отсутствует", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    user.postCall(mUserInfo1.getId(), mSubCategoryTitle, mSubCategoryId);
                    ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());
                    handler.postCall(user.getToken(), mUserInfo1.getId(), mSubCategoryId);
                    uri = "tel:" + mUserInfo1.getTelephone().trim();

                }
            } else {
                if(arguments.getSerializable(USER_INFO_ARG) instanceof  RecentCall) {
                    uri = "tel:" + mUserInfo.getTelephoneNumber().trim();
                } else {
                    uri = "tel:" + mUserInfo1.getTelephone().trim();
                }
            }

            if(uri != null && uri.length() > 4) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "Номер абонента отсутствует", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener favoritesSwitcherListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // add current executor and subcategory to favorites


            if(arguments.getSerializable(USER_INFO_ARG) instanceof  RecentCall) {
                if (mUserInfo != null && mSubCategoryId > -1) {
                    User user = User.create(getActivity());
                    if (user.isLoggedIn()) {
                        if (MainActivity.mDB.isFavorites(user.getToken(), mUserInfo.getUserId())) {

                            MainActivity.mDB.deleteFavarites(user.getToken(), mUserInfo.getUserId());

                            // user.removeExecutorFromFavorites(mSubCategoryId, mUserInfo.getId());
                            mFavoritesSwitcher.setSelected(false);
                            mFavoritesSwitcher.setText(getString(R.string.to_favorites));
                            mFavoritesSwitcher.setBackgroundResource(R.drawable.right_tab);
                            mFavoritesSwitcher.setTextColor(Color.BLACK);

                        } else {
                            //user.addExecutorToFavorites(mSubCategoryTitle, mSubCategoryId, mUserInfo.getId());
                            SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm:ss");
                            Date t = new Date();

                            SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yy");
                            Date date = new Date();


                            MainActivity.mDB.setFavorites(user.getToken(), mUserInfo.getAvatar(), mUserInfo.getUserName(),
                                    mUserInfo.getStatus(), (float) mUserInfo.getRate(), mUserInfo.getDescription(), mUserInfo.getTelephoneNumber(),
                                    mSubCategoryTitle, mSubCategoryId, sdfTime.format(t), sdfDate.format(date), mUserInfo.getUserId());

                            mFavoritesSwitcher.setSelected(true);
                            mFavoritesSwitcher.setText(getString(R.string.from_favorites));
                            mFavoritesSwitcher.setTextColor(Color.WHITE);
                            mFavoritesSwitcher.setBackgroundResource(R.drawable.right_selected);
                        }
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.favorites_restrictions), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if (mUserInfo1 != null && mSubCategoryId > -1) {
                    User user = User.create(getActivity());
                    if (user.isLoggedIn()) {
                        if (MainActivity.mDB.isFavorites(user.getToken(), mUserInfo1.getId())) {

                            MainActivity.mDB.deleteFavarites(user.getToken(), mUserInfo1.getId());

                            // user.removeExecutorFromFavorites(mSubCategoryId, mUserInfo.getId());
                            mFavoritesSwitcher.setSelected(false);
                            mFavoritesSwitcher.setText(getString(R.string.to_favorites));
                            mFavoritesSwitcher.setBackgroundResource(R.drawable.right_tab);
                            mFavoritesSwitcher.setTextColor(Color.BLACK);

                        } else {
                            //user.addExecutorToFavorites(mSubCategoryTitle, mSubCategoryId, mUserInfo.getId());
                            SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm:ss");
                            Date t = new Date();

                            SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yy");
                            Date date = new Date();


                            MainActivity.mDB.setFavorites(user.getToken(), mUserInfo1.getAvatarURL(), mUserInfo1.getUserName(),
                                    mUserInfo1.getStatus(), (float) mUserInfo1.getRate(), mUserInfo1.getDescription(), mUserInfo1.getTelephone(),
                                    mSubCategoryTitle, mSubCategoryId, sdfTime.format(t), sdfDate.format(date), mUserInfo1.getId());

                            mFavoritesSwitcher.setSelected(true);
                            mFavoritesSwitcher.setText(getString(R.string.from_favorites));
                            mFavoritesSwitcher.setTextColor(Color.WHITE);
                            mFavoritesSwitcher.setBackgroundResource(R.drawable.right_selected);
                        }
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.favorites_restrictions), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    private void queryReviews(int userId, int subCategoryId) {
        ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());
        handler.queryReviews(userId, subCategoryId, new ConnectionHandler.OnReviewsListener() {
            @Override
            public void onReviewsResponse(List<Review> reviews) {
                mReviews = reviews;
                mReviewsBtn.setText(String.format(
                        getString(R.string.reviews_cap_with_count),
                        reviews.size()));
            }
        });
    }

    @Override
    public void onResume() {
        setContentShown(true);
        super.onResume();
    }

    @Override
    public String getTitle() {
        return getString(R.string.executor).toUpperCase();
    }

    @Override
    public boolean onHomeIconClick() {
        if (isFromUserProfile) {
             openFragment(CabinetFragment.newInstance());
             MainActivity.setCurrentTabByTagHack();
            return true;
        }
        if (mBackToList) {
            openFragment(FavoritesFragment.newInstance(getApplication().getSubcategory()));
        } else {
            openFragment(FavoritesFragment.newInstance());
        }
        return true;
    }

   /* public static PerformerFragment newInstance(UserInfo ui, boolean asList, String subCategoryTitle, int subCategoryId) {
        PerformerFragment fragment = new PerformerFragment();
        Bundle args = new Bundle();
        args.putSerializable(USER_INFO_ARG, ui);
        args.putBoolean(AS_LIST_ARG, asList);
        args.putInt(SUBCATEGORY_ID_ARG, subCategoryId);
        args.putString(SUBCATEGORY_TITLE_ARG, subCategoryTitle);
        fragment.setArguments(args);
        mInstance = fragment;
        return fragment;
    }*/

    public static PerformerFragment newInstance(RecentCall ui, boolean asList, String subCategoryTitle, int subCategoryId) {
        PerformerFragment fragment = new PerformerFragment();
        Bundle args = new Bundle();
        args.putSerializable(USER_INFO_ARG, ui);
        args.putBoolean(AS_LIST_ARG, asList);
        args.putInt(SUBCATEGORY_ID_ARG, subCategoryId);
        args.putString(SUBCATEGORY_TITLE_ARG, subCategoryTitle);
        fragment.setArguments(args);
        mInstance = fragment;
        return fragment;
    }

    public static PerformerFragment newInstance(UserInfo ui, boolean asList, String subCategoryTitle, int subCategoryId) {
        PerformerFragment fragment = new PerformerFragment();
        Bundle args = new Bundle();
        args.putSerializable(USER_INFO_ARG, ui);
        args.putBoolean(AS_LIST_ARG, asList);
        args.putInt(SUBCATEGORY_ID_ARG, subCategoryId);
        args.putString(SUBCATEGORY_TITLE_ARG, subCategoryTitle);
        fragment.setArguments(args);
        mInstance = fragment;
        return fragment;
    }

    public static PerformerFragment newInstance(RecentCall ui, String subCategoryTitle, int subCategoryId) {
        PerformerFragment fragment = new PerformerFragment();
        Bundle args = new Bundle();
        args.putSerializable(USER_INFO_ARG, ui);
        args.putBoolean(FROM_HISTORY, true);
        args.putInt(SUBCATEGORY_ID_ARG, subCategoryId);
        args.putString(SUBCATEGORY_TITLE_ARG, subCategoryTitle);
        fragment.setArguments(args);
        mInstance = fragment;
        return fragment;
    }

    public static Fragment restoreInstance() {
        return mInstance;
    }

    @Override
    public void onInfoIconClick() {
        new PerformerInfoDialog(getActivity()).show();
    }



}
