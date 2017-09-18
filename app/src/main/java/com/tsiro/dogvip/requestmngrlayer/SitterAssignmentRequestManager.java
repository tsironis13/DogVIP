package com.tsiro.dogvip.requestmngrlayer;

import com.tsiro.dogvip.POJO.petsitter.BookingObj;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsRequest;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsResponse;
import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.POJO.petsitter.SearchedSittersResponse;
import com.tsiro.dogvip.networklayer.PetSitterAPIService;
import com.tsiro.dogvip.networklayer.SitterAssignmentAPIService;
import com.tsiro.dogvip.petsitters.PetSittersViewModel;
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

    public Flowable<SearchedSittersResponse> searchSitters(PetSitterObj request, SitterAssignmentViewModel sitterAssignmentViewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mSitterAssignmentAPIService.searchSitters(request, sitterAssignmentViewModel).delay(500, TimeUnit.MILLISECONDS);
    }

    public Flowable<SearchedSittersResponse> sendBooking(BookingObj request, SitterAssignmentViewModel sitterAssignmentViewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mSitterAssignmentAPIService.sendBooking(request, sitterAssignmentViewModel).delay(500, TimeUnit.MILLISECONDS);
    }
}
