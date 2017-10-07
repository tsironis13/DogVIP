package com.tsiro.dogvip.requestmngrlayer;

import com.tsiro.dogvip.POJO.BaseResponseObj;
import com.tsiro.dogvip.POJO.profs.DeleteProfRequest;
import com.tsiro.dogvip.POJO.profs.GetProfDetailsRequest;
import com.tsiro.dogvip.POJO.profs.ProfDetailsResponse;
import com.tsiro.dogvip.POJO.profs.SaveProfDetailsRequest;
import com.tsiro.dogvip.POJO.profs.SearchProfsRequest;
import com.tsiro.dogvip.networklayer.ProfAPIService;
import com.tsiro.dogvip.profs.profprofile.ProfProfileViewModel;

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

    public Flowable<ProfDetailsResponse> getProfDetails(GetProfDetailsRequest request, ProfProfileViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mProfAPIService.getProfDetails(request, viewModel).delay(500, TimeUnit.MILLISECONDS);
    }

    public Flowable<ProfDetailsResponse> saveProfDetails(SaveProfDetailsRequest request, ProfProfileViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mProfAPIService.saveProfDetails(request, viewModel).delay(500, TimeUnit.MILLISECONDS);
    }

    public Flowable<ProfDetailsResponse> deleteProf(DeleteProfRequest request, ProfProfileViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mProfAPIService.deleteProf(request, viewModel).delay(500, TimeUnit.MILLISECONDS);
    }

    public Flowable<ProfDetailsResponse> searchProf(SearchProfsRequest request, ProfProfileViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mProfAPIService.searchProf(request, viewModel).delay(500, TimeUnit.MILLISECONDS);
    }

}
