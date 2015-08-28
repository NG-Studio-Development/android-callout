package su.whs.call;

//import su.whs.call.crashreport.ReportHandler;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.crittercism.app.Crittercism;

import su.whs.call.models.SubCategory;
import su.whs.call.net.ConnectionHandler;

public class CallApp extends Application {
    private SubCategory mActiveSubCategory = null;

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

}
