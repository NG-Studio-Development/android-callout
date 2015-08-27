package su.whs.call.helper.task.listeners;


import java.util.EventListener;

public interface AsyncResultProvider extends EventListener {
    Object asyncWork();

    void after(Object result);
}



