package com.tsiro.dogvip.requestmngrlayer;

import android.util.Log;

import com.tsiro.dogvip.POJO.lovematch.LoveMatchRequest;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchResponse;
import com.tsiro.dogvip.lovematch.LoveMatchViewModel;
import com.tsiro.dogvip.networklayer.LoveMatchAPIService;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

/**
 * Created by giannis on 2/7/2017.
 */

public class LoveMatchRequestManager {

    private static LoveMatchRequestManager mInstance;
    private LoveMatchAPIService mLoveMatchAPIService;

    private LoveMatchRequestManager() {
        this.mLoveMatchAPIService = new LoveMatchAPIService();
    }

    public static LoveMatchRequestManager getInstance() {
        if (mInstance == null) mInstance = new LoveMatchRequestManager();
        return mInstance;
    }

    public Flowable<LoveMatchResponse> getPetsByFilter(LoveMatchRequest request, LoveMatchViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mLoveMatchAPIService.getPetsByFilter(request, viewModel).delay(500, TimeUnit.MILLISECONDS);
    }

}
