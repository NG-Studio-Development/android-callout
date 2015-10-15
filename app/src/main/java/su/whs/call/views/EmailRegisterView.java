package su.whs.call.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import su.whs.call.R;
import su.whs.call.api.ApiClient;
import su.whs.call.form.MainActivity;
import su.whs.call.fragments.RegisterFragment;
import su.whs.call.net.ConnectionHandler;

public class EmailRegisterView extends LinearLayout {
    private EditText mName;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mPasswordConfirm;
    private Button mButton;
    private RegisterFragment mRegisterFragment;
    private Context mContext;

    public EmailRegisterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        LayoutInflater.from(context).inflate(R.layout.register_form_email, this, true);

        mName = (EditText) findViewById(R.id.editRegisterName);
        mEmail = (EditText) findViewById(R.id.editRegisterEmail);
        mPassword = (EditText) findViewById(R.id.editRegisterPassword);
        mPasswordConfirm = (EditText) findViewById(R.id.editRegisterPasswordConfirm);
        mButton = (Button) findViewById(R.id.buttonRegisterEmail);

        if (!isInEditMode()) {
            mPasswordConfirm.setOnEditorActionListener(new OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        processRegistration();
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        return true;
                    }
                    return false;
                }
            });
        }

        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                processRegistration();
            }
        });
    }

    public void setParentFragment(RegisterFragment fragment) {
        mRegisterFragment = fragment;
    }

    public void processRegistration() {

        if (!isValidEnterData(true))
            return;

        ConnectionHandler.getInstance(getContext()).register(
                mEmail.getText().toString(),
                mPassword.getText().toString(),
                mPasswordConfirm.getText().toString(),
                mName.getText().toString(), new ApiClient.LoginListener() {
                    @Override
                    public void complete() {
                        if (mRegisterFragment != null && mRegisterFragment.getActivity() != null) {

                            MainActivity.setCurrentTabByTagHack();

                          //  MainActivity mainActivity = (MainActivity) mRegisterFragment.getActivity();
                           // mainActivity.setCurrentTabByTag("login");
                        }
                    }

                    @Override
                    public void fail(String message) {
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void inetFail() {
                        Toast.makeText(getContext(), "FAIL", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    protected boolean isValidEnterData(boolean isShowToast) {

        String message = null;
        boolean isValid = true;

        if (mName.getText().toString().isEmpty() ||
                mEmail.getText().toString().isEmpty() ||
                mPassword.getText().toString().isEmpty() ||
                mPasswordConfirm.getText().toString().isEmpty() ) {

            message = mContext.getString(R.string.warning_you_must_enter_all_fields);
            isValid = false;
        } else if (!mPassword.getText().toString().equals(mPasswordConfirm.getText().toString() )) {
            message = mContext.getString(R.string.warning_password_mismatch);
            isValid = false;
        } else if(!isValidEmail(mEmail.getText())) {
            message = mContext.getString(R.string.warning_no_valid_email);
            isValid = false;
        }

        if (!isValid && isShowToast)
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();

        return isValid;
    }


    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

}
