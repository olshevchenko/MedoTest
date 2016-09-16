package com.example.ol.medotest.Account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

/**
 * Created by ol on 9/5/16.
 */


/**
 * non thread-safe singleton (that's enough for calls from UI only)
 *
 * manages account record by AccountManager
 * uses past record from previous app session
 * stores new data (phone number just entered)
 */
public class MyAccountManager {

  public enum AddAccountRes {
    NOT_TO_STORE, /// flag 'toStoreData' == false
    NULLED_NUMBER, /// number to add is null (internal error)
    EQUAL_NUMBERS, /// new number to add is equal to the last stored one
    STARTING, /// checking succeeded, adding started
    SUCCESS /// :=)
  }

  private static MyAccountManager instance = null;

  private static boolean toStoreData = false;
  private static AccountManager am;

  private static String accType = "";
  private static String authToken = "";
  private static String authTokenType = "";

  /// from prev. session
  private static Account accLast = null;
  private static String accLastName = "";

  /// current, just entered
  private static String accNewName = "";
  private static String accNewPassword = "";

  private static void init(Context context, String accType) {
    if (null != instance)
      return;
    MyAccountManager.accType = accType;
    am = AccountManager.get(context);
    obtainLastAccData();
    toStoreData = (! accLastName.equals("")); /// store user if we already have account here..
    instance = new MyAccountManager();
  }

  public static MyAccountManager getInstance(Context context, String accType) {
    if (instance == null)
      init(context, accType);
    return instance;
  }

  private static void obtainLastAccData() {
    if (am == null)
      return;
    Account[] accounts = am.getAccountsByType(accType);
    if (accounts.length != 0) {
      accLast = accounts[accounts.length-1];
      /// get the phone from the last (=>freshest) account
      accLastName = accLast.name;
    }
  }

  public String getLastNumber() {
    return accLastName;
  }

  public String getNumber() {
    return accNewName;
  }

  public void setNumber(String number) {
    accNewName = number;
  }

  public String getAccType() {
    return accType;
  }

  public String getPassword() {
    return accNewPassword;
  }

  public void setPassword(String password) {
    accNewPassword = password;
  }

  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String authToken) {
    MyAccountManager.authToken = authToken;
  }

  /**
   * external call to initiate number (for account) addition
   * @param activity - AccountAuthenticatorActivity
   * @return false if no need to add account
   *         true if addition procedure is initiated
   */
  public AddAccountRes storeNumber(Activity activity, AccountManagerCallback<Bundle> accountManagerCallback) {
    if (!toStoreData)
      return AddAccountRes.NOT_TO_STORE;
    if (accNewName == null)
      return AddAccountRes.NULLED_NUMBER;
    if (accNewName.equals(accLastName))
      return AddAccountRes.EQUAL_NUMBERS; ///new account number is the same with the last (stored) one
    /// start to add account
    am.addAccount(accType, authToken, null, null, activity, accountManagerCallback, null);
    return AddAccountRes.STARTING;
  }

  /**
   * internal call from AccountAuthenticatorActivity to execute account adding
   */
  @SuppressWarnings("deprecation")
  public void storeAccount() {
    if (accLast == null) {
      /// initial account adding
      myAddAccountAndSetToken(accNewName, accType);
    } else {
      /// need to rename original account
      if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        /// remove, then add
        am.removeAccount(
            accLast,
            new AccountManagerCallback<Boolean>() {
              @Override
              public void run(AccountManagerFuture<Boolean> accountManagerFuture) {
                myAddAccountAndSetToken(accNewName, accType);
              }
            },
            null);
      } else {
        /// simply rename
        myRenameAccount(accLast, accNewName); /// create one-line method to prevent strange static analyzer inspections..
      }
    }
    obtainLastAccData(); /// refresh accLast record etc..
  }

  private void myAddAccountAndSetToken(String name, String type) {
    Account accNew = new Account(name, type);
    am.addAccountExplicitly(accNew, null, null);
    am.setAuthToken(accNew, authTokenType, authToken);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private void myRenameAccount(Account accOld, String accNewName) {
    am.renameAccount(accOld, accNewName, null, null);
  }

  public boolean isToStoreData() {
    return toStoreData;
  }

  public void setToStoreData(boolean toStoreData) {
    MyAccountManager.toStoreData = toStoreData;
  }
}
