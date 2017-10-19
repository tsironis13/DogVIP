package com.tsiro.dogvip.utilities;

import android.content.Context;
import android.net.ConnectivityManager;

import com.tsiro.dogvip.di.qualifiers.ApplicationContext;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by giannis on 6/10/2017.
 */

public class NetworkUtls {

    private Context mContext;

    @Inject
    public NetworkUtls(Context context) {
        this.mContext = context;
    }

    public Flowable<Boolean> getNetworkFlowable = Flowable.create(new FlowableOnSubscribe<Boolean>() {
        @Override
        public void subscribe(@NonNull FlowableEmitter<Boolean> e) throws Exception {
            if (isNetworkAvailable()) {
                e.onNext(true);
            } else {
                e.onError(new Throwable());
            }
        }
    }, BackpressureStrategy.LATEST);

    private Boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public Flowable<Boolean> isNetworkAvailable1() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return Flowable.just(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected());
    }

}
