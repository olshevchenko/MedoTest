package com.example.ol.medotest;

import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.widget.Button;

import java.util.regex.Pattern;

/**
 * Created by ol on 04.04.16.
 */

/**
 * Class-watcher for phone number input processing
 * executes validity checking
 */
public class PhoneNumberTextWatcher extends PhoneNumberFormattingTextWatcher {
  //for logging
  private static final String LOG_TAG = PhoneNumberTextWatcher.class.getName();

  static final Pattern pat = Pattern.compile(Constants.PhoneNumber.REG_EX);

  private Button btAction; /// btLogin on Login page or btSendSms on Register one

  public PhoneNumberTextWatcher(Button btAction) {
    this.btAction = btAction;
  }

  @Override
  public void afterTextChanged(Editable s) {
    super.afterTextChanged(s);
    if (pat.matcher(s.toString()).matches())
      /// there's a correct well-formated phone number - we might use it now..
      btAction.setEnabled(true);
    else
      btAction.setEnabled(false);
  }
}
