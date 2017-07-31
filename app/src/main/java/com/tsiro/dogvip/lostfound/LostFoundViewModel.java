package com.tsiro.dogvip.lostfound;

import android.util.Log;

import com.tsiro.dogvip.POJO.lostfound.LostFoundRequest;
import com.tsiro.dogvip.POJO.lostfound.LostFoundResponse;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.requestmngrlayer.LostFoundRequestManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 10/7/2017.
 */

public class LostFoundViewModel implements LostFoundContract.ViewModel {

    private static final String debugTag = LostFoundViewModel.class.getSimpleName();
    private LostFoundContract.View mViewClback;
    private LostFoundRequestManager mLostFoundRequestManager;
    private AsyncProcessor<LostFoundResponse> mProcessor;
    private int requestState;
    private Disposable mDisp;

    public LostFoundViewModel(LostFoundRequestManager mLostFoundRequestManager) {
        this.mLostFoundRequestManager = mLostFoundRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewClback = (LostFoundContract.View) viewCallback;
    }

    @Override
    public void onViewResumed() {
        if (mDisp != null && requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new LostFoundObserver());
        }
    }

    @Override
    public void onViewDetached() {
        mViewClback = null;
        if (mDisp != null) mDisp.dispose();
    }

    @Override
    public void setRequestState(int state) {
        requestState = state;
    }

    @Override
    public void getLostPets(LostFoundRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            requestState = AppConfig.REQUEST_RUNNING;
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new LostFoundObserver());

            mLostFoundRequestManager.getLostFound(request, this).subscribe(mProcessor);
        }
    }

    @Override
    public void getFoundPets(LostFoundRequest request) {

    }

    private void onSuccessAction(LostFoundResponse response) {
        mDisp = null;
        mViewClback.onSuccess(response);
    }

    private void onErrorAction(int code) {
        mDisp = null;
        mViewClback.onError(code);
        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private class LostFoundObserver extends DisposableSubscriber<LostFoundResponse> {

        @Override
        public void onNext(LostFoundResponse response) {
            if (response.getCode() != AppConfig.STATUS_OK) {
                onErrorAction(AppConfig.getCodes().get(response.getCode()));
            } else {
                onSuccessAction(response);
            }
        }

        @Override
        public void onError(Throwable t) {
            onErrorAction(AppConfig.getCodes().get(AppConfig.STATUS_ERROR));
        }

        @Override
        public void onComplete() {

        }
    }
}
