package su.whs.call.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by featZima on 10.09.2014.
 */
public class SubCategoryReview implements Serializable {

    private int userId;
    private String userName;
    private String description;
    private double rate;

    public SubCategoryReview(JSONObject json) throws JSONException {
        userId = Integer.valueOf(json.getString("user_id"));
        userName = json.getString("name");
        description = json.getString("description");

        rate = json.getDouble("rate");
    }

    public double getRate() {
        return rate;
    }

    public String getUserName() {
        return userName;
    }

    public String getDescription() {
        return description;
    }
}
