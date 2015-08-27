package su.whs.call.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import su.whs.call.R;

public class FavoritesFilterInfoDialog extends Dialog {

    public FavoritesFilterInfoDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.translucent_black);
        setContentView(R.layout.favorites_filter_dialog);
        findViewById(R.id.dialogLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
