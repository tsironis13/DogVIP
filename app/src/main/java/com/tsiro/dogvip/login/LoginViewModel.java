package com.tsiro.dogvip.login;

import android.os.Bundle;
import android.util.Log;

import com.tsiro.dogvip.POJO.BaseResponseObj;
import com.tsiro.dogvip.POJO.Response;
import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.POJO.login.SignInEmailRequest;
import com.tsiro.dogvip.POJO.login.SignInUpFbGoogleRequest;
import com.tsiro.dogvip.POJO.login.signup.SignUpEmailRequest;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.lovematch.LoveMatchViewModel;
import com.tsiro.dogvip.responsecontroller.ResponseController;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.requestmngrlayer.LoginRequestManager;
import com.tsiro.dogvip.responsecontroller.login.SignInEmailCommand;
import com.tsiro.dogvip.responsecontroller.login.SignInUpFbCommand;
import com.tsiro.dogvip.responsecontroller.login.SignInUpGoogleCommand;
import com.tsiro.dogvip.responsecontroller.login.SignUpEmailCommand;
import com.tsiro.dogvip.responsecontroller.lovematch.GetPetsCommand;
import com.tsiro.dogvip.responsecontroller.lovematch.LikeDislikeCommand;
import com.tsiro.dogvip.utilities.NetworkUtls;
import com.tsiro.dogvip.utilities.RetryWithDelay;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 19/10/2017.
 */
//common ViewModel for signIn, register fragment
public class LoginViewModel implements LoginContract.ViewModel {

    private static final String debugTag = LoginViewModel.class.getSimpleName();
    private LoginRequestManager mLoginRequestManager;
    private LoginContract.View mViewCallback;
    private AsyncProcessor<Response> mProcessor;
    private int requestState;
    private Disposable mLoginDisp;
    @Inject
    ResponseController responseController;
    @Inject
    SignInEmailCommand signInEmailCommand;
    @Inject
    SignUpEmailCommand signUpEmailCommand;
    @Inject
    SignInUpFbCommand signInUpFbCommand;
    @Inject
    SignInUpGoogleCommand signInUpGoogleCommand;
    @Inject
    NetworkUtls networkUtls;
    @Inject
    RetryWithDelay retryWithDelay;

    @Inject
    public LoginViewModel(LoginRequestManager loginRequestManager) {
        this.mLoginRequestManager = loginRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewCallback = (LoginContract.View) viewCallback;
        Log.e(debugTag, mViewCallback + " onattach");
    }

    @Override
    public void onViewResumed() {
        Log.e(debugTag, "onResume: "+mViewCallback + " request state "+requestState);
        if (requestState == AppConfig.REQUEST_RUNNING || requestState == AppConfig.REQUEST_FAILED)
            mViewCallback.onProcessing();
        if (mLoginDisp != null && requestState != AppConfig.REQUEST_RUNNING && requestState != AppConfig.REQUEST_FAILED) {
            Log.e(debugTag, "here");
            mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new LoginObserver());
        }
    }

    @Override
    public void onViewDetached() {
        Log.e(debugTag, "onViewDetached");
        mViewCallback.onStopProcessing();
        mViewCallback = null;
        if (mLoginDisp != null) {
            mLoginDisp.dispose();
        }
    }

    @Override
    public void onProcessing() {
        Log.e(debugTag, "onProcessing "+mViewCallback);
        mViewCallback.onProcessing();
    }

    @Override
    public void signUpEmail(SignUpEmailRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING && requestState != AppConfig.REQUEST_FAILED) {
            signUpEmailCommand.setViewCallback((LoginContract.SignUpView) mViewCallback);
            responseController.setCommand(signUpEmailCommand);
            prepareRequest(getSignUpEmailFlowableRequest(request));
        }
    }

    @Override
    public void signInUpFb(SignInUpFbGoogleRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING && requestState != AppConfig.REQUEST_FAILED) {
            signInUpFbCommand.setViewCallback((LoginContract.SignUpView) mViewCallback);
            responseController.setCommand(signInUpFbCommand);
            prepareRequest(getSignInUpFbGoogleRequest(request));
        }
    }

    @Override
    public void signInUpGoogle(SignInUpFbGoogleRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING && requestState != AppConfig.REQUEST_FAILED) {
            signInUpGoogleCommand.setViewCallback((LoginContract.SignUpView) mViewCallback);
            responseController.setCommand(signInUpGoogleCommand);
            prepareRequest(getSignInUpFbGoogleRequest(request));
        }
    }

    @Override
    public void signInEmail(SignInEmailRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING && requestState != AppConfig.REQUEST_FAILED) {
            signInEmailCommand.setViewCallback((LoginContract.SignInView) mViewCallback);
            responseController.setCommand(signInEmailCommand);
            prepareRequest(getSignInEmailFlowableRequest(request));
        }
    }

    @Override
    public void forgotPass(ForgotPaswrdObj request) {

    }

    private void prepareRequest(final Flowable<Response> responseFlowable) {
        Log.e(debugTag, "prepare request " + mViewCallback);
        mProcessor = AsyncProcessor.create();
        mLoginDisp = mProcessor
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.e(debugTag, "kalaseeeeeeeeeee");
                    }
                })
                            .subscribeWith(new LoginObserver());

        networkUtls.getNetworkFlowable.
                doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        onProcessing();
                    }
                })
                .flatMap(new Function<Boolean, Publisher<Response>>() {
                    @Override
                    public Publisher<Response> apply(@NonNull Boolean aBoolean) throws Exception {
                        return responseFlowable
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .retryWhen(configureRetryWithDelayParams(3, 2000));
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.e("aa", "do on error");
//                        mProcessor.onError(new Exception());
                        requestState = AppConfig.REQUEST_NONE;
                        if (mViewCallback != null) handleError(throwable);
                    }
                })
                .take(1)
                .subscribe(mProcessor);
    }

    private Flowable<Response> getSignUpEmailFlowableRequest(SignUpEmailRequest request) {
        return mLoginRequestManager.signUpEmail(request, this);
    }

    private Flowable<Response> getSignInEmailFlowableRequest(SignInEmailRequest request) {
        return mLoginRequestManager.signInEmail(request, this);
    }

    private Flowable<Response> getSignInUpFbGoogleRequest(SignInUpFbGoogleRequest request) {
        return mLoginRequestManager.signInUpFbGoogle(request, this);
    }

    private RetryWithDelay configureRetryWithDelayParams(int maxRetries, int retryDelayMillis) {
        retryWithDelay.setMaxRetries(maxRetries);
        retryWithDelay.setRetryDelayMillis(retryDelayMillis);
        return retryWithDelay;
    }

    private void handleError(Throwable throwable) {
        Log.e("ddd", throwable + " throwable "+ mViewCallback + " callbacl");
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

    private class LoginObserver extends DisposableSubscriber<Response> {

        @Override
        public void onNext(Response response) {
//            Log.e("aaaa", response.getCode() + " ");
            mLoginDisp = null;
            if (mViewCallback != null) requestState = AppConfig.REQUEST_NONE;
            if (response.getCode() != AppConfig.STATUS_OK) {
                Log.e("aaa", "execute on error");
                responseController.executeCommandOnError(AppConfig.getCodes().get(response.getCode()));
            } else {
                responseController.executeCommandOnSuccess(response);
            }
        }

        @Override
        public void onError(Throwable t) {
            mLoginDisp = null;
            if (mViewCallback != null) requestState = AppConfig.REQUEST_NONE;
        }

        @Override
        public void onComplete() {}
    }
}
