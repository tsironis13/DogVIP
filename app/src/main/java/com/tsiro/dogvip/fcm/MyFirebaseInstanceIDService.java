package com.tsiro.dogvip.fcm;

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.tsiro.dogvip.POJO.FcmTokenUpload;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.accountmngr.MyAccountManager;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.utilities.common.CommonUtls;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by giannis on 16/7/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String debugTag = MyFirebaseInstanceIDService.class.getSimpleName();
    private MyAccountManager myAccountManager;
    private CommonUtls mCommonUtls;

    @Override
    public void onCreate() {
        super.onCreate();
        myAccountManager = new MyAccountManager(getApplicationContext());
        mCommonUtls = new CommonUtls(getApplicationContext());
    }

    @Override
    public void onTokenRefresh() {
        if (myAccountManager.checkAccountExists()) {
            Log.e(debugTag, "account exists");
            mCommonUtls.uploadTokenToServer(myAccountManager.getAccountDetails().getToken(), FirebaseInstanceId.getInstance().getToken());
        } else {
            mCommonUtls.getSharedPrefs().edit()
                    .putBoolean(getResources().getString(R.string.fcmtoken_uploaded), false)
                    .putString(getResources().getString(R.string.fcmtoken), FirebaseInstanceId.getInstance().getToken())
                    .apply();
            Log.e(debugTag, "account does not exist");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myAccountManager = null;
        mCommonUtls = null;
    }

}
