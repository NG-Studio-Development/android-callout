package su.whs.call.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import su.whs.call.R;

public class InfoDialog extends Dialog {

    private String title;

    public InfoDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.translucent_black);
        setContentView(R.layout.dialog_info);
        ((TextView) findViewById(R.id.tvDialogInfo)).setText(title);
        findViewById(R.id.dialogLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
