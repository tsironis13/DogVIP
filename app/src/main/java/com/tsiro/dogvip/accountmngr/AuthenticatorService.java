package com.tsiro.dogvip.accountmngr;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by giannis on 22/5/2017.
 */

public class AuthenticatorService extends Service {

    private AccountAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuthenticator = new AccountAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
