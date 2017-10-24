package com.tsiro.dogvip.requestmngrlayer;

import com.tsiro.dogvip.POJO.BaseResponseObj;
import com.tsiro.dogvip.POJO.Response;
import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.POJO.login.LoginResponse;
import com.tsiro.dogvip.POJO.login.SignInEmailRequest;
import com.tsiro.dogvip.POJO.login.SignInUpFbGoogleRequest;
import com.tsiro.dogvip.POJO.login.signup.SignUpEmailRequest;
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

    public Flowable<Response> signUpEmail(SignUpEmailRequest request, LoginViewModel loginViewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mLoginAPIService.signUpEmail(request, loginViewModel).delay(500, TimeUnit.MILLISECONDS);
    }

    public Flowable<Response> signInEmail(SignInEmailRequest request, LoginViewModel loginViewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mLoginAPIService.signInEmail(request, loginViewModel).delay(500, TimeUnit.MILLISECONDS);
    }

    public Flowable<Response> signInUpFbGoogle(SignInUpFbGoogleRequest request, LoginViewModel loginViewModel) {
        return mLoginAPIService.signInUpFbGoogle(request, loginViewModel).delay(500, TimeUnit.MILLISECONDS);
    }

    public Flowable<ForgotPaswrdObj> forgotPaswrd(ForgotPaswrdObj request, LoginViewModel loginViewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mLoginAPIService.forgotPaswrd(request, loginViewModel).delay(500, TimeUnit.MILLISECONDS);
    }

}
