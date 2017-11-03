package com.tsiro.dogvip.responsecontroller.login.forgotpass;

import com.tsiro.dogvip.POJO.Response;
import com.tsiro.dogvip.login.LoginContract;
import com.tsiro.dogvip.responsecontroller.Command;

import javax.inject.Inject;

/**
 * Created by giannis on 1/11/2017.
 */

public class ValidateForgotPassEmailCommand implements Command {

    private LoginContract.ForgotPassView mViewCallback;

    @Inject
    public ValidateForgotPassEmailCommand() {}

    public void setViewCallback(LoginContract.ForgotPassView mViewCallback) {
        this.mViewCallback = mViewCallback;
    }

    public void clearCallback() {
        this.mViewCallback = null;
    }

    @Override
    public void executeOnSuccess(Response response) {
        mViewCallback.onSuccessIsEmailValid(response.getForgotPass());
    }

    @Override
    public void executeOnError(int resource) {
        mViewCallback.onError(resource);
    }
}
