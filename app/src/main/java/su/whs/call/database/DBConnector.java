package su.whs.call.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import su.whs.call.Constants;
import su.whs.call.models.FavoriteItem;
import su.whs.call.models.RecentCall;

/**
 * Created by ProgLife-1 on 01.04.2015.
 */
public class DBConnector  extends SQLiteOpenHelper {

    private SQLiteDatabase mDB;
    private String DATABASE_PATH;
    private String DATABASE_NAME;

    public DBConnector(Context context, String DATABASE_NAME) {
        super(context, DATABASE_NAME, null, 1);
        String packageName = context.getPackageName();
        this.DATABASE_NAME = DATABASE_NAME;
        DATABASE_PATH = String.format("//data//data//%s//databases//",
                packageName);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (mDB != null) {
            mDB = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME,
                    null, SQLiteDatabase.OPEN_READWRITE);

        } else {
            createTableFavorites(db);

        }
    }


    /*mAvatarURL = call.getAvatar();
        mUserName = call.getUserName();
        mStatus = call.isBusy();
        mRate = (float) call.getRate();
        mDescription = call.description;
        mTelephone = call.telephoneNumber;
        mId = call.getUserId();*/


    private void createTableFavorites(SQLiteDatabase db) {
        db.execSQL("create table " + Constants.TABLE_FAVORITES + " ("
                + "_id integer primary key autoincrement,"
                + "tkn text,"
                + "avatar text,"
                + "user_name text,"
                + "status int,"
                + "rate float,"
                + "description text,"
                + "telephone text,"
                + "subcategoty text,"
                + "subcategory_id int,"
                + "time text,"
                + "date text,"
                + "mid int" + ");");
    }


    public void setFavorites(String token, String avatar, String user_name,
                             int status, float rate, String description, String telephone,
                             String subcategoty, int subcategory_id, String time, String date, int mid) {


        SQLiteDatabase db = this.getReadableDatabase();


        ContentValues mContentValues = new ContentValues();
        mContentValues.put("tkn", token);
        mContentValues.put("avatar", avatar);
        mContentValues.put("user_name", user_name);
        mContentValues.put("status", status);
        mContentValues.put("rate", rate);
        mContentValues.put("description", description);
        mContentValues.put("telephone", telephone);
        mContentValues.put("subcategoty", subcategoty);
        mContentValues.put("subcategory_id", subcategory_id);
        mContentValues.put("time", time);
        mContentValues.put("date", date);
        mContentValues.put("mid", mid);


        long id = db.insert(Constants.TABLE_FAVORITES, null, mContentValues);

        Log.d("ididididididid", " idididid = " + id);
    }


    public void deleteFavarites(String token, int mid) {

        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(Constants.TABLE_FAVORITES, "tkn = '" + token + "' AND mid = '" + mid + "'", null);

    }

    public List<RecentCall>  getFavorites(String token) {

        List<RecentCall> item = new ArrayList<RecentCall>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM `" + Constants.TABLE_FAVORITES + "` WHERE `tkn` = '" + token + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {

            int subCategoryId = cursor.getInt(cursor.getColumnIndex("subcategory_id"));

            String subCategory = cursor.getString(cursor.getColumnIndex("subcategoty"));
            String avatar = cursor.getString(cursor.getColumnIndex("avatar"));
            int status =  cursor.getInt(cursor.getColumnIndex("status"));
            int userId = cursor.getInt(cursor.getColumnIndex("mid"));
            String userName = cursor.getString(cursor.getColumnIndex("user_name"));
            String userState = "off";
            double rate = cursor.getDouble(cursor.getColumnIndex("rate"));
            String telephoneNumber  = cursor.getString(cursor.getColumnIndex("telephone"));
            String description = cursor.getString(cursor.getColumnIndex("description"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String time = cursor.getString(cursor.getColumnIndex("time"));

            item.add(new RecentCall(subCategory, subCategoryId, avatar, status, userId, userName, userState, rate, telephoneNumber, description, date, time));
            cursor.moveToNext();
        }

        return item;
    }


    public boolean isFavorites(String token, int mid) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM `" + Constants.TABLE_FAVORITES + "` WHERE `tkn` = '" + token + "' AND `mid` = '" + mid + "'";
            Cursor cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();

            if(cursor.getCount() > 0) {
                return true;
        }

        return false;
    }


    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

    }
}