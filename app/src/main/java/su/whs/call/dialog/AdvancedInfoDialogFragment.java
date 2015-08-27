package su.whs.call.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import su.whs.call.R;

public class AdvancedInfoDialogFragment extends DialogFragment {
    private String text;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(text)
               /*.setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // FIRE ZE MISSILES!
                   }
               })*/
               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public static AdvancedInfoDialogFragment newInstance(String text) {
        AdvancedInfoDialogFragment dialogFragment = new AdvancedInfoDialogFragment();
        dialogFragment.text = text != null ? text : "Without text";
        return dialogFragment;
    }
}