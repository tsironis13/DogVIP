package com.tsiro.dogvip.login;

import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.POJO.registration.RegistrationRequest;
import com.tsiro.dogvip.POJO.signin.SignInRequest;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.requestmngrlayer.LoginRequestManager;

import javax.inject.Inject;

/**
 * Created by giannis on 19/10/2017.
 */
//common ViewModel for signIn, register fragment
public class LoginViewModel implements LoginContract.ViewModel {

    private LoginRequestManager mLoginRequestManager;

    @Inject
    public LoginViewModel(LoginRequestManager loginRequestManager) {
        this.mLoginRequestManager = loginRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {

    }

    @Override
    public void onViewResumed() {

    }

    @Override
    public void onViewDetached() {

    }

    @Override
    public void signIn(SignInRequest request) {

    }

    @Override
    public void signUp(RegistrationRequest request) {

    }

    @Override
    public void forgotPass(ForgotPaswrdObj request) {

    }

    @Override
    public void setRequestState(int state) {

    }
}
