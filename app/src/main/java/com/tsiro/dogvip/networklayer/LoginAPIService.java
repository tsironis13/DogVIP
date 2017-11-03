package com.tsiro.dogvip.networklayer;

import android.util.Log;

import com.tsiro.dogvip.POJO.Response;
import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.POJO.login.SignInEmailRequest;
import com.tsiro.dogvip.POJO.login.SignInUpFbGoogleRequest;
import com.tsiro.dogvip.POJO.login.forgotpass.ForgotPassRequest;
import com.tsiro.dogvip.POJO.login.forgotpass.ForgotPassResponse;
import com.tsiro.dogvip.POJO.login.signup.SignUpEmailRequest;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.login.forgotpass.ForgotPaswrdViewModel;
import com.tsiro.dogvip.login.signin.SignInViewModel;
import com.tsiro.dogvip.login.signup.RegistrationViewModel;
import com.tsiro.dogvip.retrofit.RetrofitFactory;
import com.tsiro.dogvip.retrofit.ServiceAPI;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
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

    public Flowable<Response> signInEmail(final SignInEmailRequest request, final SignInViewModel viewModel) {
        return serviceAPI.signInEmail(request)
                .doOnSubscribe(subscription -> viewModel.setRequestState(AppConfig.REQUEST_RUNNING))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<Response> signUpEmail(SignUpEmailRequest request, final RegistrationViewModel loginViewModel) {
        return serviceAPI.signUpEmail(request)
                .doOnSubscribe(subscription -> loginViewModel.setRequestState(AppConfig.REQUEST_RUNNING))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<Response> signInFbGoogle(SignInUpFbGoogleRequest request, final SignInViewModel viewModel) {
        return serviceAPI.signInUpFbGoogle(request)
                .doOnSubscribe(subscription -> {
                    viewModel.setRequestState(AppConfig.REQUEST_RUNNING);
//                    Log.e("REQUEST RUNNING", "RUNNING");
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<Response> signUpFbGoogle(SignInUpFbGoogleRequest request, final RegistrationViewModel viewModel) {
        return serviceAPI.signInUpFbGoogle(request)
                .doOnSubscribe(subscription -> viewModel.setRequestState(AppConfig.REQUEST_RUNNING))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<Response> forgotPass(ForgotPassRequest request, final ForgotPaswrdViewModel viewModel) {
        return serviceAPI.forgotPass(request)
                .doOnSubscribe(subscription -> viewModel.setRequestState(AppConfig.REQUEST_RUNNING))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
