package su.whs.call.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;

import su.whs.call.R;
import su.whs.call.net.ConnectionHandler;
import su.whs.call.register.User;

public abstract class BaseCabinetFragment extends BaseFragment {

    private static final String TAG = "BASE_CABINET";

    protected static final int CAMERA_REQUEST = 1888;
    protected static final int GALLERY_REQUEST = 1889;


    protected Dialog auth_dialog;

    public View.OnClickListener avatarSelectListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            onSelectAvatar();
        }

    };

    //ChangeAvatarListener changeListener = null;

    protected void onSelectAvatar() {

        //this.changeListener = changeListener;

        Button sourceCameraBtn;
        Button sourceGalleryBtn;
        Button removeBtn;

        auth_dialog = new Dialog(getActivity());
        auth_dialog.setContentView(R.layout.avatar_dialog);

        sourceCameraBtn = (Button) auth_dialog.findViewById(R.id.source_camera);
        sourceGalleryBtn = (Button) auth_dialog.findViewById(R.id.source_gallery);
        removeBtn = (Button) auth_dialog.findViewById(R.id.source_remove);

        sourceCameraBtn.setOnClickListener(avatarFromCameraListener);
        sourceGalleryBtn.setOnClickListener(avatarFromGalleryListener);
        removeBtn.setOnClickListener(removeAvatarListener);

        auth_dialog.setTitle(getString(R.string.select_avatar_source));
        auth_dialog.show();

    }


    /*public void postAvatarToServer(Bitmap photo) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        byte[] byteArray = bytes.toByteArray();
        String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

        Log.d(TAG, "Base64: " + base64);

        User user = User.create(getActivity());
        ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());
        handler.postAvatar(user.getToken(), "jpeg", base64);

        if ( changeListener != null )
            changeListener.onChange();
    } */


    public String toBase64(Bitmap photo) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        byte[] byteArray = bytes.toByteArray();
        String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

        Log.d(TAG, "Base64: " + base64);

        return base64;

        /*User user = User.create(getActivity());
        ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());
        handler.postAvatar(user.getToken(), "jpeg", base64);

        if ( changeListener != null )
            changeListener.onChange();*/
    }

    public View.OnClickListener avatarFromCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    };

    public View.OnClickListener avatarFromGalleryListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, GALLERY_REQUEST);
        }
    };

    public View.OnClickListener removeAvatarListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            User user = User.create(getActivity());
            if (user.isLoggedIn()) {
                ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());
                handler.removeAvatar(user.getToken());
                applyAvatar(null);
            }
            if (auth_dialog != null) auth_dialog.dismiss();
        }
    };


    protected abstract void applyAvatar(Bitmap bitmapAvatar);

}
