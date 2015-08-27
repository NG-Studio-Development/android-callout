package su.whs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class ErrorSender {

    private static final String EMAIL_TO = "frederikos@mail.ru";

    public static void sendErrorViaEmail(String errorString, Activity activity) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", EMAIL_TO, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Ошибка");
        emailIntent.putExtra(Intent.EXTRA_TEXT, errorString);
        try {
            activity.startActivity(Intent.createChooser(emailIntent, "Ошибка"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "Почтовый клиент не найден", Toast.LENGTH_LONG).show();
        }
    }

}
