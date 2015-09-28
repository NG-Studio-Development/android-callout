package su.whs.call.register;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import su.whs.call.models.CallItem;
import su.whs.call.models.FavoriteItem;

/**
 * Created by featZima on 30.08.2014.
 */
public class User {

    private static final String PREFS_FILENAME = "preferences";

    private static final String NAME = "name";
    private static final String EXECUTOR = "executor";
    private static final String TOKEN = "token";
    private static final String SURNAME = "surname";
    private static final String LOGGEDIN = "loggedin";
    private static final String FAVORITES = "favorites";
    private static final String CALLS = "calls";

    private SharedPreferences prefs;

    private User(Context context) {
        prefs = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
    }

    public static User create(Context context) {
        return new User(context);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(LOGGEDIN, false);
    }

    public boolean isGuest() { return !this.isLoggedIn(); }

    public User setIsLoggedIn(boolean value) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(LOGGEDIN, value);
        edit.commit();
        return this;
    }

    public String getUserName() {
        return prefs.getString(NAME, null);
    }

    public boolean isExecutor() {
        return prefs.getBoolean(EXECUTOR, false);
    }

    public User setUserName(String value) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(NAME, value);
        edit.commit();
        return this;
    }


    public void logout() {
        setIsLoggedIn(false);
        setUserName("");
        setToken("");
        setExecutor(false);
    }

    public String getToken() {
        return prefs.getString(TOKEN, null);
    }

    public User setToken(String value) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(TOKEN, value);
        edit.commit();
        return this;
    }

    public String getStorageKey(String param) {
        return String.format("%s_%s", param, this.getUserName());
    }

    public boolean addExecutorToFavorites(String subcategoryTitle, int subcategoryId, int userId) {
        if (this.isLoggedIn() && !isFavoriteExecutive(subcategoryId, userId)) {
            List<FavoriteItem> favorites = getFavorites();
            favorites.add(new FavoriteItem(subcategoryTitle, subcategoryId, userId));
            setFavorites(favorites);
            return true;
        }
        return false;
    }

    public List<String> filterForIds(String[] strings) {
        List<String> clearList = new ArrayList<String>();
        for (int i = 0; i < strings.length; i++) {
            try {
                int id = Integer.valueOf(strings[i]);
                clearList.add(String.valueOf(id));
            } catch (NumberFormatException e) { }
        }
        return clearList;
    }

    public boolean removeExecutorFromFavorites(int subCategoryId, int userId) {
        if (this.isLoggedIn()) {
            // remove from favorites executor in subcategory
            List<FavoriteItem> favorites = getFavorites();
            for (Iterator<FavoriteItem> iter = favorites.listIterator(); iter.hasNext(); ) {
                FavoriteItem item = iter.next();
                if (item.subCategoryId == subCategoryId && item.userId == userId) {
                    iter.remove();
                }
            }
            setFavorites(favorites);
            return true;
        }
        return false;
    }

    public boolean isFavoriteExecutive(int subCategoryId, int userId)
    {
        if (this.isLoggedIn()) {
            List<FavoriteItem> favorites = getFavorites();
            for (Iterator<FavoriteItem> iter = favorites.listIterator(); iter.hasNext(); ) {
                FavoriteItem item = iter.next();
                if (item.subCategoryId == subCategoryId && item.userId == userId) {
                    return true;
                }
            }
            setFavorites(favorites);
        }
        return false;
    }

    /**
     * Get favorites list for current user from preferences
     *
     * @return Favorites list for current user
     */
    public List<FavoriteItem> getFavorites() {
        // get special key for current user
        String storageKey = this.getStorageKey(FAVORITES);

        // get favorites list from preferences
        Gson gson = new Gson();
        List<FavoriteItem> favorites;
        String json = prefs.getString(storageKey, "");
        if (json.isEmpty()) {
            favorites = new ArrayList<FavoriteItem>();
        } else {
            Type listType = new TypeToken<ArrayList<FavoriteItem>>() {}.getType();
            favorites = gson.fromJson(json, listType);
        }

        return favorites;
    }

    private void setFavorites(List<FavoriteItem> favorites) {
        // get special key for current user
        String storageKey = this.getStorageKey(FAVORITES);

        // save favorites to preferences
        Gson gson = new Gson();
        String json = gson.toJson(favorites);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(storageKey, json);
        editor.commit();
    }

    /**
     * Get recent calls for current user from preferences
     *
     * @return Recent calls list for current user
     */
    public List<CallItem> getRecentCalls() {
        // get special key for current user
        String storageKey = this.getStorageKey(CALLS);

        // get favorites list from preferences
        Gson gson = new Gson();
        List<CallItem> calls;
        String json = prefs.getString(storageKey, "");
        if (json.isEmpty()) {
            calls = new ArrayList<CallItem>();
        } else {
            Type listType = new TypeToken<ArrayList<CallItem>>() {}.getType();
            calls = gson.fromJson(json, listType);
        }

        Collections.sort(calls, new Comparator<CallItem>() {
            @Override
            public int compare(CallItem call, CallItem call2) {
                try {
                    return call2.getDate().compareTo(call.getDate());
                } catch (ParseException e) {
                    return 0;
                }
            }
        });


        Log.d("VISIVAY = ", "call = " + calls.size() );

        return calls;
    }

    public void postCall(int userId, String subCategoryTitle, int subCategoryId) {
        // get special key for current user
        String storageKey = this.getStorageKey(CALLS);

        List<CallItem> calls = getRecentCalls();
        calls.add(new CallItem(userId, subCategoryTitle, subCategoryId));

        // save favorites to preferences
        Gson gson = new Gson();
        String json = gson.toJson(calls);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(storageKey, json);
        editor.commit();
    }


    public User setExecutor(boolean executor) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(EXECUTOR, executor);
        edit.commit();
        return this;
    }
}