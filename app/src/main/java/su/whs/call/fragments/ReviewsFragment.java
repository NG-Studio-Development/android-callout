package su.whs.call.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import su.whs.call.R;
import su.whs.call.adapters.ReviewsAdapter;
import su.whs.call.dialog.InfoDialog;
import su.whs.call.models.RecentCall;
import su.whs.call.models.Review;
import su.whs.call.models.UserInfo;
import su.whs.call.net.ConnectionHandler;
import su.whs.call.register.User;

public class ReviewsFragment extends BaseSearchTabFragment {

    private static final String REVIEWS_ARG = "reviews";
    private static final String USER_INFO_ARG = "user_info";
    private static final String SUBCATEGORY_ID_ARG = "subcategory_id";

    private ListView mList;
    private List<Review> mReviews;
    private LinearLayout mReviewsListArea;
    private LinearLayout mReviewWritingArea;
    private RatingBar mRatingBar;
    private TextView mTextUser;
    private EditText mEditReview;
    private RecentCall mUserInfo;
    private UserInfo mUserInfo1;
    private Button mSendReviewBtn;
    private int mSubCategoryId = -1;
    private TextView mTextFrom;
    private Bundle args;

    public static ReviewsFragment newInstance(ArrayList<Review> reviews, RecentCall userInfo, int subCategoryId) {
        Bundle arguments = new Bundle();
        if (reviews != null) {
            arguments.putSerializable(REVIEWS_ARG, reviews);
        }
        arguments.putSerializable(USER_INFO_ARG, userInfo);
        arguments.putSerializable(SUBCATEGORY_ID_ARG, subCategoryId);
        ReviewsFragment f = new ReviewsFragment();
        f.setArguments(arguments);

        return f;
    }

