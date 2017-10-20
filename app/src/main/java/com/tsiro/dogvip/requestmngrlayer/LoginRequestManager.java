package com.tsiro.dogvip.requestmngrlayer;

import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.POJO.registration.RegistrationRequest;
import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
import com.tsiro.dogvip.POJO.signin.SignInRequest;
import com.tsiro.dogvip.login.LoginViewModel;
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

    public Flowable<AuthenticationResponse> signUp(RegistrationRequest request, LoginViewModel loginViewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mLoginAPIService.signUp(request, loginViewModel).delay(500, TimeUnit.MILLISECONDS);
    }

    public Flowable<AuthenticationResponse> signin(SignInRequest request, LoginViewModel loginViewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mLoginAPIService.signIn(request, loginViewModel).delay(500, TimeUnit.MILLISECONDS);
    }

    public Flowable<ForgotPaswrdObj> forgotPaswrd(ForgotPaswrdObj request, LoginViewModel loginViewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mLoginAPIService.forgotPaswrd(request, loginViewModel).delay(500, TimeUnit.MILLISECONDS);
    }

}
