package su.whs.call.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import su.whs.call.Constants;
import su.whs.call.R;
import su.whs.call.adapters.RecentCallsAdapter;
import su.whs.call.dialog.ExecutorInfoDialog;
import su.whs.call.dialog.ProfileInfoDialog;
import su.whs.call.form.CabinetActivity;
import su.whs.call.form.MainActivity;
import su.whs.call.models.ExecutorSubcategory;
import su.whs.call.models.RecentCall;
import su.whs.call.models.UserExtra;
import su.whs.call.models.UserInfo;
import su.whs.call.net.ConnectionHandler;
import su.whs.call.register.User;
import su.whs.call.views.RateStarsView;

public class CabinetFragment extends BaseCabinetFragment {

    private final static String TAG = "CABINET_FRAGMENT";


    public CabinetFragment() {}

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .build();

    private Button currentStateSwitcher;
    private Button numberOfCallsBtn;
    private TextView profileDescription;
    private RateStarsView executorRateView;
    private ListView recentCallsList;
    private LinearLayout clientArea;
    private ScrollView executorArea;
    private ImageView busyMark;
    private UserInfo mUserInfo;
    private ImageView clientAvatar;
    private ImageView executorAvatar;
    //private RegisteredYear mYear;
    //private List<CallsExpert> mListCalls;
    private Button executorCategoriesBtn;
    private ArrayList<ExecutorSubcategory> subcategories;
    //private Dialog auth_dialog;

