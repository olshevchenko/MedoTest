package com.example.ol.medotest.Account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.ol.medotest.Constants;
import com.example.ol.medotest.PasswordActivity;

/**
 * Created by ol on 9/14/16.
 */
public class MyAuthenticator extends AbstractAccountAuthenticator {
  private Context context;
  public MyAuthenticator(Context context) {
    super(context);
    this.context = context;
  }

  @Override
  public Bundle addAccount(AccountAuthenticatorResponse response,
                           String accountType, String authTokenType,
                           String[] requiredFeatures, Bundle options)
      throws NetworkErrorException {
    final Intent intent = new Intent(context, PasswordActivity.class);
    intent.putExtra(Constants.Account.ARG_ACCOUNT_TYPE, accountType);
    intent.putExtra(Constants.Account.ARG_AUTH_TYPE, authTokenType);
    intent.putExtra(Constants.Account.ARG_IS_ADDING_NEW_ACCOUNT, true);
    intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
    final Bundle bundle = new Bundle();
    bundle.putParcelable(AccountManager.KEY_INTENT, intent);
    return bundle;
  }

  @Override
  public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
    return null;
  }

  @Override
  public Bundle addAccountFromCredentials(AccountAuthenticatorResponse response, Account account, Bundle accountCredentials) throws NetworkErrorException {
    return super.addAccountFromCredentials(response, account, accountCredentials);
  }

  @Override
  public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
    return null;
  }

  @Override
  public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
    return null;
  }

  @Override
  public String getAuthTokenLabel(String s) {
    return null;
  }

  @Override
  public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
    return null;
  }

  @Override
  public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
    return null;
  }
}
