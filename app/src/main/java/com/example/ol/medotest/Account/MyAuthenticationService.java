package com.example.ol.medotest.Account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyAuthenticationService extends Service {
  private MyAuthenticator mAuth;

  @Override
  public void onCreate() {
    mAuth = new MyAuthenticator(getApplicationContext());
  }

  @Override
  public IBinder onBind(Intent intent) {
    return mAuth.getIBinder();
  }
}