    public static List<RecentCall> calls;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cabinet_fragment, container, false);


        //imgLoader = new ImageLoader(getActivity());

        User user = User.create(getActivity());

        if (!user.isLoggedIn()) {
            openFragment(LoginFragment.newInstance());
            mContentView = rootView;
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        super.onCreateView();

        clientArea = (LinearLayout) rootView.findViewById(R.id.clientArea);
        executorArea = (ScrollView) rootView.findViewById(R.id.executorArea);
        currentStateSwitcher = (Button) rootView.findViewById(R.id.currentStateSwitcher);
        numberOfCallsBtn = (Button) rootView.findViewById(R.id.numberOfCallsBtn);
        profileDescription = (TextView) rootView.findViewById(R.id.profileDescription);
        executorRateView = (RateStarsView) rootView.findViewById(R.id.executorRateView);
        recentCallsList = (ListView) rootView.findViewById(R.id.recentCallsList);
        busyMark = (ImageView) rootView.findViewById(R.id.busyMark);
        clientAvatar = (ImageView) rootView.findViewById(R.id.clientAvatar);
        executorAvatar = (ImageView) rootView.findViewById(R.id.executorAvatar);
        executorCategoriesBtn = (Button) rootView.findViewById(R.id.executorCategoriesBtn);
        currentStateSwitcher.setOnClickListener(currentStateListener);
        numberOfCallsBtn.setOnClickListener(callListener);
        executorCategoriesBtn.setOnClickListener(executorCategoriesListener);
        clientAvatar.setOnClickListener(avatarSelectListener);
        executorAvatar.setOnClickListener(avatarSelectListener);

        mContentView = rootView;

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public int getCustomHomeIconId() {
        return android.R.drawable.ic_menu_share;
    }

    @Override
    public boolean onHomeIconClick() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Фото");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Приложение Вызывай в твоём мобильном!\n\n\n" + Constants.URL_GOOGLE_PLAY);
        //sharingIntent.putExtra(Intent.EXTRA_STREAM, Util.getShareImagePath());
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.app_name)));
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        /* ((CabinetActivity) getActivity()).loadUserInformation(new ConnectionHandler.OnUserInfoListener() {
            @Override
            public void onUserInfoReady(List<UserExtra> allUsers, UserExtra ui) {
                mUserInfo = ui.getUserInfo();
                initClient(allUsers, ui.getUserInfo());
            }

            @Override
            public void onFail() {
                //Toast.makeText(getActivity(), "Fail in loadUserInformation()", Toast.LENGTH_LONG).show();
            }
        }); */
    }



    /* public void loadUserInformation() {
        ConnectionHandler connection = ConnectionHandler.getInstance(getActivity());
        connection.queryUser(User.create(getActivity()).getToken(), new ConnectionHandler.OnUserInfoListener() {
            @Override
            public void onUserInfoReady(List<UserExtra> allUsers, UserExtra ui) {
                numberOfCallsBtn.setText(String.format("%S (%s)", getString(R.string.number_of_calls), "0"));
                if (ui.getUserInfo().isClient()) {
                    initClient(allUsers, ui.getUserInfo());
                } else {
                    initExecutor(ui.getUserInfo());
                }
                mUserInfo = ui.getUserInfo();
                setContentShown(true);
            }

            @Override
            public void onFail() {
                Toast.makeText(getActivity(), "Fail in loadUserInformation()", Toast.LENGTH_LONG).show();
            }
        });
    } */


    //private staArrayList<ExecutorSubcategory> subcategories
    private void initExecutor(UserInfo ui) {
        executorArea.setVisibility(View.VISIBLE);
        clientArea.setVisibility(View.GONE);
        setProfileDescription(ui.getDescription());
        setBusyStatus(ui.isBusy());
        setUserAvatar(ui);
        setProfileUsername(ui.getUserName());
        executorRateView.setStars(ui.getAverageRate());

        /* ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());

        handler.queryExecutorCategories(User.create(getActivity()).getToken(), new ConnectionHandler.OnExecutorCategoriesListener() {
            @Override
            public void onCategoriesResponse(ArrayList<ExecutorSubcategory> subcategories) {
                CabinetFragment.this.subcategories = subcategories;

                if (subcategories != null && subcategories.size() != 0)
                    openFragment(ExecutorSubcategoriesFragment.newInstance(subcategories, mUserInfo));
                else
                    throw new Error("Executor not have categories");

                //executorCategoriesBtn.setText(String.format("%S (%d)",
                        //getString(R.string.my_categories),
                        //subcategories.size()));
            }
        }); */
    }


    @Override
    public void onResume() {
        super.onResume();
        MainActivity.btnCabinet.setSelected(true);


        ((CabinetActivity) getActivity()).loadUserInformation(new ConnectionHandler.OnUserInfoListener() {
            @Override
            public void onUserInfoReady(List<UserExtra> allUsers, UserExtra ui) {
                mUserInfo = ui.getUserInfo();
                initClient(allUsers, ui.getUserInfo());
            }

            @Override
            public void onFail() {
                //Toast.makeText(getActivity(), "Fail in loadUserInformation EF()", Toast.LENGTH_LONG).show();
            }
        });


    }

    private View.OnClickListener callListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(), "Unactuale function", Toast.LENGTH_LONG).show();
        }
    };

    private View.OnClickListener executorCategoriesListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (subcategories != null) {
                openFragment(ExecutorSubcategoriesFragment.newInstance(subcategories, mUserInfo));
            } else {
                Toast.makeText(getActivity(), "You do not have catigories", Toast.LENGTH_LONG).show();
            }
        }
    };


    private void initClient(final List<UserExtra> allUsers, UserInfo ui) {
        executorArea.setVisibility(View.GONE);
        clientArea.setVisibility(View.VISIBLE);
        setProfileUsername(ui.getUserName());
        setUserAvatar(ui);
        User user = User.create(getActivity());
        ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());
        handler.queryClientCalls(user.getToken(), new ConnectionHandler.OnClientCallsListener() {
            @Override
            public void onCallsResponse(List<RecentCall> calls) {

                CabinetFragment.calls = calls;
                Collections.reverse(calls);

                final RecentCallsAdapter mAdapter = new RecentCallsAdapter(getActivity(), calls);
                recentCallsList.setAdapter(mAdapter);

                recentCallsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        RecentCall recentCall = CabinetFragment.calls.get(i);

                        //UserInfo userInfo = new UserInfo(recentCall);
                        openFragment(PerformerFragment.newInstance(recentCall, recentCall.getSubCategoryTitle(), recentCall.getSubCategoryId()));
                    }
                });
            }
        });

        setContentShown(true);
    }

    private void setProfileDescription(String description) {
        if (description != null
                && !description.equals("null")
                && description.length() > 0) {
            profileDescription.setText(description);
        } else {
            profileDescription.setText(getString(R.string.empty));
        }
    }

    private void setProfileUsername(String username) {
        CabinetActivity activity = (CabinetActivity) getActivity();
        if (activity != null
                && username != null
                && !username.equals("null")
                && username.length() > 0) {
            activity.getTitleBar().setTile(username.toUpperCase());
        }
    }

    private void setUserAvatar(UserInfo userInfo) {
        if (userInfo.getAvatarURL() != null) {


            //imgLoader.DisplayImage(Constants.API + userInfo.getAvatarURL(), R.drawable.avatar_icon, clientAvatar);

            Log.d("AVATAR_CABINET", "Avatar: " + userInfo.getAvatarURL());


            //imageLoader.loadImage(Constants.API + userInfo.getAvatarURL(), new SimpleImageLoadingListener() {
            imageLoader.loadImage(Constants.API + userInfo.getAvatarURL(), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (loadedImage != null) {

                        //RoundedAvatarDrawable drawable = new RoundedAvatarDrawable(loadedImage);
                        applyAvatar(loadedImage);
                    }
                }
            });
        }
    }

    @Override
    protected void applyAvatar(Bitmap bitmapAvatar) {
        if (bitmapAvatar != null) {



            clientAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            clientAvatar.setImageBitmap(bitmapAvatar);
            executorAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            executorAvatar.setImageBitmap(bitmapAvatar);
        } else {
            clientAvatar.setImageResource(R.drawable.avatar_icon);
            executorAvatar.setImageResource(R.drawable.avatar_icon);
        }
    }

    /*private void applyAvatarDrawable(Drawable drawable) {
        if (drawable != null) {



            clientAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            clientAvatar.setImageDrawable(drawable);
            executorAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            clientAvatar.setImageDrawable(drawable);
        } else {
            clientAvatar.setImageResource(R.drawable.avatar_icon);
            executorAvatar.setImageResource(R.drawable.avatar_icon);
        }
    } */

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }


    @Deprecated
    private void setBusyStatus(boolean isBusy) {
        StateListDrawable stateListDrawable = (StateListDrawable) currentStateSwitcher.getBackground();
        LayerDrawable layerDrawable = (LayerDrawable) stateListDrawable.getCurrent();
        GradientDrawable grayDrawable = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.gray_background);
        if (isBusy) {
            currentStateSwitcher.setText(getString(R.string.current_state_busy));
            grayDrawable.setColor(getResources().getColor(R.color.state_busy));
            busyMark.setImageResource(R.drawable.ic_circle_red_small);
        } else {
            currentStateSwitcher.setText(getString(R.string.current_state_free));
            grayDrawable.setColor(getResources().getColor(R.color.state_free));
            busyMark.setImageResource(R.drawable.ic_circle_green_small);
        }
    }


    public void postAvatarToServer(Bitmap photo) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        byte[] byteArray = bytes.toByteArray();
        String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

        Log.d(TAG, "Base64: " + base64);

        User user = User.create(getActivity());
        ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());
        handler.postUserAvatar(user.getToken(), "jpeg", base64);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            applyAvatar(photo);
            postAvatarToServer(photo);
            if (auth_dialog != null) auth_dialog.dismiss();
        }

        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap photo = BitmapFactory.decodeFile(picturePath);
            applyAvatar(photo);
            postAvatarToServer(photo);
            if (auth_dialog != null) auth_dialog.dismiss();

        }

    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }



    public Bitmap scaleDownBitmap(Bitmap originalImage, int width, int height) {
        Bitmap background = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        float originalWidth = originalImage.getWidth(), originalHeight = originalImage.getHeight();
        Canvas canvas = new Canvas(background);
        float scale = width / originalWidth;
        float xTranslation = 0.0f, yTranslation = (height - originalHeight * scale) / 2.0f;
        Matrix transformation = new Matrix();
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scale, scale);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(originalImage, transformation, paint);
        return background;
    }

    @Deprecated
    public View.OnClickListener currentStateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            /*if (mUserInfo == null) return;
            if (mUserInfo.isBusy()) {
                mUserInfo.setBusy(false);

                ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());
                handler.postStatus(User.create(getActivity()).getToken(), false);
                setBusyStatus(false);
            } else {
                setBusyStatus(true);
                mUserInfo.setBusy(true);

                ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());
                handler.postStatus(User.create(getActivity()).getToken(), true);
            } */
        }
    };

    @Override
    public String getTitle() {
        return getString(R.string.cabinet).toUpperCase();
    }

    public static CabinetFragment newInstance(/*UserInfo ui, boolean asList*/) {
        CabinetFragment fragment = new CabinetFragment();
        Bundle args = new Bundle();
        fragment.isHomeButtonDisabled = false                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          ;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onInfoIconClick() {
        User user = User.create(getActivity());
        if (user.isExecutor()) {
            new ExecutorInfoDialog(getActivity()).show();
        } else {
            new ProfileInfoDialog(getActivity()).show();
        }
    }

}
