package com.tsiro.dogvip.requestmngrlayer;

import android.content.Context;

import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
import com.tsiro.dogvip.POJO.signin.SignInRequest;
import com.tsiro.dogvip.login.SignInViewModel;
import com.tsiro.dogvip.networklayer.SignInAPIService;

import io.reactivex.Flowable;

/**
 * Created by giannis on 28/5/2017.
 */

public class SignInRequestManager {

    private static SignInRequestManager mInstance;
    private SignInAPIService mSignInAPIService;

    private SignInRequestManager(Context context) {
        this.mSignInAPIService = new SignInAPIService();
    }

    public static SignInRequestManager getInstance(Context context) {
        if (mInstance == null) mInstance = new SignInRequestManager(context);
        return mInstance;
    }

    public Flowable<AuthenticationResponse> signin(SignInRequest request, SignInViewModel registrationViewModel) {
        return mSignInAPIService.signin(request, registrationViewModel);
    }

}
