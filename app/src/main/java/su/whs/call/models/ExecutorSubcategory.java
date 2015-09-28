package su.whs.call.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by featZima on 10.09.2014.
 */
public class ExecutorSubcategory implements Serializable {
    private final static SimpleDateFormat jsonDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private int id;
    private String name;
    private ArrayList<SubCategoryReview> reviews;
    private String avatar;
    private String dateFrom;
    private String dateTo;
    private String description;
    private int countCall = 0;
    private boolean status = true;
    private List<CallsExpert> callsList;

    public ExecutorSubcategory(JSONObject json) throws JSONException {
        reviews = new ArrayList<SubCategoryReview>();
        callsList = new ArrayList<CallsExpert>();
        id = json.getInt("sub_category_id");
        name = json.getString("sub_category_name");
        dateFrom = json.getString("date_from");
        dateTo = json.getString("date_to");
        description = json.getString("description");
        countCall = json.getInt("count_call");
        avatar = json.getString("avatar");
        status = json.getBoolean("status");

        JSONArray reviewsJson = json.getJSONArray("reviews");
        for (int i = 0; i < reviewsJson.length(); i++) {
            reviews.add(new SubCategoryReview(reviewsJson.getJSONObject(i)));
        }

        JSONArray callsJson = json.getJSONArray("calls");
        for (int i = 0; i < callsJson.length(); i++) {
            callsList.add(new CallsExpert(callsJson.getJSONObject(i)));
        }
    }


    public int getId() {
        return id;
    }

    public int getCountCall() {
        return countCall;
    }

    public boolean getStatus() { return status; }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getDescription() {
        return description;
    }

    public int getReviewCount() {
        return reviews.size();
    }

    public List<CallsExpert> getCallsList() {
        return callsList;
    }

    public String getLastDays() {
        try {
            Date to = jsonDateFormat.parse(dateTo);
            Calendar c = Calendar.getInstance();
            Date now = c.getTime();
            return String.valueOf(getDaysBetweenDates(now, to));
        } catch (Exception e) {
            return "";
        }
    }

    private int getDaysBetweenDates(Date date1, Date date2)
    {
        //if date2 is more in the future than date1 then the result will be negative
        //if date1 is more in the future than date2 then the result will be positive.

        return (int)((date2.getTime() - date1.getTime()) / (1000*60*60*24l));
    }

    public double getRate() {
        double totalRate = 0;
        for (SubCategoryReview review : reviews) {
            totalRate += review.getRate();

        }

        return reviews.size() > 0 ? totalRate / reviews.size() : 0;
    }

    public String getName() {
        return name;
    }

    public ArrayList<SubCategoryReview> getReviews() {
        return reviews;
    }
}
