package su.whs.call.views;

import su.whs.call.Constants;
import su.whs.call.R;
import su.whs.call.api.ApiClient;
import su.whs.call.form.CabinetActivity;
import su.whs.call.form.MainActivity;
import su.whs.call.fragments.BaseFragment;
import su.whs.call.fragments.CabinetFragment;
import su.whs.call.net.ConnectionHandler;
import su.whs.call.register.User;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;

public class EmailLoginView extends LinearLayout {

    private EditText editLoginEmail;
    private EditText editPasswordEmail;
    private Button buttonLoginEmail;
    private TextView tvLostPassword;

    private static final String TAG_LOGIN = "login";

    public EmailLoginView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        LayoutInflater.from(context).inflate(R.layout.login_form_email, this, true);

        editLoginEmail = (EditText) findViewById(R.id.editLoginEmail);
        editPasswordEmail = (EditText) findViewById(R.id.editPasswordEmail);
        buttonLoginEmail = (Button) findViewById(R.id.buttonLoginEmail);
        tvLostPassword = (TextView) findViewById(R.id.tvLostPassword);

        //editLoginEmail.setText("frederikos@mail.ru");
        //editPasswordEmail.setText("qwertyui");
        //editLoginEmail.setText("olgamakarova@mail.ru");
        //editPasswordEmail.setText("qwerty12345");

        tvLostPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = new EditText(getContext());
                new AlertDialog.Builder(getContext())
                        .setMessage("Пароль будет отправлен на указанный адрес электронной почты")
                        .setView(input)
                        .setPositiveButton(getContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                new AsyncTask<Void, Void, String>() {
                                    @Override
                                    protected String doInBackground(Void... voids) {
                                        HttpClient httpClient = new DefaultHttpClient();
                                        try {
                                            String inputText = URLEncoder.encode(input.getText().toString(), "utf-8");
                                            HttpResponse response = httpClient.execute(new HttpGet(Constants.API + "/api/reset/password?email=" + inputText));
                                            return EntityUtils.toString(response.getEntity());
                                        } catch (Exception ex) {

                                        }
                                        return "false";
                                    }

                                    @Override
                                    protected void onPostExecute(String aVoid) {
                                        if (aVoid.contains("true")) {
                                            Toast.makeText(getContext(), "Пароль успешно выслан", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getContext(), "Не удалось выслать пароль", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }.execute();
                            }
                        }).setNegativeButton(getContext().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();
            }
        });

        buttonLoginEmail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectionHandler handler = ConnectionHandler.getInstance(context);
                String email = editLoginEmail.getText().toString();
                String password = editPasswordEmail.getText().toString();
                handler.login(email, password, new ApiClient.LoginListener() {
                    @Override
                    public void complete() {
                        Context context = getContext();


                        //CabinetFragment.fragment.openFragment(CabinetFragment.newInstance());


                        /*CabinetActivity activity = (CabinetActivity) context;
                        activity.setupTab(TAG_LOGIN, R.drawable.ic_tab_ic_tab_user, R.string.cabinet, CabinetFragment.class);

                        CabinetActivity.mTabHost.setCurrentTab(CabinetActivity.mTabHost.getTabWidget().getTabCount() - 1);
*/
                        MainActivity.mTabHost.setCurrentTab(5);
                        MainActivity.updateTabs();
                        //if (context instanceof MainActivity) {

                        //mainActivity.setCurrentTabByTagHack();
                       // }
                    }

                    @Override
                    public void fail(String message) {
                        Toast.makeText(context, "fail: " + message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void inetFail() {
                        Toast.makeText(context, "inetFail", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
