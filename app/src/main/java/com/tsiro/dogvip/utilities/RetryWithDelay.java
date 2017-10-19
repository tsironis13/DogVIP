package com.tsiro.dogvip.utilities;

import android.util.Log;

import com.tsiro.dogvip.app.AppConfig;

import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by giannis on 14/10/2017.
 */

public class RetryWithDelay implements Function<Flowable<? extends Throwable>, Flowable<?>> {

    private int maxRetries;
    private int retryDelayMillis;
    private int retryCount;
    private PublishSubject<Object> retryButtonClicked;

    @Inject
    public RetryWithDelay() {
        this.retryCount = 0;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public void setRetryDelayMillis(int retryDelayMillis) {
        this.retryDelayMillis = retryDelayMillis;
    }

    public PublishSubject<Object> getRetryButtonClicked() {
        return retryButtonClicked;
    }

    public void setRetryButtonClicked() {
        this.retryButtonClicked = PublishSubject.create();
    }

    public void emptySubject() {
        this.retryButtonClicked = null;
    }

    @Override
    public Flowable<?> apply(@NonNull Flowable<? extends Throwable> throwableFlowable) throws Exception {
        return throwableFlowable.flatMap(new Function<Throwable, Publisher<?>>() {
            @Override
            public Publisher<?> apply(@NonNull Throwable throwable) throws Exception {
                if (++retryCount < maxRetries) {
//                    requestState = AppConfig.REQUEST_RUNNING;
                    return Flowable.timer(retryDelayMillis, TimeUnit.MILLISECONDS);
                }
                return Flowable.error(new IllegalStateException());
            }
        });
    }

    public PublishSubject<Object> getRetrySubject() {
//        retryButtonClicked = PublishSubject.create();
        Log.e("aaaa", retryButtonClicked +  " RETRU BUTTON");
        return retryButtonClicked;
    }

}
