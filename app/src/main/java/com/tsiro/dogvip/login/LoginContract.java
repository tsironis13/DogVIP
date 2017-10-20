package com.tsiro.dogvip.login;


import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
import com.tsiro.dogvip.POJO.registration.RegistrationRequest;
import com.tsiro.dogvip.POJO.signin.SignInRequest;
import com.tsiro.dogvip.app.Lifecycle;

/**
 * Created by giannis on 22/5/2017.
 */

public interface LoginContract {

    interface View extends Lifecycle.View {
        void onSuccess(AuthenticationResponse response);
        void onError(int resource, boolean msglength);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void signIn(SignInRequest request);
        void signUp(RegistrationRequest request);
        void forgotPass(ForgotPaswrdObj request);
        void setRequestState(int state);
    }

}
