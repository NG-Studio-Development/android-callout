package su.whs.call.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by featZima on 10.09.2014.
 */
public class RegisteredYear implements Serializable {

    public String name;
    public List<RegisteredMonth> months;

    public RegisteredYear(JSONObject json) throws JSONException, ParseException {
        months = new ArrayList<RegisteredMonth>();
        name = json.getString("year");



        JSONArray jsonMonths = json.getJSONArray("months");
        for (int i = 0; i < jsonMonths.length(); i++) {
            months.add(new RegisteredMonth(jsonMonths.getJSONObject(i)));
        }

    }



    public List<RegisteredMonth> getMonths() {
        return months;
    }

    public void complete() {
        for (int i = 0; i < 12; i++) {
            RegisteredMonth month = getMonthById(i);
            if (month == null) {
                months.add(new RegisteredMonth(i));
            }
        }
        Collections.sort(months, new Comparator<RegisteredMonth>() {
            @Override
            public int compare(RegisteredMonth registeredMonth, RegisteredMonth registeredMonth2) {
                return registeredMonth.getId() - registeredMonth2.getId();
            }
        });
    }

    public RegisteredMonth getMonthById(int id) {
        for (RegisteredMonth month: months) {
            if (month.getId() == id) return month;
        }
        return null;
    }

    public String getYear() {
        return name;
    }
}
