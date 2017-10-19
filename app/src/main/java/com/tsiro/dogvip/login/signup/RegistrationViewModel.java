package com.tsiro.dogvip.login.signup;

import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
import com.tsiro.dogvip.POJO.registration.RegistrationRequest;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.requestmngrlayer.AuthenticationRequestManager;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 22/5/2017.
 */

public class RegistrationViewModel implements RegistrationContract.ViewModel {

    private AuthenticationRequestManager mAuthenticationRequestManager;
    private RegistrationContract.View mViewClback;
    private AsyncProcessor<AuthenticationResponse> mRegstrProcessor;
    private Disposable mRegstrDisp;
    private int requestState;

    public RegistrationViewModel(AuthenticationRequestManager authenticationRequestManager) {
        this.mAuthenticationRequestManager = authenticationRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewClback = (RegistrationContract.View) viewCallback;
    }

    @Override
    public void onViewResumed() {
        if (mRegstrDisp != null && requestState != AppConfig.REQUEST_RUNNING) mRegstrProcessor.subscribe(new RegistrationObserver());
    }

    @Override
    public void onViewDetached() {
        mViewClback = null;
        if (mRegstrDisp != null) mRegstrDisp.dispose();
    }

    @Override
    public void setRequestState(int state) {
        requestState = state;
    }

    @Override
    public void register(RegistrationRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            mRegstrProcessor = AsyncProcessor.create();
            mRegstrDisp = mRegstrProcessor.subscribeWith(new RegistrationObserver());

            mAuthenticationRequestManager.register(request, this).subscribe(mRegstrProcessor);
        }
    }

    private void onRegistrationSuccess(AuthenticationResponse response) {
        mRegstrDisp = null;
        mViewClback.onSuccess(response);
    }

    private void onRegistrationError(int resource, boolean msglength) {
        mRegstrDisp = null;
        mViewClback.onError(resource, msglength);
        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private class RegistrationObserver extends DisposableSubscriber<AuthenticationResponse> {

        @Override
        public void onNext(@NonNull AuthenticationResponse registrationResponse) {
            if (registrationResponse.getCode() != AppConfig.STATUS_OK) {
                onRegistrationError(AppConfig.getCodes().get(registrationResponse.getCode()), registrationResponse.isStringlength());
            } else {
                onRegistrationSuccess(registrationResponse);
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {
            onRegistrationError(AppConfig.getCodes().get(AppConfig.STATUS_ERROR), false);
        }

        @Override
        public void onComplete() {
        }
    }
}
