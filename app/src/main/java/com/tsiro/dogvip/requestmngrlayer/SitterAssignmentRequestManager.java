package com.tsiro.dogvip.requestmngrlayer;

import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.POJO.petsitter.SearchedSittersResponse;
import com.tsiro.dogvip.networklayer.PetSitterAPIService;
import com.tsiro.dogvip.networklayer.SitterAssignmentAPIService;
import com.tsiro.dogvip.petsitters.petsitter.PetSitterViewModel;
import com.tsiro.dogvip.petsitters.sitter_assignment.SitterAssignmentViewModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

/**
 * Created by thomatou on 9/13/17.
 */

public class SitterAssignmentRequestManager {

    private static SitterAssignmentRequestManager mInstance;
    private SitterAssignmentAPIService mSitterAssignmentAPIService;

    private SitterAssignmentRequestManager() {
        this.mSitterAssignmentAPIService = new SitterAssignmentAPIService();
    }

    public static SitterAssignmentRequestManager getInstance() {
        if (mInstance == null) mInstance = new SitterAssignmentRequestManager();
        return mInstance;
    }

    public Flowable<SearchedSittersResponse> searchSitter(PetSitterObj request, SitterAssignmentViewModel sitterAssignmentViewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mSitterAssignmentAPIService.searchSitter(request, sitterAssignmentViewModel).delay(500, TimeUnit.MILLISECONDS);
    }

}
