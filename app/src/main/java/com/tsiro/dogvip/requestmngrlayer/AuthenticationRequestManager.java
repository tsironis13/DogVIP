package com.tsiro.dogvip.requestmngrlayer;

import android.content.Context;

import com.tsiro.dogvip.POJO.registration.RegistrationRequest;
import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
import com.tsiro.dogvip.networklayer.RegistrationAPIService;
import com.tsiro.dogvip.register.RegistrationViewModel;

import io.reactivex.Flowable;

/**
 * Created by giannis on 23/5/2017.
 */

public class AuthenticationRequestManager {

    private static AuthenticationRequestManager mInstance;
    private RegistrationAPIService mRegistrationAPIService;

    private AuthenticationRequestManager(Context context) {
        this.mRegistrationAPIService = new RegistrationAPIService();
    }

    public static AuthenticationRequestManager getInstance(Context context) {
        if (mInstance == null) mInstance = new AuthenticationRequestManager(context);
        return mInstance;
    }

    public Flowable<AuthenticationResponse> register(RegistrationRequest request, RegistrationViewModel registrationViewModel) {
        return mRegistrationAPIService.register(request, registrationViewModel);
    }

}
