package com.tsiro.dogvip.requestmngrlayer;

import android.content.Context;

import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.networklayer.ForgotPaswrdAPIService;
import com.tsiro.dogvip.resetpasswrd.ForgotPaswrdViewModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

/**
 * Created by giannis on 29/5/2017.
 */

public class ForgotPaswrdRequestManager {

    private static ForgotPaswrdRequestManager mInstance;
    private ForgotPaswrdAPIService mForgotPaswrdAPIService;

    private ForgotPaswrdRequestManager() {
        this.mForgotPaswrdAPIService = new ForgotPaswrdAPIService();
    }

    public static ForgotPaswrdRequestManager getInstance() {
        if (mInstance == null) mInstance = new ForgotPaswrdRequestManager();
        return mInstance;
    }

    public Flowable<ForgotPaswrdObj> forgotPaswrd(ForgotPaswrdObj request, ForgotPaswrdViewModel forgotPaswrdViewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mForgotPaswrdAPIService.forgotPaswrd(request, forgotPaswrdViewModel).delay(500, TimeUnit.MILLISECONDS);
    }

}
