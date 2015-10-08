package su.whs.call.utils;

public class BackPressed {

    public interface OnBackPressedListener {
        public void onBackPressed();
    }


    private static OnBackPressedListener listener = null;


    public static void setOnBackPressedFragmentListener(BackPressed.OnBackPressedListener listener) {
        BackPressed.listener = listener;
    }

    public static OnBackPressedListener getListener() {
        return listener;
    }

}
