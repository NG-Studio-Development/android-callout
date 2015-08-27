package su.whs.call.helper.url.listeners;

import org.json.JSONObject;

import java.util.EventListener;

public interface SuccessJsonListener extends EventListener {
    void success(JSONObject responseJson);
}