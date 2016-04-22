package services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import account.CustomAuthenticator;

/**
 * Created by prasadsawant on 4/15/16.
 */
public class AuthenticatorService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        CustomAuthenticator customAuthenticator = new CustomAuthenticator(getApplicationContext());

        return customAuthenticator.getIBinder();
    }
}
