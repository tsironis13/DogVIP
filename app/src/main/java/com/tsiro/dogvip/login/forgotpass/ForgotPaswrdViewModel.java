package com.tsiro.dogvip.login.forgotpass;

import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 29/5/2017.
 */

public class ForgotPaswrdViewModel {

//    private ForgotPaswrdRequestManager mForgotPaswrdRequestManager;
//    private ForgotPaswrdContract.View mViewClback;
    private AsyncProcessor<ForgotPaswrdObj> mForgotPaswrdProcessor;
    private Disposable mForgotPaswrdDisp;
    private int requestState;

    public ForgotPaswrdViewModel() {
//        this.mForgotPaswrdRequestManager = forgotPaswrdRequestManager;
    }

//    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
//        this.mViewClback = (ForgotPaswrdContract.View) viewCallback;
    }

//    @Override
    public void onViewResumed() {
        if (mForgotPaswrdDisp != null && requestState != AppConfig.REQUEST_RUNNING) mForgotPaswrdProcessor.subscribe(new ForgotPaswrdObserver());
    }

//    @Override
    public void onViewDetached() {
//        mViewClback = null;
        if (mForgotPaswrdDisp != null) mForgotPaswrdDisp.dispose();
    }

//    @Override
    public void fogotpass(ForgotPaswrdObj request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            mForgotPaswrdProcessor = AsyncProcessor.create();
            mForgotPaswrdDisp = mForgotPaswrdProcessor.subscribeWith(new ForgotPaswrdObserver());

//            mForgotPaswrdRequestManager.forgotPaswrd(request, this).subscribe(mForgotPaswrdProcessor);
        }
    }

//    @Override
    public void setRequestState(int state) {
        requestState = state;
    }

    private void onForgotPaswrdSuccess(ForgotPaswrdObj response) {
        mForgotPaswrdDisp = null;
//        mViewClback.onSuccess(response);
    }

    private void onForgotPaswrdError(int resource, boolean msglength) {
        mForgotPaswrdDisp = null;
//        mViewClback.onError(resource, msglength);
//        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private class ForgotPaswrdObserver extends DisposableSubscriber<ForgotPaswrdObj> {

        @Override
        public void onNext(@NonNull ForgotPaswrdObj forgotPaswrdResponse) {
            if (forgotPaswrdResponse.getCode() != AppConfig.STATUS_OK) {
                onForgotPaswrdError(AppConfig.getCodes().get(forgotPaswrdResponse.getCode()), forgotPaswrdResponse.isStringlength());
            } else {
                onForgotPaswrdSuccess(forgotPaswrdResponse);
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {
            onForgotPaswrdError(AppConfig.getCodes().get(AppConfig.STATUS_ERROR), false);
        }

        @Override
        public void onComplete() {
        }
    }

}
