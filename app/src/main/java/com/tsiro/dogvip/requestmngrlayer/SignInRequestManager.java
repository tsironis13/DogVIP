package com.tsiro.dogvip.requestmngrlayer;

import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
import com.tsiro.dogvip.POJO.signin.SignInRequest;
import com.tsiro.dogvip.login.signin.SignInViewModel;
import com.tsiro.dogvip.networklayer.SignInAPIService;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

/**
 * Created by giannis on 28/5/2017.
 */

public class SignInRequestManager {

    private static SignInRequestManager mInstance;
    private SignInAPIService mSignInAPIService;

    private SignInRequestManager() {
        this.mSignInAPIService = new SignInAPIService();
    }

    public static SignInRequestManager getInstance() {
        if (mInstance == null) mInstance = new SignInRequestManager();
        return mInstance;
    }

    public Flowable<AuthenticationResponse> signin(SignInRequest request, SignInViewModel registrationViewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mSignInAPIService.signin(request, registrationViewModel).delay(500, TimeUnit.MILLISECONDS);
    }

}
