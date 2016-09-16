package com.example.ol.medotest.Password;

import android.text.InputType;
import android.widget.EditText;

import com.example.ol.medotest.Constants;

import java.util.regex.Pattern;

/**
 * Created by ol on 9/8/16.
 */

/**
 * PasswordChecker - related service methods and data
 * block it from inheritance for security reasons
 */
public class PasswordChecker {

  static final Pattern pat = Pattern.compile(Constants.Password.REG_EX);

  /**
   * check passwords for coincidence
   * @param password1
   * @param password2
   * @return true if not nulled && equals
   */
  public boolean checkEquivalence(String password1, String password2) {
    if ( (null == password1) || (null == password2) )
      return false;
    return password1.equals(password2);
  }

  /**
   * check if input is valid for password
   * @param input
   * @return true if suitable
   */
  public boolean checkInputValidity(String input) {
    if (null == input)
      return false;
    if (input.length() < Constants.Password.MIN_LENGTH)
      return false;
    return pat.matcher(input).matches();
  }

  /**
   * whether to replace input text on stars signs or to show as is
   * @param et
   * @param toShowPassword
   */
  public static void setInputMask(EditText et, boolean toShowPassword) {
    if (null == et)
      return;
    if (toShowPassword)
      et.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
    else
      et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
  }

}
