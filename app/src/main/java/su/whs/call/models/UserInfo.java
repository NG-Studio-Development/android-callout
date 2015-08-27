package su.whs.call.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;

public class UserInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -5514047699402234389L;
    private static final String TAG = "UserInfo";
    private String mPrimaryCategory = "n/a";
    private String[] mCategories = new String[]{};
    private String mUserName = "n/a";
    private String mGender = "n/a";
    private String mAvatarURL = null;
    private Date mDate;
    private int mId;
    private String mDescription;
    private boolean mStatus;
    private String mTelephone;

    public int getCallsCount() {
        return mCallsCount;
    }

    private int mCallsCount;
    private Date mSeen;
    public float mRate = 0f;
    private boolean mIsPerformer = false;
    private double mLatitude = 0;
    private double mLongitude = 0;

    public UserInfo(RecentCall call) {
        mAvatarURL = call.getAvatar();
        mUserName = call.getUserName();
        mStatus = call.isBusy();
        mRate = (float) call.getRate();
        mDescription = call.description;
        mTelephone = call.telephoneNumber;
        mId = call.getUserId();
    }

    public UserInfo(JSONObject json) throws JSONException {
        if (json != null) {
            Log.v(TAG, "init:" + json);
        }
        JSONObject userInfo = json;
        mUserName = userInfo.optString("name", "n/a");
        mId = userInfo.getInt("id");
        mCallsCount = userInfo.getInt("calls_count");
        mStatus = userInfo.getBoolean("status");
        mDescription = userInfo.getString("description");
        mTelephone = userInfo.optString("telephone_number", "");
        try {
            mLatitude = Double.valueOf(userInfo.getString("lattitude").replace(",", "."));
            mLongitude = Double.valueOf(userInfo.getString("longitude").replace(",", "."));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error on parsing geo coordinates");
            Log.e(TAG, "Current json value - " + json);
        }

        if (userInfo.has("avatar")) {
            mAvatarURL = userInfo.optString("avatar", null);
            if (mAvatarURL != null && mAvatarURL.contains("{")) {
                JSONObject avatar = userInfo.optJSONObject("avatar");
                if (avatar != null && avatar.has("url")) {
                    mAvatarURL = avatar.getString("url");
                }
            }
        }

        mRate = (float) userInfo.optDouble("rate", 0f);
        /*if (userInfo.has("reviews")) {
            JSONObject reviews = userInfo.getJSONObject("reviews");
			mRate = (float)reviews.optDouble("average_rate", 0f);
		}*/
        mIsPerformer = userInfo.optString("state", "").equals("исполнитель");
        String ALT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
        SimpleDateFormat sdf = new SimpleDateFormat(
                ALT_DATE_TIME_FORMAT);
        try {
            mDate = sdf.parse(userInfo.optString("created_at", "2013-10-17'T'09:22:44Z"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mSeen = sdf.parse(userInfo.optString("updated_at", "2013-10-17'T'09:22:44Z"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPrimaryCategory() {
        return mPrimaryCategory;
    }

    public String[] getCategories() {
        return mCategories;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getAvatarURL() {
        return mAvatarURL;
    }

    public String getGender() {
        return mGender;
    }

    public Date getDate() {
        return mDate;
    }

    public Date getSeen() {
        return mSeen;
    }

    public float getRate() {
        return mRate;
    }

    public float getAverageRate() {
        return mRate;
    }

    public int getId() {
        return mId;
    }

    public boolean isBusy() {
        return mStatus;
    }

    public String getDescription() {
        return mDescription;
    }

    public boolean isClient() {
        return !mIsPerformer;
    }

    public boolean isExecutor() {
        return mIsPerformer;
    }

    public Location getLocation() {
        if (mLongitude == 0 && mLongitude == 0) {
            return null;
        } else {
            Location targetLocation = new Location("");
            targetLocation.setLatitude(mLatitude);
            targetLocation.setLongitude(mLongitude);
            return targetLocation;
        }
    }

    public void setBusy(boolean busy) {
        this.mStatus = busy;
    }

    public String getTelephone() {
        return mTelephone.replaceAll("[^0-9|\\+]", "");
    }
}
