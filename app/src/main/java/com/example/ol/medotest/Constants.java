package com.example.ol.medotest;

/**
 * Constants necessary for operation
 */
public final class Constants {

  public class Extras {
    public static final String ARG_SECTION_NUMBER = "section_number";
  }

  public class PhoneNumber {
    public final static String REG_EX = "^\\+?[1-9]{1}\\s*[0-9]{3}\\s*[0-9]{3}\\s*[0-9]{4}$";
  }

  public class SmsCode {
    public static final int MIN_CODE_VALUE = 111111;
    public static final int MAX_CODE_VALUE = 999999;
    public static final int TIMEOUT = 300; /// (sec.) till the code exceeds time limit
    public static final int UPDATE_TIME_DELTA = 10; /// (sec.) timer updating period
  }

  public class Password {
    public final static String REG_EX = "^[a-zA-Z0-9]*$";
    public final static int MIN_LENGTH = 7;
  }

  public class Account {
    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public final static String PARAM_USER_PASS = "USER_PASS";
  }

} //class Constants



