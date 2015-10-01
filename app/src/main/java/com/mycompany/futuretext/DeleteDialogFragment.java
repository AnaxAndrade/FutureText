package com.mycompany.futuretext;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class DeleteDialogFragment extends DialogFragment {

    public static DeleteDialogFragment newInstance(int title) {

        DeleteDialogFragment ddf = new DeleteDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        ddf.setArguments(args);
        return ddf;
    }

    //makes a dialog fragment with a positive and negative button that call methods from DisplayMessageActivity when clicked
//    //Used to confirm deleting a pending message
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//
//        int title = getArguments().getInt("title");
//
//        return new AlertDialog.Builder(getActivity())
//                .setTitle(title)
//                .setPositiveButton(R.string.delete_pending_message,
//                        new DialogInterface.OnClickListener(){
//
//                            public void onClick(DialogInterface dialog, int button) {
//                                ((DisplayMessageActivity)getActivity()).deletePendingMessage();
//                            }
//                        })
//                .setNegativeButton(R.string.dont_delete_pending_message,
//                        new DialogInterface.OnClickListener(){
//
//                            public void onClick(DialogInterface dialog, int button) {
//                                ((DisplayMessageActivity)getActivity()).dontDeletePendingMessage();
//                            }
//                        })
//                .create();
//    }
}
