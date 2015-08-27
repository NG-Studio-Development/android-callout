package su.whs.call.helper;

public class Delay {

    public static void thread(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
        }
    }

}
