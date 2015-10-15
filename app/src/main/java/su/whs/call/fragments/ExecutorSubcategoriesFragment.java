package su.whs.call.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import su.whs.call.Constants;
import su.whs.call.R;
import su.whs.call.adapters.ExecutorSubcategoriesAdapter;
import su.whs.call.dialog.InfoDialog;
import su.whs.call.form.CabinetActivity;
import su.whs.call.form.MainActivity;
import su.whs.call.models.ExecutorSubcategory;
import su.whs.call.models.RegisteredYear;
import su.whs.call.models.UserExtra;
import su.whs.call.models.UserInfo;
import su.whs.call.net.ConnectionHandler;
import su.whs.call.register.User;

public class ExecutorSubcategoriesFragment extends BaseCabinetFragment {

    private static final String SUBCATEGORIES_ARGS = "subcats";
    private static final String USERINFO_ARGS = "userinfo";

    private ListView mList;

    private static UserInfo mUserInfo;
    private static List<ExecutorSubcategory> mSubcategories;
    private static ExecutorSubcategoriesAdapter adapter;
    private ExecutorSubcategory selectedSubcategory;
    private ConnectionHandler.ChangeAvatarListener changeAvatarListener = null;
    private ProgressDialog progressDialog;

    private static ExecutorSubcategoriesFragment mInstance = null;
    private RegisteredYear mYear;

    public static ExecutorSubcategoriesFragment newInstance(ArrayList<ExecutorSubcategory> data, UserInfo userInfo) {
        Bundle arguments = new Bundle();
        if (data != null && userInfo != null) {
            arguments.putSerializable(SUBCATEGORIES_ARGS, data);
            arguments.putSerializable(USERINFO_ARGS, userInfo);
        }
        ExecutorSubcategoriesFragment f = new ExecutorSubcategoriesFragment();
        f.setArguments(arguments);
        mInstance = f;

        return f;
    }


    public static ExecutorSubcategoriesFragment getInstanceFromPool() {

        if (mInstance == null)
            mInstance = new ExecutorSubcategoriesFragment();

        return mInstance;
    }

    public static View clickedView;


