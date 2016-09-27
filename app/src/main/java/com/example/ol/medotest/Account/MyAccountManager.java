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

import java.util.ArrayList;
import java.util.List;

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

  /// all stored numbers here on the device (by AccountManager)
  private static List<String> accNames = null;

  /// from prev. session
  private static Account accLast = null;

  /// current, just entered
  private static String accNewName = "";
  private static String accNewPassword = "";

  private static void init(Context context, String accType) {
    if (null != instance)
      return;
    MyAccountManager.accType = accType;
    am = AccountManager.get(context);
    obtainLastAccData();
    toStoreData = !accNames.isEmpty(); /// store user if we already have account here..
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
    accNames = new ArrayList<>(accounts.length);
    if (accounts.length == 0)
      return;
    for (int i=0; i< accounts.length; i++)
      accNames.add(accounts[i].name);

    accLast = accounts[accounts.length-1];
  }

  public List<String> getAllNumbers() {
    return accNames;
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
    if (accNames.contains(accNewName))
      return AddAccountRes.EQUAL_NUMBERS; ///new account number is the same with the stored one
    /// start to add account
    am.addAccount(accType, authToken, null, null, activity, accountManagerCallback, null);
    return AddAccountRes.STARTING;
  }

  /**
   * internal call from AccountAuthenticatorActivity to execute account adding
   */
  public void storeAccount() {
    Account accNew = new Account(accNewName, accType);
    am.addAccountExplicitly(accNew, null, null);
    am.setAuthToken(accNew, authTokenType, authToken);
    obtainLastAccData(); /// refresh accLast record etc..
  }

  private void myAddAccountAndSetToken(String name, String type) {
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
