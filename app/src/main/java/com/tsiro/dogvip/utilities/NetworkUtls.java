package com.tsiro.dogvip.utilities;

import android.content.Context;
import android.net.ConnectivityManager;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Created by giannis on 6/10/2017.
 */

public class NetworkUtls {

    private Context mContext;

    public NetworkUtls(Context context) {
        this.mContext = context;
    }

    public Flowable<Boolean> isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return Observable.just(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()).toFlowable(BackpressureStrategy.LATEST);
    }

}
