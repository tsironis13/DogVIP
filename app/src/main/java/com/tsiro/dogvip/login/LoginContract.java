package com.tsiro.dogvip.login;


import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.tsiro.dogvip.POJO.BaseResponseObj;
import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.POJO.login.LoginResponse;
import com.tsiro.dogvip.POJO.login.SignInEmailRequest;
import com.tsiro.dogvip.POJO.login.SignInUpFbGoogleRequest;
import com.tsiro.dogvip.POJO.login.forgotpass.ForgotPassRequest;
import com.tsiro.dogvip.POJO.login.forgotpass.ForgotPassResponse;
import com.tsiro.dogvip.POJO.login.signup.SignUpEmailRequest;
import com.tsiro.dogvip.app.Lifecycle;

import javax.inject.Inject;

/**
 * Created by giannis on 22/5/2017.
 */

public interface LoginContract {

    interface View extends Lifecycle.View {
        void onError(int resource);
        void onProcessing();
        void onStopProcessing();
    }

    interface SignInUpFbGoogleView extends View {
        void onSuccessFbLogin(LoginResponse response);
        void onSuccessGoogleLogin(LoginResponse response);
        void onErrorFbLogin(int resource);
        void onErrorGoogleLogin(int resource);
    }

    interface SignInView extends SignInUpFbGoogleView {
        void onSuccessEmailSignIn(LoginResponse response);
    }

    interface SignUpView extends SignInUpFbGoogleView {
        void onSuccessEmailSignUp(BaseResponseObj response);
    }

    interface ForgotPassView extends View {
        void onSuccessIsEmailValid(ForgotPassResponse response);
        void onSuccessNewPassChange(BaseResponseObj response);
        void onError(int resource);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void onProcessing();
        void setRequestState(int state);
    }

    interface SignInViewModel extends ViewModel {
        void handleGoogleSignInResult(GoogleSignInResult result, SignInUpFbGoogleRequest request);
        void handleFbSignInResult(String email, SignInUpFbGoogleRequest request);
        void signInEmail(SignInEmailRequest request);
    }

    interface SignUpViewModel extends ViewModel {
        void handleGoogleSignUpResult(GoogleSignInResult result, SignInUpFbGoogleRequest request);
        void handleFbSignUpResult(String email, SignInUpFbGoogleRequest request);
        void signUpEmail(SignUpEmailRequest request);
    }

    interface ForgotPassViewModel extends ViewModel {
        void handleUserInputAction(ForgotPassRequest request);
//        void forgotPass(ForgotPaswrdObj request);
    }

}
