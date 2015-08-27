package su.whs.call.helper.url.listeners;


import java.util.EventListener;

public interface SuccessListener extends EventListener {
    void success(String response);
}