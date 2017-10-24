package com.tsiro.dogvip.responsecontroller.login;

import com.tsiro.dogvip.POJO.Response;
import com.tsiro.dogvip.login.LoginContract;
import com.tsiro.dogvip.responsecontroller.Command;

import javax.inject.Inject;

/**
 * Created by giannis on 23/10/2017.
 */

public class SignInUpFbCommand implements Command {

    private LoginContract.SignInUpView mViewCallback;

    @Inject
    public SignInUpFbCommand() {}

    public void setViewCallback(LoginContract.SignInUpView mViewCallback) {
        this.mViewCallback = mViewCallback;
    }

    @Override
    public void executeOnSuccess(Response response) {
        mViewCallback.onSuccessFbLogin(response.getLogin());
    }

    @Override
    public void executeOnError(int resource) {
        mViewCallback.onErrorFbLogin(resource);
    }

}
