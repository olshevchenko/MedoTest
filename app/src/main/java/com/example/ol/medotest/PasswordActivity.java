package com.example.ol.medotest;

import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.ol.medotest.Account.MyAccountManager;
import com.example.ol.medotest.Password.PasswordChecker;
import com.example.ol.medotest.Password.PasswordTextWatcher;

public class PasswordActivity extends AccountAuthenticatorActivity implements
    PasswordTextWatcher.onPasswordChangeListener {

  private Resources res = null;
  private MyAccountManager mam;
  private EditText etPassword1, etPassword2;
  private ImageView ivCheckDoneIcon1, ivCheckDoneIcon2;
  private Button btSendPassword;

  private boolean toShowPassword1 = false;
  private boolean toShowPassword2 = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    res = getResources();
    mam = MyAccountManager.getInstance(
        getApplicationContext(), res.getString(R.string.ACCOUNT_TYPE));

    Intent intent = getIntent();
    if (intent != null) {
      if (intent.getStringExtra(Constants.Account.ARG_ACCOUNT_TYPE) != null) {
        /// start PasswordActivity from AccountManager.addAccount()
        mam.storeAccount();
        Bundle data = new Bundle();
        data.putString(AccountManager.KEY_ACCOUNT_NAME, mam.getNumber());
        data.putString(AccountManager.KEY_ACCOUNT_TYPE, mam.getAccType());
        data.putString(AccountManager.KEY_AUTHTOKEN, mam.getAuthToken());
        final Intent result = new Intent();
        result.putExtras(data);
        setAccountAuthenticatorResult(data);
        setResult(RESULT_OK, result);
        finish();
      }
    }
    /// ordinary UI PasswordActivity start
    setContentView(R.layout.activity_password);
    ivCheckDoneIcon1 = (ImageView) findViewById(R.id.ivCheckDoneIcon1);
    etPassword1 = (EditText) findViewById(R.id.etPassword1);
    if (null != etPassword1) {
      /// link "show/hide password" ToggleButton with corresponding EditText
      PasswordChecker.setInputMask(etPassword1, toShowPassword1);
      ToggleButton tbtShowPassword1 = (ToggleButton) findViewById(R.id.tbtShowPassword1);
      if (null != tbtShowPassword1) {
        tbtShowPassword1.setChecked(toShowPassword1);
        tbtShowPassword1.setOnCheckedChangeListener(
            new CompoundButton.OnCheckedChangeListener() {
              @Override
              public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                toShowPassword1 = isChecked;
                PasswordChecker.setInputMask(etPassword1, isChecked);
              }
            }
        );
      }
      /// attach text watcher for the first ("master") password with the listener oneself
      etPassword1.addTextChangedListener(new PasswordTextWatcher(etPassword1, null, this));
    }

    ivCheckDoneIcon2 = (ImageView) findViewById(R.id.ivCheckDoneIcon2);
    etPassword2 = (EditText) findViewById(R.id.etPassword2);
    if (null != etPassword2) {
      PasswordChecker.setInputMask(etPassword2, toShowPassword2);
      ToggleButton tbtShowPassword2 = (ToggleButton) findViewById(R.id.tbtShowPassword2);
      if (null != tbtShowPassword2) {
        tbtShowPassword2.setChecked(toShowPassword2);
        tbtShowPassword2.setOnCheckedChangeListener(
            new CompoundButton.OnCheckedChangeListener() {
              @Override
              public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                toShowPassword2 = isChecked;
                PasswordChecker.setInputMask(etPassword2, isChecked);
              }
            }
        );
      }
      /// attach text watcher for the second ("servant") password with the listener oneself
      etPassword2.addTextChangedListener(new PasswordTextWatcher(etPassword2, etPassword1, this));
    }

    btSendPassword = (Button) findViewById(R.id.btSendPassword);
    btSendPassword.setEnabled(false);
    btSendPassword.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mam.setPassword(etPassword1.getText().toString());
//        PasswordActivity activity = (PasswordActivity) view.getContext();
        MyAccountManager.AddAccountRes res =
            mam.storeNumber(PasswordActivity.this, new MyAccountManagerCallback());
        switch (res) {
          case EQUAL_NUMBERS:
            Informing.ErrorInfoDialogFragment eiDialogFragment =
                Informing.ErrorInfoDialogFragment.newInstance(
                    R.string.dlgAccountAdditionWarningTitle,
                    R.string.dlgAccountAdditionWarningMessage,
                    R.drawable.ic_warning_black_24dp);
            eiDialogFragment.show(getFragmentManager(), "warndialog");
            break;
          case STARTING:
            ; /// succeeded - gonna wait onNewIntent(), then MyAccountManagerCallback calls
            break;
          case NOT_TO_STORE:
          case NULLED_NUMBER:
          default:
            /// might to finish now
            myFinish();
            break;
        } //switch
      }
    });
  }


  @Override
  protected void onPause() {
    super.onPause();
  }

  @Override
  public void onValidityChange(EditText et, boolean isValid) {
    if ((null == ivCheckDoneIcon1) || (null == ivCheckDoneIcon2))
      return;
    if (et == etPassword1)
      ivCheckDoneIcon1.setVisibility(isValid? View.VISIBLE : View.GONE);
    else {
      /// for 2'nd password, turn the icon ON if it's both valid & equal to master (see below)
      if (! isValid)
        ivCheckDoneIcon2.setVisibility(View.GONE);
    }
  }

  @Override
  public void onEqualityChange(EditText et, boolean isEqual) {
    if (et == etPassword2) {
      /// it's equal to master only if it's valid already
      ivCheckDoneIcon2.setVisibility(isEqual? View.VISIBLE : View.GONE);
    }
    btSendPassword.setEnabled(isEqual);
  }

  private void addingSucceededInfo() {
    Toast toast = Toast.makeText(this,
        getResources().getString(R.string.tstAccountAdditionSucceeded),
        Toast.LENGTH_LONG);
    toast.setGravity(Gravity.CENTER, 0, 0);
    toast.show();
  }

  private void myFinish() {
    Toast toast = Toast.makeText(this,
        getResources().getString(R.string.tstSeeYaNextTime),
        Toast.LENGTH_LONG);
    toast.setGravity(Gravity.CENTER, 0, 0);
    toast.show();
//    finish();
  }
  private class MyAccountManagerCallback implements AccountManagerCallback<Bundle> {
    @Override
    public void run(AccountManagerFuture<Bundle> result) {
      try {
        result.getResult();
      } catch (Exception ex) {
        Informing.ErrorInfoDialogFragment eiDialogFragment =
            Informing.ErrorInfoDialogFragment.newInstance(
                R.string.dlgAccountAdditionErrorTitle,
                R.string.dlgAccountAdditionErrorMessage,
                R.drawable.ic_error_black_24dp);
        eiDialogFragment.show(getFragmentManager(), "errdialog");
        return;
      }
      /// all ok
      addingSucceededInfo();
      myFinish();
    }
  }
}
