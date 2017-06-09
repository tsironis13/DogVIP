package com.tsiro.dogvip.accountmngr;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.io.IOException;


/**
 * Created by giannis on 22/5/2017.
 */

public class MyAccountManager {

    private Context mContext;
    private AccountManager am;

    public MyAccountManager(Context context) {
        this.mContext = context;
        this.am = AccountManager.get(mContext);
    }

    private Account getAccountByType(String type) {
        Account[] mAccount = am.getAccountsByType(type);
        return mAccount.length == 1 ? mAccount[0] : null;
    }

    public boolean checkAccountExists() {
        return am.getAccountsByType(mContext.getResources().getString(R.string.account_type)).length != 0;
    }

    public boolean addAccount(String email, String token) {
        if (checkAccountExists()) return false;
        Account account = new Account(email, mContext.getResources().getString(R.string.account_type));

        Bundle extra = new Bundle();
        extra.putString(mContext.getResources().getString(R.string.email), email);
        boolean account_added = am.addAccountExplicitly(account, null, extra);
        am.setAuthToken(account, "1", token);
        return account_added;
    }

    public void getUserData(final Lifecycle.BaseView baseView) {
        final Account mAccount = getAccountByType(mContext.getResources().getString(R.string.account_type));
        AccountManager.get(mContext).getAuthToken(mAccount, "1", null, null, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle authTokenBundle = future.getResult();
                    AuthenticationResponse response = new AuthenticationResponse();
                    if (authTokenBundle.get(AccountManager.KEY_AUTHTOKEN) != null) {
                        response.setAuthtoken(authTokenBundle.get(AccountManager.KEY_AUTHTOKEN).toString());
                        response.setEmail(AccountManager.get(mContext).getUserData(mAccount, mContext.getResources().getString(R.string.email)));
                    }
                    baseView.logUserIn(response);
//                    RxEventBus.createSubject(AppConfig.USER_ACCOUNT_DATA, AppConfig.PUBLISH_SUBJ).post(response);
                } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                    e.printStackTrace();
                }

            }
        }, null);
    }

    public void removeAccount() {
        Account mAccount = getAccountByType(mContext.getResources().getString(R.string.account_type));
        if (mAccount != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
                AccountManager.get(mContext).removeAccount(mAccount, null, null);
            } else {
                AccountManager.get(mContext).removeAccount(mAccount, null, null, null);
            }
        }
    }


}
