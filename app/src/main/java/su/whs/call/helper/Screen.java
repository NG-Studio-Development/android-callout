package su.whs.call.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.View;

/**
 * Created by Infinity-PC on 28.01.2015.
 */
public class Screen {

    public static Point getDisplayPointSize(Context context) {
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(size);
        } else {
            display.getSize(size);
        }

        return size;
    }

    public static int getDisplayWidth(Context context) {
        return  Screen.getDisplayPointSize(context).x;
    }

    public static int getDisplayHeight(Context context) {
        return  Screen.getDisplayPointSize(context).y;
    }


}
