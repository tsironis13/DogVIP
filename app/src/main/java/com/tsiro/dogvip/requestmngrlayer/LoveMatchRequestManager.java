package com.tsiro.dogvip.requestmngrlayer;

import com.tsiro.dogvip.POJO.lovematch.LoveMatch;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchRequest;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchResponse;
import com.tsiro.dogvip.lovematch.viewmodel.LoveMatchViewModel;
import com.tsiro.dogvip.networklayer.LoveMatchAPIService;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Flowable;

/**
 * Created by giannis on 2/7/2017.
 */

public class LoveMatchRequestManager {

    private LoveMatchAPIService mLoveMatchAPIService;

    @Inject
    public LoveMatchRequestManager(LoveMatchAPIService loveMatchAPIService) {
        this.mLoveMatchAPIService = loveMatchAPIService;
    }

//    public Flowable<LoveMatchResponse> getPetsByFilter(LoveMatchRequest request, LoveMatchViewModel viewModel) {
//        //in case server response is faster than activity lifecycle callback methods
//        return mLoveMatchAPIService.getPetsByFilter(request, viewModel).delay(500, TimeUnit.MILLISECONDS);
//    }

    public Flowable<LoveMatch> getPetsByFilter(LoveMatchRequest request, LoveMatchViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mLoveMatchAPIService.getPetsByFilter(request, viewModel).delay(500, TimeUnit.MILLISECONDS);
    }

    public Flowable<LoveMatch> likeDislikePet(LoveMatchRequest request, LoveMatchViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mLoveMatchAPIService.likeDislikePet(request, viewModel).delay(500, TimeUnit.MILLISECONDS);
    }

}
