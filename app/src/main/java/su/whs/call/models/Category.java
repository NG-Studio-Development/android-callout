package su.whs.call.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import su.whs.call.models.SubCategory;


public class Category implements Serializable {
    public String mName;
    private String mIconURL = null;
    private List<SubCategory> mSubCats = new ArrayList<SubCategory>();

    public Category() {
    }


    public Category(JSONObject json) {
        mName = json.optString("category_name");
        mIconURL = json.optString("category_icon");
        if (json.has("subcategories")) {
            JSONArray subcategories;
            try {
                subcategories = json.getJSONArray("subcategories");
            } catch (JSONException e) {
                return;
            }
            for (int j = 0; j < subcategories.length(); j++) {
                JSONObject subcat;
                try {
                    subcat = subcategories.getJSONObject(j);
                } catch (JSONException e) {
                    continue;
                }
                mSubCats.add(new SubCategory(subcat));
            }
        }
    }

    public List<SubCategory> getSubCategories() {
        return mSubCats;
    }

    public String getName() {
        return mName;
    }

    public String getIconURL() {
        return mIconURL;
    }
}
