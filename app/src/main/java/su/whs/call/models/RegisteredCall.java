package su.whs.call.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by featZima on 10.09.2014.
 */
public class RegisteredCall implements Serializable {

    private final static SimpleDateFormat jsonDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public Date date;
    public int fromUserId;
    public String fromUserType;

    public RegisteredCall (JSONObject json) throws JSONException, ParseException {
        date = jsonDateFormat.parse(json.getString("date"));
        fromUserId = json.getInt("from_user_id");
        fromUserType = json.getString("from_user_type");
    }
}
