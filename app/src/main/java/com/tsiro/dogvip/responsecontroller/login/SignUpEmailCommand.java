package com.tsiro.dogvip.responsecontroller.login;

import com.tsiro.dogvip.POJO.Response;
import com.tsiro.dogvip.login.LoginContract;
import com.tsiro.dogvip.responsecontroller.Command;

import javax.inject.Inject;

/**
 * Created by giannis on 23/10/2017.
 */

public class SignUpEmailCommand implements Command {

    private LoginContract.SignUpView mViewCallback;

    @Inject
    public SignUpEmailCommand() {}

    public void setViewCallback(LoginContract.SignUpView mViewCallback) {
        this.mViewCallback = mViewCallback;
    }

    @Override
    public void executeOnSuccess(Response response) {
        mViewCallback.onSuccessEmailSignUp(response.getLogin());
    }

    @Override
    public void executeOnError(int resource) {
        mViewCallback.onError(resource);
    }

}
