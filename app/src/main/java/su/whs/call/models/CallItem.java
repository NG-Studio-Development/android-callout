package su.whs.call.models;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by featZima on 10.09.2014.
 */
public class CallItem implements Serializable {

    public static final SimpleDateFormat simple121Format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    public static final SimpleDateFormat dateTextFormat = new SimpleDateFormat("dd.MM.yy");
    public static final SimpleDateFormat timeTextFormat = new SimpleDateFormat("hh:mm");

    public int userId;
    public String subCategoryTitle;
    public int subCategoryId;
    public String callTime;

    public CallItem() {}

    public CallItem(int userId, String subCategoryTitle, int subCategoryId) {
        this.userId = userId;
        this.subCategoryTitle = subCategoryTitle;
        this.subCategoryId = subCategoryId;
        callTime = simple121Format.format(new Date());
    }

    public Date getDate() throws ParseException {
        return simple121Format.parse(callTime);
    }

    public String getDateAsText() throws ParseException {
        return dateTextFormat.format(getDate());
    }

    public String getTimeAsText() throws ParseException {
        return timeTextFormat.format(getDate());
    }


}