    final ExecutorSubcategoriesAdapter.BtnClickListener executorAdapterListener = new ExecutorSubcategoriesAdapter.BtnClickListener() {
        @Override
        public void onReviewsClick(ExecutorSubcategory subcategory) {
            openFragment(SubcategoryReviewsFragment.newInstance(subcategory.getReviews()));
        }

        @Override
        public void onDescriptionClick(View clickedView, ExecutorSubcategory subcategory) {
            ExecutorSubcategoriesFragment.clickedView = clickedView;
            openFragment(ExecutorEditDescriptionFragment.newInstance(subcategory));
        }

        public void onCountCallClick(ExecutorSubcategory subcategory) {
            if (subcategory.getCallsList().size() != 0)
                openFragment( CallsFragment.newInstance( subcategory.getCallsList() ) );
            else
                Toast.makeText(getActivity(), getString(R.string.you_not_have_calls), Toast.LENGTH_LONG).show();
        }

        public void onChangeState( ExecutorSubcategory subcategory ) {
            ConnectionHandler handler = ConnectionHandler.getInstance( getActivity() );
            handler.postStatus( User.create(getActivity()).getToken(), subcategory );
        }

        @Override
        public void onAvatarClick(final View view, ExecutorSubcategory subcategory) {

            selectedSubcategory = subcategory;
            progressDialog.setTitle(getString(R.string.loading));
            onSelectAvatar();


            setChangeAvatarListener(new ConnectionHandler.ChangeAvatarListener() {
                @Override
                public void onChange() {
                    selectedSubcategory.setPublished(false);
                    view.findViewById(R.id.tvModerationState).setVisibility(View.VISIBLE);

                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), getString(R.string.service_will_be_published), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError() {

                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), getString(R.string.loading_error), Toast.LENGTH_LONG).show();
                }
            });

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.subcategories_fragment, container, false);

        mList = (ListView) v.findViewById(R.id.listView);

        final ConnectionHandler.OnExecutorCategoriesListener execotorCategoryListener =
                new ConnectionHandler.OnExecutorCategoriesListener() {
                    @Override
                    public void onCategoriesResponse(ArrayList<ExecutorSubcategory> subcategories) {
                        mSubcategories = subcategories;

                        adapter = new ExecutorSubcategoriesAdapter(getActivity(), mUserInfo, mSubcategories);
                        mList.setAdapter(adapter);
                        adapter.setBtnClickListener(executorAdapterListener);
                        setContentShown(true);
                    }
                };


        if ( mUserInfo == null || mSubcategories==null ) {
            //setContentShown(true);
            ( (CabinetActivity) getActivity() ).loadUserInformation(new ConnectionHandler.OnUserInfoListener() {
                @Override
                public void onUserInfoReady(List<UserExtra> allUsers, UserExtra ui) {
                    mUserInfo = ui.getUserInfo();

                    ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());
                    handler.queryExecutorCategories( User.create(getActivity()).getToken(), execotorCategoryListener );
                }

                @Override
                public void onFail() {
                    Toast.makeText(getActivity(), "Fail in loadUserInformation()", Toast.LENGTH_LONG).show();
                }
            } );
        } else {
            adapter.setBtnClickListener(executorAdapterListener);
        }

        if (mUserInfo != null)
            setProfileUsername(mUserInfo.getUserName());

        mContentView = v;
        return super.onCreateView(inflater, container, savedInstanceState);
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

    @Override
    public int getCustomHomeIconId() {
        return android.R.drawable.ic_menu_share;
    }


    @Override
    public boolean onHomeIconClick() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "?????????? ??????? ? ????? ?????????!\n\n\n" + Constants.URL_GOOGLE_PLAY);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.app_name)));

        return true;
    }

    public int getTotalCalls() {
        int total = 0;

        try {
            for(int i = 0; i < ConnectionHandler.jsonYears.length(); i++) {
                JSONObject object = ConnectionHandler.jsonYears.getJSONObject(i);
                JSONArray month = object.getJSONArray("months");


                for(int j = 0; j < month.length(); j++) {
                    JSONObject o = month.getJSONObject(j);
                    JSONArray calls =  o.getJSONArray("calls");
                    total += calls.length();

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return total;
    }



    @Override
    public void onResume() {
        super.onResume();

        MainActivity.btnCabinet.setSelected(true);

        if (mUserInfo != null && mSubcategories != null) {
            if (mList.getAdapter() == null) {
                adapter = new ExecutorSubcategoriesAdapter(getActivity(), mUserInfo, mSubcategories);
                mList.setAdapter(adapter);
                adapter.setBtnClickListener(executorAdapterListener);
                setContentShown(true);
            }
        }

        if (clickedView != null)
            clickedView.setVisibility(View.INVISIBLE);

    }

    @Override
    public String getTitle() {
        return getString(R.string.cabinet).toUpperCase();
    }

    public static Fragment restoreInstance() {
        return mInstance;
    }

    @Override
    public void onInfoIconClick() {
        InfoDialog infoDialog = new InfoDialog(getActivity());
        infoDialog.setTitle(getString(R.string.info_executor_subcategories));
        infoDialog.show();
    }


    public void setChangeAvatarListener(ConnectionHandler.ChangeAvatarListener changeAvatarListener) {
        this.changeAvatarListener = changeAvatarListener;
    }

    public ConnectionHandler.ChangeAvatarListener getChangeAvatarListener() {
        if ( changeAvatarListener == null )
            throw new Error("Avatar listener can not be null !!!");

        return changeAvatarListener;
    }

    @Override
    protected void applyAvatar(Bitmap bitmapAvatar) { /* Nothing to do */ }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            progressDialog.show();
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            applyAvatar(photo);
            postAvatarToServer(photo);
            if (auth_dialog != null) auth_dialog.dismiss();
        }

        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            progressDialog.show();
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




    protected void postAvatarToServer(Bitmap photo) {
        String base64 = toBase64(photo);

        User user = User.create(getActivity());
        ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());

        if (selectedSubcategory != null)
            handler.postCategoryAvatar(user.getToken(), selectedSubcategory.getId(), "jpeg", base64, getChangeAvatarListener());

    }


}