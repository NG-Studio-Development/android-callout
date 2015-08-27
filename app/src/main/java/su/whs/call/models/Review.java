package su.whs.call.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Review implements Serializable {

    private String mUserName;
    private String mDescription;
    private double mRate;

	public Review(JSONObject json) {
		mUserName = json.optString("user_name");
		mDescription = json.optString("description");

		mRate = json.optDouble("rate");


	}

    public Review(String userName, String description, double rate) {
        mUserName = userName;
        mDescription = description;
        mRate = rate;
    }

    public String getUserName() {
        return mUserName != null ? mUserName.trim() : mUserName;
    }

    public String getDescription() {
        return mDescription != null ? mDescription.trim() : mDescription;
    }

    public double getRate() {
        return mRate;
    }
}
