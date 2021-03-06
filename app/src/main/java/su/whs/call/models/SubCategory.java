package su.whs.call.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SubCategory implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7209335233455430879L;
	private String mName;
	private List<UserInfo> mUsers = new ArrayList<UserInfo>();
    private int mId;
	
	public SubCategory(JSONObject json) {
		mName = json.optString("sub_category");
		mId = json.optInt("sub_category_id");
        if (json.has("users")) {
			JSONArray users;
			try {
				users = json.getJSONArray("users");
			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}
			for(int i=0; i<users.length(); i++) {
				JSONObject user;
				try {
					user = users.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
				try {
					mUsers.add(new UserInfo(user));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public List<UserInfo> getUsers() { return mUsers; }
	public String getName() { return mName; }
    public int getId() { return mId; }
}
