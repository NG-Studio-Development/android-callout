package su.whs.call.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CallsExpert implements Serializable {

    int count;
    int month;
    int year;

    CallsExpert(JSONObject json) throws JSONException {
        count = json.optInt("count", 0);
        month = json.optInt("month", 0);
        year = json.optInt("year", 0);
    }

    CallsExpert(int count, int month, int year) {
        this.count = count;
        this.month = month;
        this.year = year;
    }

    public int getCount() {
        return count;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    /*public static List<CallsExpert> createListDEBUG() {

        List<CallsExpert> callsList = new ArrayList<CallsExpert>();

        callsList.add(new CallsExpert(4,4,2014));
        callsList.add(new CallsExpert(2,5,2014));
        callsList.add(new CallsExpert(8,6,2014));
        callsList.add(new CallsExpert(10,7,2014));
        callsList.add(new CallsExpert(9,8,2014));

        callsList.add(new CallsExpert(4,4,2015));
        callsList.add(new CallsExpert(2,5,2015));
        callsList.add(new CallsExpert(8,6,2015));
        callsList.add(new CallsExpert(10,7,2015));
        callsList.add(new CallsExpert(9,8,2015));

        return callsList;
    } */

    public static int getCountYear(List<CallsExpert> list) {
        return getListOfYears(list).size();
    }

    public static List<Integer> getListOfYears(List<CallsExpert> listCalls) {
        List<Integer> list = new ArrayList<Integer>();

        for (CallsExpert call : listCalls) {

            if(!list.contains(call.getYear()))
                list.add(call.getYear());
        }

        return list;
    }
}
