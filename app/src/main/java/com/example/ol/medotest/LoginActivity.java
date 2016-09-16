package com.example.ol.medotest;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.ol.medotest.Account.MyAccountManager;
import com.example.ol.medotest.Password.PasswordChecker;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
  //for logging
  private static final String LOG_TAG = LoginActivity.class.getName();

  private Resources res = null;
  private MyAccountManager mam;
  private EditText etPhoneNumber = null;
  private EditText etPassword;
  private boolean toShowPassword = false;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    res = getResources();

    mam = MyAccountManager.getInstance(
        getApplicationContext(), getResources().getString(R.string.ACCOUNT_TYPE));

    Button btLogin = (Button) findViewById(R.id.btLogin);
    btLogin.setOnClickListener(this);
    Button btRegister = (Button) findViewById(R.id.btRegister);
    btRegister.setOnClickListener(this);

    TextView tvForgetLink = (TextView) findViewById(R.id.tvForgetLink);
    if (tvForgetLink != null) {
      tvForgetLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
    if (etPhoneNumber != null) {
      /// format input numbers
      etPhoneNumber.addTextChangedListener(new PhoneNumberTextWatcher(btLogin));

      /// fill input field with number
      String phone_number = mam.getLastNumber(); /// got number from past sessions
      if (phone_number.length() > 0)
        etPhoneNumber.setText(phone_number); /// suggest to use previous one
      else
        etPhoneNumber.setHint(res.getString(R.string.prompt_phone_number)); /// just hint
    }


    etPassword = (EditText) findViewById(R.id.etPassword);
    if (null != etPassword) {
      PasswordChecker.setInputMask(etPassword, toShowPassword);
      ToggleButton tbtShowPassword = (ToggleButton) findViewById(R.id.tbtShowPassword);
      if (null != tbtShowPassword) {
        tbtShowPassword.setChecked(toShowPassword);
        tbtShowPassword.setOnCheckedChangeListener(
            new CompoundButton.OnCheckedChangeListener() {
              @Override
              public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                toShowPassword = isChecked;
                PasswordChecker.setInputMask(etPassword, isChecked);
              }
            }
        );
      }
    }

    SwitchCompat swRemember = (SwitchCompat) findViewById(R.id.swRemember);
    if (null != swRemember) {
      swRemember.setChecked(mam.isToStoreData());
      swRemember.setOnCheckedChangeListener(
          new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
              mam.setToStoreData(isChecked);
            }
          }
      );
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btLogin:
        attemptLogin(); /// check for password validity, then try to login
        break;
      case R.id.btRegister:
        mam.setNumber(etPhoneNumber.getText().toString()); /// store currently entered phone
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        break;
      default:
        break;
    }
  }

  private void attemptLogin() {
    /// incorrect password
    Informing.ErrorInfoDialogFragment eiDialogFragment =
        Informing.ErrorInfoDialogFragment.newInstance(
            R.string.dlgPasswordInvalidTitle,
            R.string.dlgPasswordInvalidMessage,
            R.drawable.ic_error_black_24dp);
    eiDialogFragment.show(getFragmentManager(), "dialog");
    etPassword.requestFocus();
  }

}
