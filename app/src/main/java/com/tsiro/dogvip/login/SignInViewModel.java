package com.tsiro.dogvip.login;

import android.util.Log;

import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
import com.tsiro.dogvip.POJO.signin.SignInRequest;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.requestmngrlayer.SignInRequestManager;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 28/5/2017.
 */

public class SignInViewModel implements SignInContract.ViewModel {

    private static final String debugTag = SignInViewModel.class.getSimpleName();
    private SignInRequestManager mSignInRequestManager;
    private SignInContract.View mViewClback;
    private int requestState;
    private AsyncProcessor<AuthenticationResponse> mSignInProcessor;
    private Disposable mSignInDisp;

    public SignInViewModel(SignInRequestManager signInRequestManager) {
        this.mSignInRequestManager = signInRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewClback = (SignInContract.View) viewCallback;
    }

    @Override
    public void onViewResumed() {
        if (mSignInDisp != null && requestState != AppConfig.REQUEST_RUNNING) mSignInProcessor.subscribe(new SignInObserver());
    }

    @Override
    public void onViewDetached() {
        mViewClback = null;
        if (mSignInDisp != null) mSignInDisp.dispose();
    }

    @Override
    public void signin(SignInRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            mSignInProcessor = AsyncProcessor.create();
            mSignInDisp = mSignInProcessor.subscribeWith(new SignInObserver());
            mSignInRequestManager.signin(request, this).subscribe(mSignInProcessor);
        }
    }

    @Override
    public void setRequestState(int state) {
        requestState = state;
    }

    private void onSignInSuccess(AuthenticationResponse response) {
        mSignInDisp = null;
        mViewClback.onSuccess(response);
    }

    private void onSignInError(int resource, boolean msglength) {
        mSignInDisp = null;
        mViewClback.onError(resource, msglength);
        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private class SignInObserver extends DisposableSubscriber<AuthenticationResponse> {

        @Override
        public void onNext(@NonNull AuthenticationResponse signInResponse) {
            if (signInResponse.getCode() != AppConfig.STATUS_OK) {
                onSignInError(AppConfig.getCodes().get(signInResponse.getCode()), signInResponse.isStringlength());
            } else {
                onSignInSuccess(signInResponse);
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {
            onSignInError(AppConfig.getCodes().get(AppConfig.STATUS_ERROR), false);
        }

        @Override
        public void onComplete() {
        }
    }
}
