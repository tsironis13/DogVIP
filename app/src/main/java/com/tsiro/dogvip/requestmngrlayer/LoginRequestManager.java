package com.tsiro.dogvip.requestmngrlayer;

import com.tsiro.dogvip.POJO.Response;
import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.POJO.login.SignInEmailRequest;
import com.tsiro.dogvip.POJO.login.SignInUpFbGoogleRequest;
import com.tsiro.dogvip.POJO.login.forgotpass.ForgotPassRequest;
import com.tsiro.dogvip.POJO.login.forgotpass.ForgotPassResponse;
import com.tsiro.dogvip.POJO.login.signup.SignUpEmailRequest;
import com.tsiro.dogvip.login.forgotpass.ForgotPaswrdViewModel;
import com.tsiro.dogvip.login.signin.SignInViewModel;
import com.tsiro.dogvip.login.signup.RegistrationViewModel;
import com.tsiro.dogvip.networklayer.LoginAPIService;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Flowable;

/**
 * Created by giannis on 23/5/2017.
 */

public class LoginRequestManager {

    private LoginAPIService mLoginAPIService;

    @Inject
    public LoginRequestManager(LoginAPIService loginAPIService) {
        this.mLoginAPIService = loginAPIService;
    }

    public Flowable<Response> signUpEmail(SignUpEmailRequest request, RegistrationViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mLoginAPIService.signUpEmail(request, viewModel).delay(600, TimeUnit.MILLISECONDS);
    }

    public Flowable<Response> signInEmail(SignInEmailRequest request, SignInViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mLoginAPIService.signInEmail(request, viewModel).delay(600, TimeUnit.MILLISECONDS);
    }

    public Flowable<Response> signInFbGoogle(SignInUpFbGoogleRequest request, SignInViewModel viewModel) {
        return mLoginAPIService.signInFbGoogle(request, viewModel).delay(600, TimeUnit.MILLISECONDS);
    }

    public Flowable<Response> signUpFbGoogle(SignInUpFbGoogleRequest request, RegistrationViewModel viewModel) {
        return mLoginAPIService.signUpFbGoogle(request, viewModel).delay(600, TimeUnit.MILLISECONDS);
    }

    public Flowable<Response> forgotPass(ForgotPassRequest request, ForgotPaswrdViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mLoginAPIService.forgotPass(request, viewModel).delay(500, TimeUnit.MILLISECONDS);
    }

}
