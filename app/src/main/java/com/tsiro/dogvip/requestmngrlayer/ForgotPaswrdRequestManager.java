package com.tsiro.dogvip.requestmngrlayer;

import android.content.Context;

import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.networklayer.ForgotPaswrdAPIService;
import com.tsiro.dogvip.resetpasswrd.ForgotPaswrdViewModel;

import io.reactivex.Flowable;

/**
 * Created by giannis on 29/5/2017.
 */

public class ForgotPaswrdRequestManager {

    private static ForgotPaswrdRequestManager mInstance;
    private ForgotPaswrdAPIService mForgotPaswrdAPIService;

    private ForgotPaswrdRequestManager(Context context) {
        this.mForgotPaswrdAPIService = new ForgotPaswrdAPIService();
    }

    public static ForgotPaswrdRequestManager getInstance(Context context) {
        if (mInstance == null) mInstance = new ForgotPaswrdRequestManager(context);
        return mInstance;
    }

    public Flowable<ForgotPaswrdObj> forgotPaswrd(ForgotPaswrdObj request, ForgotPaswrdViewModel forgotPaswrdViewModel) {
        return mForgotPaswrdAPIService.forgotPaswrd(request, forgotPaswrdViewModel);
    }

}
