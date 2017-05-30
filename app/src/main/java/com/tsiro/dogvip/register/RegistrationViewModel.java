package com.tsiro.dogvip.register;

import com.tsiro.dogvip.POJO.registration.RegistrationRequest;
import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
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

    private static final String debugTag = RegistrationViewModel.class.getSimpleName();
    private AuthenticationRequestManager mAuthenticationRequestManager;
    private RegistrationContract.View viewClback;
    private AsyncProcessor<AuthenticationResponse> mRegstrProcessor;
    private Disposable mRegstrDisp;
    private int requestState;

    public RegistrationViewModel(AuthenticationRequestManager authenticationRequestManager) {
        this.mAuthenticationRequestManager = authenticationRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
//        Log.e(debugTag, "onViewATTACHED");
        this.viewClback = (RegistrationContract.View) viewCallback;
    }

    @Override
    public void onViewResumed() {
        if (mRegstrDisp != null) mRegstrProcessor.subscribe(new RegistrationObserver());
    }

    @Override
    public void onViewDetached() {
//        Log.e(debugTag, "onViewDetached");
        viewClback = null;
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
        viewClback.onSuccess(response);
    }

    private void onRegistrationError(int resource, boolean msglength) {
        mRegstrDisp = null;
        viewClback.onError(resource, msglength);
        if (viewClback != null) requestState = AppConfig.REQUEST_NONE;
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
//            Log.e(debugTag, "onComplete");
        }
    }
}
