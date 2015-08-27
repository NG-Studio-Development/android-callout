package su.whs.call.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import su.whs.call.models.UserInfo;

public class UserExtra implements Serializable {
    private UserInfo userInfo;
    private double averageRate;

    public UserExtra(JSONObject json) throws JSONException {
        averageRate = json.getDouble("average_rate");
        userInfo = new UserInfo(json.getJSONObject("user_info"));
        userInfo.mRate = (float) averageRate;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public double getAverageRate() {
        return averageRate;
    }
}