    public static ReviewsFragment newInstance(ArrayList<Review> reviews, UserInfo userInfo, int subCategoryId) {
        Bundle arguments = new Bundle();
        if (reviews != null) {
            arguments.putSerializable(REVIEWS_ARG, reviews);
        }
        arguments.putSerializable(USER_INFO_ARG, userInfo);
        arguments.putSerializable(SUBCATEGORY_ID_ARG, subCategoryId);
        ReviewsFragment f = new ReviewsFragment();
        f.setArguments(arguments);

        return f;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.reviews_fragment, container, false);
        mList = (ListView) rootView.findViewById(R.id.listView);
        mRatingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);
        mTextUser = (TextView) rootView.findViewById(R.id.textUser);
        mTextFrom = (TextView) rootView.findViewById(R.id.textFrom);
        mEditReview = (EditText) rootView.findViewById(R.id.editReview);
        mReviewsListArea = (LinearLayout) rootView.findViewById(R.id.areaReviewsList);
        mReviewWritingArea = (LinearLayout) rootView.findViewById(R.id.areaReviewWriting);
        mSendReviewBtn = (Button) rootView.findViewById(R.id.sendReviewBtn);

        args = getArguments();
        if (args.containsKey(REVIEWS_ARG)) {
            mReviews = (List<Review>) args.getSerializable(REVIEWS_ARG);
            Collections.reverse(mReviews);
            mList.setAdapter(new ReviewsAdapter(getActivity(), mReviews));
        }

        if(args.getSerializable(USER_INFO_ARG) instanceof RecentCall) {
            mUserInfo = (RecentCall) args.getSerializable(USER_INFO_ARG);
            mTextUser.setText(mUserInfo.getUserName());
        } else {
            mUserInfo1 = (UserInfo) args.getSerializable(USER_INFO_ARG);
            mTextUser.setText(mUserInfo1.getUserName());
        }


        mTextFrom.setText(User.create(getActivity()).getUserName());
        mSubCategoryId = args.getInt(SUBCATEGORY_ID_ARG);

        User user = User.create(getActivity());
        mSendReviewBtn.setOnClickListener(sendReviewListener);

        // hide keyboard on user tap Enter key on keyboard after review writing
        mEditReview.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(mEditReview.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });

        mContentView = rootView;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        setContentShown(true);
        mTitleBar.btnReview.setVisibility(View.VISIBLE);
        mTitleBar.btnReview.setText("оценить");
        super.onResume();
    }

    @Override
    public boolean onHomeIconClick() {
        openFragment(PerformerFragment.restoreInstance());
        return true;
    }

    @Override
    public String getTitle() {
        return getString(R.string.reviews_cap).toUpperCase();
    }

    View.OnClickListener sendReviewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // send user review to server
            String description = mEditReview.getText().toString();

            if (!User.create(getActivity()).isLoggedIn()) {
                Toast.makeText(getActivity(), getString(R.string.review_need_login), Toast.LENGTH_SHORT).show();
                return;
            }

            if (description.isEmpty()) {
                Toast.makeText(getActivity(), getString(R.string.cant_send_empty_review), Toast.LENGTH_SHORT).show();
            } else {
                User user = User.create(getActivity());
                Review review = new Review(user.getUserName(), description,  mRatingBar.getRating());
                ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());
                setContentShown(false);

                int userId;
                if(args.getSerializable(USER_INFO_ARG) instanceof RecentCall) {
                    userId = mUserInfo.getUserId();
                } else {
                    userId = mUserInfo1.getId();
                }

                handler.postReview(userId, mSubCategoryId, user.getToken(), review, new ConnectionHandler.OnPostReviewListener() {
                    @Override
                    public void onSuccessPostReview() {
                        mEditReview.setText(getString(R.string.empty));
                        mRatingBar.setRating(0f);
                        mReviewsListArea.setVisibility(View.VISIBLE);
                        mReviewWritingArea.setVisibility(View.GONE);
                        mTitleBar.btnReview.setText("оценить");

                        Toast.makeText(getActivity(), getString(R.string.review_will_be_published), Toast.LENGTH_SHORT).show();

                        if(args.getSerializable(USER_INFO_ARG) instanceof RecentCall) {
                            queryReviews(PerformerFragment.mUserInfo.getUserId(), PerformerFragment.mSubCategoryId);
                        } else {
                            queryReviews(PerformerFragment.mUserInfo1.getId(), PerformerFragment.mSubCategoryId);
                        }


                        setContentShown(true);
                    }

                    @Override
                    public void onFailPostReview() {
                        Toast.makeText(getActivity(), getString(R.string.review_posting_error), Toast.LENGTH_SHORT).show();
                        setContentShown(true);
                    }
                });
            }
        }
    };

    @Override
    public void onInfoIconClick() {
        if (mReviewWritingArea.getVisibility() == View.VISIBLE) {
            String description = mEditReview.getText().toString();
            if (!User.create(getActivity()).isLoggedIn()) {
                Toast.makeText(getActivity(), getString(R.string.review_need_login), Toast.LENGTH_SHORT).show();
                return;
            }
            if (description.isEmpty()) {
                Toast.makeText(getActivity(), getString(R.string.cant_send_empty_review), Toast.LENGTH_SHORT).show();
            } else {
                User user = User.create(getActivity());
                Review review = new Review(user.getUserName(), description, mRatingBar.getRating());
                ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());
                setContentShown(false);

                int userId = 0;
                if(args.getSerializable(USER_INFO_ARG) instanceof RecentCall) {
                    userId = mUserInfo.getUserId();
                } else {
                    userId = mUserInfo1.getId();
                }


                handler.postReview(userId, mSubCategoryId, user.getToken(), review, new ConnectionHandler.OnPostReviewListener() {
                    @Override
                    public void onSuccessPostReview() {
                        mEditReview.setText(getString(R.string.empty));
                        mRatingBar.setRating(0f);
                        mReviewsListArea.setVisibility(View.VISIBLE);
                        mReviewWritingArea.setVisibility(View.GONE);
                        mTitleBar.btnReview.setText("оценить");
                        Toast.makeText(getActivity(), getString(R.string.review_will_be_published), Toast.LENGTH_SHORT).show();
                        setContentShown(true);

                        if(args.getSerializable(USER_INFO_ARG) instanceof RecentCall) {
                            queryReviews(PerformerFragment.mUserInfo.getUserId(), PerformerFragment.mSubCategoryId);
                        } else {
                            queryReviews(PerformerFragment.mUserInfo1.getId(), PerformerFragment.mSubCategoryId);
                        }
                    }

                    @Override
                    public void onFailPostReview() {
                        Toast.makeText(getActivity(), getString(R.string.review_posting_error), Toast.LENGTH_SHORT).show();
                        setContentShown(true);
                    }
                });

            }
        } else {
            if (!User.create(getActivity()).isLoggedIn()) {
                Toast.makeText(getActivity(), getString(R.string.comment_restrictions), Toast.LENGTH_SHORT).show();
                return;
            }

            mReviewsListArea.setVisibility(View.GONE);
            mReviewWritingArea.setVisibility(View.VISIBLE);
            mTitleBar.btnReview.setText("отправить");
        }
    }


    private void queryReviews(int userId, int subCategoryId) {
        ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());
        handler.queryReviews(userId, subCategoryId, new ConnectionHandler.OnReviewsListener() {
            @Override
            public void onReviewsResponse(List<Review> reviews) {
                mReviews = reviews;
                Collections.reverse(mReviews);
                mList.setAdapter(new ReviewsAdapter(getActivity(), mReviews));
            }
        });
    }

}
