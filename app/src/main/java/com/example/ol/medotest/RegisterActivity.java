package com.example.ol.medotest;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ol.medotest.Account.MyAccountManager;

import java.util.Random;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
  //for logging
  private static final String LOG_TAG = RegisterActivity.class.getName();

  private SmsManager smsManager = null;
  private Resources res = null;
  private String sendSmsAgain;
  private MyAccountManager mam;
  private AutoCompleteTextView etPhoneNumber = null;
  private TextView tvSmsSent = null;
  private Button btSendSms = null;
  private RelativeLayout rlSms = null;
  private EditText etSmsCode = null;
  private Button btConfirmSmsCode = null;

  private Random rndNumbers;
  private Handler h;
  private static int smsValidTime = 0;
  private int smsCode = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    smsManager = SmsManager.getDefault();
    rndNumbers = new Random();

    res = getResources();
    sendSmsAgain = res.getString(R.string.sendsms2_button_text);

    mam = MyAccountManager.getInstance(
        getApplicationContext(), res.getString(R.string.ACCOUNT_TYPE));

    tvSmsSent = (TextView) findViewById(R.id.tvSmsSent);
    btSendSms = (Button) findViewById(R.id.btSendSms);
    btSendSms.setOnClickListener(this);

    ArrayAdapter<String> numbersAdapter = new ArrayAdapter<>(this,
        android.R.layout.simple_dropdown_item_1line, mam.getAllNumbers());
    etPhoneNumber = (AutoCompleteTextView) findViewById(R.id.etPhoneNumber);
    if (etPhoneNumber != null) {
      etPhoneNumber.setAdapter(numbersAdapter);
      /// format input numbers
      etPhoneNumber.addTextChangedListener(new PhoneNumberTextWatcher(btSendSms));

      /// fill input field with number
      String phone_number = mam.getNumber(); /// got number currently (e.g. on LoginActivity) entered
      if (phone_number.length() > 0)
        etPhoneNumber.setText(phone_number);
      else
        etPhoneNumber.setHint(res.getString(R.string.prompt_phone_number)); /// just hint
    }

    rlSms = (RelativeLayout) findViewById(R.id.rlSms);

    btConfirmSmsCode = (Button) findViewById(R.id.btConfirmSmsCode);
    btConfirmSmsCode.setOnClickListener(this);
    etSmsCode = (EditText) findViewById(R.id.etSmsCode);
    if (etSmsCode != null) {
      /// unblock btConfirmSmsCode button when SMS code is non-empty
      etSmsCode.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {
          btConfirmSmsCode.setEnabled(true);
        }
      });
    }

    TextView tvUserAgreementLink1 = (TextView) findViewById(R.id.tvUserAgreementLink1);
    if (tvUserAgreementLink1 != null)
      tvUserAgreementLink1.setMovementMethod(LinkMovementMethod.getInstance());
    TextView tvUserAgreementLink2 = (TextView) findViewById(R.id.tvUserAgreementLink2);
    if (tvUserAgreementLink2 != null)
      tvUserAgreementLink2.setMovementMethod(LinkMovementMethod.getInstance());

    h = new Handler();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (smsValidTime > 0)
      /// have unfinished SMS code validity timer
      h.post(updateSmsCodeTimer); ///continue loop
  }

  @Override
  protected void onPause() {
    super.onPause();
    h.removeCallbacks(updateSmsCodeTimer); /// stop text update engine
    smsValidTime = 0;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btSendSms:
        mam.setNumber(etPhoneNumber.getText().toString()); /// store currently entered phone

        h.removeCallbacks(updateSmsCodeTimer); /// stop previous loops (if any)
        smsCode = generateSmsCode();

        smsManager.sendTextMessage(
            mam.getNumber(),
            null,
            res.getString(R.string.smscode_text, smsCode),
            null,
            null
        );

        /// prepare UI elems for SMS code input
        smsValidTime = Constants.SmsCode.TIMEOUT; /// init countdown timer
        rlSms.setVisibility(View.VISIBLE);
        etSmsCode.setText("");
        etSmsCode.requestFocus();
        btConfirmSmsCode.setEnabled(false);
        h.post(updateSmsCodeTimer); ///start text update engine

        if (btSendSms.getText().toString().equals(sendSmsAgain))
          ; /// already done
        else {
          /// 1'st time, redesign screen look
          btSendSms.setText(sendSmsAgain);
          btConfirmSmsCode.setVisibility(View.VISIBLE);
        }
        break;

      case R.id.btConfirmSmsCode:
        if (etSmsCode.getText().toString().equals(String.valueOf(smsCode))) {
          Intent intent = new Intent(this, PasswordActivity.class);
          startActivity(intent);
        } else {
          /// incorrect SMS code
          Informing.ErrorInfoDialogFragment eiDialogFragment =
              Informing.ErrorInfoDialogFragment.newInstance(
                  R.string.dlgSmsCodeInvalidTitle,
                  R.string.dlgSmsCodeInvalidMessage,
                  R.drawable.ic_error_black_24dp);
          eiDialogFragment.show(getFragmentManager(), "dialog");

          if (BuildConfig.BUILD_TYPE.contentEquals("debug")) {
            Toast toast = Toast.makeText(this, "code:" + String.valueOf(smsCode), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
          }
          etSmsCode.requestFocus();
        }
        break;

      default:
        break;
    }
  }

  /**
   * password validity timer loop code
   * refreshes text with timer
   * when timer's over, blocks corresponding UI elems for input
   */
  Runnable updateSmsCodeTimer = new Runnable() {
    public void run() {
      tvSmsSent.setText(getString(R.string.smssent_text, smsValidTime));
      if (tvSmsSent.getVisibility() != View.VISIBLE)
        tvSmsSent.setVisibility(View.VISIBLE);
      if (smsValidTime <=0) {
        /// time's over
        smsValidTime = 0; /// just in case, as a sign of stopped runnable
        rlSms.setVisibility(View.INVISIBLE); ///hide sms code input functionality
        btConfirmSmsCode.setEnabled(false);
        return;
      }
      smsValidTime -= Constants.SmsCode.UPDATE_TIME_DELTA;
      // repost itself
      h.postDelayed(updateSmsCodeTimer, Constants.SmsCode.UPDATE_TIME_DELTA*1000);
    }
  };

  private int generateSmsCode() {
    return Constants.SmsCode.MIN_CODE_VALUE +
        rndNumbers.nextInt(Constants.SmsCode.MAX_CODE_VALUE - Constants.SmsCode.MIN_CODE_VALUE);
  }
}
