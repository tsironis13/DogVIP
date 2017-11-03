package com.tsiro.dogvip.responsecontroller.login.signinup;

import com.tsiro.dogvip.POJO.Response;
import com.tsiro.dogvip.login.LoginContract;
import com.tsiro.dogvip.responsecontroller.Command;

import javax.inject.Inject;

/**
 * Created by giannis on 23/10/2017.
 */

public class SignInEmailCommand implements Command {

    private LoginContract.SignInView mViewCallback;

    @Inject
    public SignInEmailCommand() {}

    public void setViewCallback(LoginContract.SignInView mViewCallback) {
        this.mViewCallback = mViewCallback;
    }

//    public LoginContract.SignInView getmViewCallback() {
//        return mViewCallback;
//    }

    public void clearCallback() {
        this.mViewCallback = null;
    }

    @Override
    public void executeOnSuccess(Response response) {
        mViewCallback.onSuccessEmailSignIn(response.getLogin());
    }

    @Override
    public void executeOnError(int resource) {
        mViewCallback.onError(resource);
    }
}
