package su.whs.call.models;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by featZima on 17.09.2014.
 */
public class RecentCall implements Serializable {

    private final static SimpleDateFormat jsonDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    public static final SimpleDateFormat dateTextFormat = new SimpleDateFormat("dd.MM.yy");
    public static final SimpleDateFormat timeTextFormat = new SimpleDateFormat("HH:mm:ss");

    private String subCategoryName;
    private int subCategoryId;
    private Date createdAt;
    private String avatar;
    private int status;
    private int userId;
    private String userName;
    private String userState;
    private double rate;
    public String telephoneNumber;
    public String description;
    private String time;
    private String date;

    public RecentCall(JSONObject json) throws ParseException {
        subCategoryName = json.optString("sub_category_name");
        subCategoryId = json.optInt("sub_category_id", -1);
        createdAt = jsonDateFormat.parse(json.optString("created_at"));
        description = json.optString("description");
        avatar = json.optString("avatar");
        status = json.optInt("status", -1);
        userId = json.optInt("user_id", -1);
        userName = json.optString("user_name");
        userState = json.optString("user_state");
        rate = json.optDouble("user_rate");

        telephoneNumber = json.optString("telephone_number", "");
        description = json.optString("description", "");
    }

    public RecentCall( String subCategoryName,
             int subCategoryId,
             String avatar,
             int status,
             int userId,
             String userName,
             String userState,
             double rate,
             String telephoneNumber,
             String description,
             String date,
             String time)  {

        this.subCategoryName = subCategoryName;
        this.subCategoryId = subCategoryId;
        this.description = description;
        this.avatar = avatar;
        this.status = status;
        this.userId = userId;
        this.userName = userName;
        this.userState = userState;
        this.rate = rate ;
        this.date = date;
        this.time = time;

        this.telephoneNumber = telephoneNumber;
        this.description = description;

    }

    public int getUserId() {
        return userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUserName() {
        return userName;
    }

    public double getRate() {
        return rate;
    }

    public boolean isBusy() {
        return status == 1;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public String getSubCategoryTitle() {
        return subCategoryName;
    }

    public String getDateAsText() throws ParseException {
        return dateTextFormat.format(createdAt);
    }

    public String getTimeAsText() throws ParseException {
        return timeTextFormat.format(createdAt);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }
}
