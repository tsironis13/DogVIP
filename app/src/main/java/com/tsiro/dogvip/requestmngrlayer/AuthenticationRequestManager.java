package com.tsiro.dogvip.requestmngrlayer;

import android.content.Context;

import com.tsiro.dogvip.POJO.registration.RegistrationRequest;
import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
import com.tsiro.dogvip.networklayer.RegistrationAPIService;
import com.tsiro.dogvip.register.RegistrationViewModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

/**
 * Created by giannis on 23/5/2017.
 */

public class AuthenticationRequestManager {

    private static AuthenticationRequestManager mInstance;
    private RegistrationAPIService mRegistrationAPIService;

    private AuthenticationRequestManager() {
        this.mRegistrationAPIService = new RegistrationAPIService();
    }

    public static AuthenticationRequestManager getInstance() {
        if (mInstance == null) mInstance = new AuthenticationRequestManager();
        return mInstance;
    }

    public Flowable<AuthenticationResponse> register(RegistrationRequest request, RegistrationViewModel registrationViewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mRegistrationAPIService.register(request, registrationViewModel).delay(500, TimeUnit.MILLISECONDS);
    }

}
