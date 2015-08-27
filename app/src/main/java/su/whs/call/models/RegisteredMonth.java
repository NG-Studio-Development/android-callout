package su.whs.call.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by featZima on 10.09.2014.
 */
public class RegisteredMonth implements Serializable {

    private static String[] ENGLISH_MONTHS = new String[] {
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
    };

    private static String[] RUSSIAN_MONTH = new String[] {
            "Январь",
            "Февраль",
            "Март",
            "Апрель",
            "Май",
            "Июнь",
            "Июль",
            "Август",
            "Сентябрь",
            "Октябрь",
            "Ноябрь",
            "Декабрь"
    };

    public String name;
    public List<RegisteredCall> calls;

    public RegisteredMonth(JSONObject json) throws JSONException, ParseException {
        calls = new ArrayList<RegisteredCall>();
        name = json.getString("month");
        JSONArray jsonCalls = json.getJSONArray("calls");
        for (int i = 0; i < jsonCalls.length(); i++) {
            calls.add(new RegisteredCall(jsonCalls.getJSONObject(i)));
        }
    }

    public RegisteredMonth(int i) {
        calls = new ArrayList<RegisteredCall>();
        name = ENGLISH_MONTHS[i];
    }

    public int getTotalCalls() {
        return calls.size();
    }

    public int getId() {
        for (int i = 0; i < ENGLISH_MONTHS.length; i++) {
            if (ENGLISH_MONTHS[i].toLowerCase().equals(name.toLowerCase())) {
                return i;
            }
        }
        return -1;
    }

    public String getName() {
        return RUSSIAN_MONTH[getId()];
    }
}
