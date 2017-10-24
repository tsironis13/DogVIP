package com.tsiro.dogvip.login;


import com.tsiro.dogvip.POJO.BaseResponseObj;
import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.POJO.login.LoginResponse;
import com.tsiro.dogvip.POJO.login.SignInEmailRequest;
import com.tsiro.dogvip.POJO.login.SignInUpFbGoogleRequest;
import com.tsiro.dogvip.POJO.login.signup.SignUpEmailRequest;
import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
import com.tsiro.dogvip.POJO.registration.RegistrationRequest;
import com.tsiro.dogvip.POJO.signin.SignInRequest;
import com.tsiro.dogvip.app.Lifecycle;

/**
 * Created by giannis on 22/5/2017.
 */

public interface LoginContract {

    interface View extends Lifecycle.View {
        void onError(int resource);
        void onProcessing();
        void onStopProcessing();
    }

    interface SignInUpView extends View {
        void onSuccessFbLogin(LoginResponse response);
        void onSuccessGoogleLogin(LoginResponse response);
        void onErrorFbLogin(int resource);
        void onErrorGoogleLogin(int resource);
    }

    interface SignInView extends SignInUpView {
        void onSuccessEmailSignIn(LoginResponse response);
    }

    interface SignUpView extends SignInUpView {
        void onSuccessEmailSignUp(BaseResponseObj response);
    }

    interface ForgotPassView extends View {
        void onSuccess(AuthenticationResponse response);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void onProcessing();
        void signInUpGoogle(SignInUpFbGoogleRequest request);
        void signInUpFb(SignInUpFbGoogleRequest request);
        void signUpEmail(SignUpEmailRequest request);
        void signInEmail(SignInEmailRequest request);
        void forgotPass(ForgotPaswrdObj request);
        void setRequestState(int state);
    }

}
