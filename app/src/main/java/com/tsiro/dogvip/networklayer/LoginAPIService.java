package com.tsiro.dogvip.networklayer;

import android.util.Log;

import com.tsiro.dogvip.POJO.BaseRequestObj;
import com.tsiro.dogvip.POJO.BaseResponseObj;
import com.tsiro.dogvip.POJO.Response;
import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.POJO.login.LoginResponse;
import com.tsiro.dogvip.POJO.login.SignInEmailRequest;
import com.tsiro.dogvip.POJO.login.SignInUpFbGoogleRequest;
import com.tsiro.dogvip.POJO.login.signup.SignUpEmailRequest;
import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
import com.tsiro.dogvip.POJO.registration.RegistrationRequest;
import com.tsiro.dogvip.POJO.signin.SignInRequest;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.login.LoginViewModel;
import com.tsiro.dogvip.login.forgotpass.ForgotPaswrdViewModel;
import com.tsiro.dogvip.login.signin.SignInViewModel;
import com.tsiro.dogvip.login.signup.RegistrationViewModel;
import com.tsiro.dogvip.retrofit.RetrofitFactory;
import com.tsiro.dogvip.retrofit.ServiceAPI;

import org.reactivestreams.Subscription;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by giannis on 28/5/2017.
 */

public class LoginAPIService {

    private ServiceAPI serviceAPI;

    @Inject
    public LoginAPIService() {
        serviceAPI = RetrofitFactory.getInstance().getServiceAPI();
    }

    public Flowable<Response> signInEmail(final SignInEmailRequest request, final LoginViewModel loginViewModel) {
        return serviceAPI.signInEmail(request)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        loginViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        loginViewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<Response>() {
                    @Override
                    public void accept(@NonNull Response response) throws Exception {
                        Log.e("aaa", response.getCode() + "");
                        loginViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

    public Flowable<Response> signUpEmail(SignUpEmailRequest request, final LoginViewModel loginViewModel) {
        return serviceAPI.signUpEmail(request)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        loginViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        loginViewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<Response>() {
                    @Override
                    public void accept(@NonNull Response response) throws Exception {
                        loginViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

    public Flowable<Response> signInUpFbGoogle(SignInUpFbGoogleRequest request, final LoginViewModel loginViewModel) {
        return serviceAPI.signInUpFbGoogle(request)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        loginViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        loginViewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<Response>() {
                    @Override
                    public void accept(@NonNull Response response) throws Exception {
                        loginViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

    public Flowable<ForgotPaswrdObj> forgotPaswrd(ForgotPaswrdObj request, final LoginViewModel loginViewModel) {
        return serviceAPI.forgotPaswrd(request)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        loginViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        loginViewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<ForgotPaswrdObj>() {
                    @Override
                    public void accept(@NonNull ForgotPaswrdObj response) throws Exception {
                        loginViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }
}
