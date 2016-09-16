package com.example.ol.medotest;

/**
 * Created by ol on 07.04.16.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class Informing {

  /**
   * failure informing dialog
   */
  public static class ErrorInfoDialogFragment extends DialogFragment {

    public static ErrorInfoDialogFragment newInstance(
        int titleId, int messageId, int iconId) {
      ErrorInfoDialogFragment frag = new ErrorInfoDialogFragment();
      Bundle args = new Bundle();
      args.putInt("titleId", titleId);
      args.putInt("messageId", messageId);
      args.putInt("iconId", iconId);
      frag.setArguments(args);
      return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      int titleId = getArguments().getInt("titleId");
      int messageId = getArguments().getInt("messageId");
      int iconId = getArguments().getInt("iconId");

      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setTitle(titleId)
          .setMessage(messageId)
          .setIcon(iconId)
          .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
              dialog.cancel();
            }
          });
      return builder.create();
    }
  }
}