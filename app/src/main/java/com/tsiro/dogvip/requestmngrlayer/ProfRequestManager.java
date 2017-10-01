package com.tsiro.dogvip.requestmngrlayer;

import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.POJO.profs.GetUserProfRequest;
import com.tsiro.dogvip.POJO.profs.GetUserProfResponse;
import com.tsiro.dogvip.networklayer.PetSitterAPIService;
import com.tsiro.dogvip.networklayer.ProfAPIService;
import com.tsiro.dogvip.petsitters.petsitter.PetSitterViewModel;
import com.tsiro.dogvip.profs.ProfProfileViewModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

/**
 * Created by giannis on 30/9/2017.
 */

public class ProfRequestManager {

    private static ProfRequestManager mInstance;
    private ProfAPIService mProfAPIService;

    private ProfRequestManager() {
        this.mProfAPIService = new ProfAPIService();
    }

    public static ProfRequestManager getInstance() {
        if (mInstance == null) mInstance = new ProfRequestManager();
        return mInstance;
    }

    public Flowable<GetUserProfResponse> getUserProf(GetUserProfRequest request, ProfProfileViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mProfAPIService.getUserProf(request, viewModel).delay(500, TimeUnit.MILLISECONDS);
    }

}
