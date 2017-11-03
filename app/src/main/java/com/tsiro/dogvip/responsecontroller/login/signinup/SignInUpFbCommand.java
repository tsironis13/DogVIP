package com.tsiro.dogvip.responsecontroller.login.signinup;

import com.tsiro.dogvip.POJO.Response;
import com.tsiro.dogvip.login.LoginContract;
import com.tsiro.dogvip.responsecontroller.Command;

import javax.inject.Inject;

/**
 * Created by giannis on 23/10/2017.
 */

public class SignInUpFbCommand implements Command {

    private LoginContract.SignInUpFbGoogleView mViewCallback;

    @Inject
    public SignInUpFbCommand() {}

    public void setViewCallback(LoginContract.SignInUpFbGoogleView mViewCallback) {
        this.mViewCallback = mViewCallback;
    }

    public void clearCallback() {
        this.mViewCallback = null;
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
