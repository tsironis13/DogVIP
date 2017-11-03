package com.tsiro.dogvip.login.forgotpass;

import android.util.Log;

import com.tsiro.dogvip.POJO.Response;
import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.POJO.login.SignInEmailRequest;
import com.tsiro.dogvip.POJO.login.SignInUpFbGoogleRequest;
import com.tsiro.dogvip.POJO.login.forgotpass.ForgotPassRequest;
import com.tsiro.dogvip.POJO.login.forgotpass.ForgotPassResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.login.LoginContract;
import com.tsiro.dogvip.requestmngrlayer.LoginRequestManager;
import com.tsiro.dogvip.responsecontroller.ResponseController;
import com.tsiro.dogvip.responsecontroller.login.forgotpass.SubmitNewPasswordCommand;
import com.tsiro.dogvip.responsecontroller.login.forgotpass.ValidateForgotPassEmailCommand;
import com.tsiro.dogvip.utilities.NetworkUtls;
import com.tsiro.dogvip.utilities.RetryWithDelay;

import java.util.InvalidPropertiesFormatException;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 29/5/2017.
 */

public class ForgotPaswrdViewModel implements LoginContract.ForgotPassViewModel {

    private static final String debugTag = ForgotPaswrdViewModel.class.getSimpleName();
    private LoginRequestManager mLoginRequestManager;
    private LoginContract.View mViewCallback;
    private AsyncProcessor<Response> mProcessor;
    private int requestState, userId;
    private boolean isEmailValid;
    private Disposable mLoginDisp, mTempDisp;
    @Inject
    ResponseController responseController;
    @Inject
    ValidateForgotPassEmailCommand validateForgotPassEmailCommand;
    @Inject
    SubmitNewPasswordCommand submitNewPasswordCommand;
    @Inject
    NetworkUtls networkUtls;
    @Inject
    RetryWithDelay retryWithDelay;

    @Inject
    public ForgotPaswrdViewModel(LoginRequestManager loginRequestManager) {
        this.mLoginRequestManager = loginRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewCallback = (LoginContract.ForgotPassView) viewCallback;
        validateForgotPassEmailCommand.setViewCallback((LoginContract.ForgotPassView) mViewCallback);
        submitNewPasswordCommand.setViewCallback((LoginContract.ForgotPassView) mViewCallback);
    }

    @Override
    public void onViewResumed() {
//        Log.e(debugTag, "onResume: "+mViewCallback + " request state "+requestState + " login disp: "+ mLoginDisp);
        if (requestState == AppConfig.REQUEST_RUNNING || requestState == AppConfig.REQUEST_FAILED) mViewCallback.onProcessing();
//        if (mLoginDisp != null && requestState != AppConfig.REQUEST_RUNNING && requestState != AppConfig.REQUEST_FAILED) {
//            Log.e(debugTag, "here: "+ requestState);
//            mProcessor
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new LoginObserver());
//        }
        if (mLoginDisp != null && requestState == AppConfig.REQUEST_RUNNING) {
//            Log.e(debugTag, "HERE: "+ requestState);
            mTempDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new ForgotPassObserver());
        }
    }

    @Override
    public void onViewDetached() {
        mViewCallback.onStopProcessing();
        mViewCallback = null;
        if (mLoginDisp != null) mLoginDisp.dispose();
        if (mTempDisp != null) mTempDisp.dispose();
        validateForgotPassEmailCommand.clearCallback();
        submitNewPasswordCommand.clearCallback();
    }

    @Override
    public void onProcessing() {
//        Log.e(debugTag, "onProcessing "+mViewCallback);
        mViewCallback.onProcessing();
    }

    @Override
    public void handleUserInputAction(ForgotPassRequest request) {
        if (!isEmailValid && userId == 0) {
            request.setAction("validate_forgot_password_email");
            validateForgotPass(request);
        } else {
            request.setAction("submit_new_password");
            request.setUserId(userId);
            submitNewPass(request);
        }

    }

    private void validateForgotPass(ForgotPassRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING && requestState != AppConfig.REQUEST_FAILED) {
            responseController.setCommand(validateForgotPassEmailCommand);
            prepareRequest(forgotPassRequest(request));
        }
    }

    private void submitNewPass(ForgotPassRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING && requestState != AppConfig.REQUEST_FAILED) {
            responseController.setCommand(submitNewPasswordCommand);
            prepareRequest(forgotPassRequest(request));
        }
    }

    private Flowable<Response> forgotPassRequest(ForgotPassRequest request) {
        return mLoginRequestManager.forgotPass(request, this);
    }

    private void prepareRequest(final Flowable<Response> responseFlowable) {
//        Log.e(debugTag, "prepare request " + mViewCallback);
        mProcessor = AsyncProcessor.create();
        mLoginDisp = mProcessor
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new ForgotPassObserver());

        networkUtls.getNetworkFlowable
                            .doOnSubscribe(subscription -> onProcessing())
                            .flatMap(aBoolean -> responseFlowable
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .retryWhen(configureRetryWithDelayParams(3, 2000)))
                            .take(1)
                            .subscribeWith(mProcessor);
    }

    private RetryWithDelay configureRetryWithDelayParams(int maxRetries, int retryDelayMillis) {
        retryWithDelay.setMaxRetries(maxRetries);
        retryWithDelay.setRetryDelayMillis(retryDelayMillis);
        return retryWithDelay;
    }

    private void handleError(Throwable throwable) {
        if (throwable instanceof IllegalStateException) { //server error
            mViewCallback.onError(R.string.error);
        } else if (throwable instanceof InvalidPropertiesFormatException) {
            mViewCallback.onError(R.string.please_fill_out_search_filters);
        } else {//no network connection error
//            Log.e(debugTag, " on error");
            mViewCallback.onError(R.string.no_internet_connection);
        }
    }

    @Override
    public void setRequestState(int requestState) {
        this.requestState = requestState;
    }

    public void setEmailValid(int userId) {
        this.userId = userId;
        isEmailValid = true;
    }

    private class ForgotPassObserver extends DisposableSubscriber<Response> {

        @Override
        public void onNext(Response response) {
            mLoginDisp = null;
            requestState = AppConfig.REQUEST_SUCCEEDED;
            if (response.getCode() != AppConfig.STATUS_OK) {
                responseController.executeCommandOnError(AppConfig.getCodes().get(response.getCode()));
            } else {
                responseController.executeCommandOnSuccess(response);
            }
        }

        @Override
        public void onError(Throwable t) {
//            Log.e(debugTag, " onerror dr: "+t);
            mLoginDisp = null;
            if (mViewCallback != null) {
                handleError(t);
                requestState = AppConfig.REQUEST_NONE;
            }
        }

        @Override
        public void onComplete() {}
    }

}
