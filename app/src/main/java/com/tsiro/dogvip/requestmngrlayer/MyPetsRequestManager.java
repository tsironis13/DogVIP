package com.tsiro.dogvip.requestmngrlayer;

import com.tsiro.dogvip.POJO.mypets.GetOwnerRequest;
import com.tsiro.dogvip.POJO.mypets.GetOwnerResponse;
import com.tsiro.dogvip.mypets.GetOwnerViewModel;
import com.tsiro.dogvip.networklayer.MyPetsAPIService;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

/**
 * Created by giannis on 4/6/2017.
 */

public class MyPetsRequestManager {

    private static MyPetsRequestManager mInstance;
    private MyPetsAPIService mMyPetsAPIService;

    private MyPetsRequestManager() {
        this.mMyPetsAPIService = new MyPetsAPIService();
    }

    public static MyPetsRequestManager getInstance() {
        if (mInstance == null) mInstance = new MyPetsRequestManager();
        return mInstance;
    }

    public Flowable<GetOwnerResponse> getOwnerDetails(GetOwnerRequest request, GetOwnerViewModel getOwnerViewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mMyPetsAPIService.getOwnerDetails(request, getOwnerViewModel).delay(500, TimeUnit.MILLISECONDS);
    }

}
