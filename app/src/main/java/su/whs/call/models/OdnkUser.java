package su.whs.call.models;

import org.json.JSONObject;

/**
 * Created by featZima on 11.09.2014.
 */
public class OdnkUser {

    private String uid;
    private String name;
    private String avatarUrl;

    public OdnkUser(JSONObject json) {
        uid = json.optString("uid");
        name = json.optString("name");
        avatarUrl = json.optString("pic_3");
    }


    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
