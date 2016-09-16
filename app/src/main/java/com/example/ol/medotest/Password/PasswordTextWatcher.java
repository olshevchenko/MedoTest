package com.example.ol.medotest.Password;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by ol on 04.04.16.
 */

/**
 * Class-watcher for password input processing
 * executes validity & equality (to master password if one exists) checking
 * calls corresponding listener methods accordingly
 */
public class PasswordTextWatcher extends PasswordChecker
    implements TextWatcher {

  public interface onPasswordChangeListener {
    void onValidityChange(EditText et, boolean isValid);
    void onEqualityChange(EditText et, boolean isEqual);
  }

  //for logging
  private static final String LOG_TAG = PasswordTextWatcher.class.getName();

  private EditText etPassword;
  private EditText masterPassword = null; /// used for SECOND (servant) password entering only
  private onPasswordChangeListener listener;

  private String strPassword;
  private boolean isPasswordValid; /// current password validity check status: valid or not
  private boolean isPasswordValidStored; /// stored (from previous iteration) password status
  private boolean isStartWorking = true; /// is it start of the text watching execution?

  public PasswordTextWatcher (EditText etPassword, EditText masterPassword,
                              onPasswordChangeListener listener) {
    this.etPassword = etPassword;
    this.masterPassword = masterPassword;
    this.listener = listener;
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

  @Override
  public void onTextChanged (CharSequence s, int start, int before, int count) {
  }

  @Override
  public void afterTextChanged(Editable s) {
    strPassword = s.toString();
    isPasswordValid = checkInputValidity(strPassword);

    if (isStartWorking) {
      /// 1'st onTextChanged() call
      isStartWorking = false; /// not any more!
      listener.onValidityChange(etPassword, isPasswordValid); /// 1'st time, kick listener EVER
    } else {
      if (isPasswordValid != isPasswordValidStored)
        listener.onValidityChange(etPassword, isPasswordValid); /// call only when validity CHANGED
    }
    isPasswordValidStored = isPasswordValid; /// remember NEW status

    if ((isPasswordValid) && (null != masterPassword)) {
      /// it's a (syntactically) valid SECOND password (master's entered already)
      if (checkEquivalence(strPassword, masterPassword.getText().toString()))
        listener.onEqualityChange(etPassword, true); /// both passwords are the same
      else
        listener.onEqualityChange(etPassword, false);
    }
  }
}
