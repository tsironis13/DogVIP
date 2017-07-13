package com.tsiro.dogvip.requestmngrlayer;

import com.tsiro.dogvip.POJO.lostfound.LostFoundRequest;
import com.tsiro.dogvip.POJO.lostfound.LostFoundResponse;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchRequest;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchResponse;
import com.tsiro.dogvip.lostfound.LostFoundViewModel;
import com.tsiro.dogvip.lovematch.LoveMatchViewModel;
import com.tsiro.dogvip.networklayer.LostFoundAPIService;
import com.tsiro.dogvip.networklayer.LoveMatchAPIService;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

/**
 * Created by giannis on 10/7/2017.
 */

public class LostFoundRequestManager {

    private static LostFoundRequestManager mInstance;
    private LostFoundAPIService mLostFoundAPIService;

    private LostFoundRequestManager() {
        this.mLostFoundAPIService = new LostFoundAPIService();
    }

    public static LostFoundRequestManager getInstance() {
        if (mInstance == null) mInstance = new LostFoundRequestManager();
        return mInstance;
    }

    public Flowable<LostFoundResponse> getLostFound(LostFoundRequest request, LostFoundViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mLostFoundAPIService.getLostFound(request, viewModel).delay(500, TimeUnit.MILLISECONDS);
    }

}
