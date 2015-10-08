package su.whs.call;

//import su.whs.call.crashreport.ReportHandler;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.crittercism.app.Crittercism;

import su.whs.call.models.SubCategory;
import su.whs.call.net.ConnectionHandler;

public class CallApp extends Application {
    private SubCategory mActiveSubCategory = null;
    private static CallApp instance = null;

    private ConnectionHandler mConnectionHandler = null;
    private LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub

        }
    };

    public LocationManager getLocationManager() {
        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        return lm;
    }

    public Location getLastKnownLocation(String provider) {
        return getLocationManager().getLastKnownLocation(provider);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mConnectionHandler = ConnectionHandler.getInstance(getApplicationContext());
        //ReportHandler.install(this, "frederikos@mail.ru");
        Crittercism.initialize(getApplicationContext(), "54063c3407229a6a92000005");

        instance = this;
    }

    public static CallApp getInstance(){
        if (instance == null)
            throw new Error("Application was not initialised");

        return instance;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mConnectionHandler = null;
    }

    public void setSubCategory(SubCategory subcat) {
        mActiveSubCategory = subcat;
    }

    public SubCategory getSubcategory() {
        return mActiveSubCategory;
    }


    public static Bitmap getRoundedBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    private static Location selectLocation = null;

    public static void setFindLocation(Location selectLocation) {
        CallApp.selectLocation = selectLocation;
    }

    public static Location getFindLocation() {
        return CallApp.selectLocation;
    }

}
