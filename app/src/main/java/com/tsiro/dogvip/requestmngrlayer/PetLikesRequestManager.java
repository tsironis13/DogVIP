package com.tsiro.dogvip.requestmngrlayer;

import com.tsiro.dogvip.POJO.petlikes.PetLikesRequest;
import com.tsiro.dogvip.POJO.petlikes.PetLikesResponse;
import com.tsiro.dogvip.networklayer.PetLikesAPIService;
import com.tsiro.dogvip.petlikes.PetLikesViewModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

/**
 * Created by giannis on 5/7/2017.
 */

public class PetLikesRequestManager {

    private static PetLikesRequestManager mInstance;
    private PetLikesAPIService mPetLikesAPIService;

    private PetLikesRequestManager() {
        this.mPetLikesAPIService = new PetLikesAPIService();
    }

    public static PetLikesRequestManager getInstance() {
        if (mInstance == null) mInstance = new PetLikesRequestManager();
        return mInstance;
    }

    public Flowable<PetLikesResponse> getPetLikes(PetLikesRequest request, PetLikesViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mPetLikesAPIService.getPetLikes(request, viewModel).delay(500, TimeUnit.MILLISECONDS);
    }

}
