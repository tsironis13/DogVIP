package com.tsiro.dogvip.requestmngrlayer;

import android.util.Log;

import com.tsiro.dogvip.POJO.Response;
import com.tsiro.dogvip.POJO.lovematch.GetPetsByFilterRequest;
import com.tsiro.dogvip.POJO.lovematch.GetPetsResponse;
import com.tsiro.dogvip.POJO.lovematch.LikeDislikeRequest;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.lovematch.LoveMatchViewModel;
import com.tsiro.dogvip.networklayer.LoveMatchAPIService;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 2/7/2017.
 */

public class LoveMatchRequestManager {

    private LoveMatchAPIService mLoveMatchAPIService;
    private static GetPetsResponse getPetsResponse = new GetPetsResponse();
    public static ArrayList<PetObj> list = new ArrayList<>();
    Response loveMatch = new Response();

    @Inject
    public LoveMatchRequestManager(LoveMatchAPIService loveMatchAPIService) {
        this.mLoveMatchAPIService = loveMatchAPIService;


//        list.add(new PetObj().setP_name("name1"));
    }

//    public Flowable<LoveMatchResponse> getPetsByFilter(GetPetsByFilterRequest request, LoveMatchViewModel viewModel) {
//        //in case server response is faster than activity lifecycle callback methods
//        return mLoveMatchAPIService.getPetsByFilter(request, viewModel).delay(500, TimeUnit.MILLISECONDS);
//    }

    public Flowable<Response> getPetsByFilter(GetPetsByFilterRequest request, LoveMatchViewModel viewModel) {
        list = new ArrayList<>();
        //in case server response is faster than activity lifecycle callback methods
        return mLoveMatchAPIService.getPetsByFilter(request, viewModel).doAfterNext(new Consumer<Response>() {
            @Override
            public void accept(@NonNull Response loveMatch) throws Exception {
                //ONLY IF CODE IS 200 SAVE IT LOCALLY !!!!!!!!!!!!!!
//                GetPetsResponse.destale = System.currentTimeMillis();
                Log.e("aaaa", "getPetsByFilter");
                GetPetsResponse.timestamp = System.currentTimeMillis()/1000;
//                getPetsResponse.setState(System.currentTimeMillis());
                setList();
            }
        });
    }

    public Flowable<Response> likeDislikePet(LikeDislikeRequest request, LoveMatchViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mLoveMatchAPIService.likeDislikePet(request, viewModel).delay(500, TimeUnit.MILLISECONDS);
    }

    public static void setList() {
        getPetsResponse = new GetPetsResponse();
        for (int i = 0; i < 3; i++) {
            PetObj petObj = new PetObj();
            petObj.setP_name("ss"+i);
            list.add(petObj);
        }
        getPetsResponse.setData(list);
    }

    public void removeList() {
        list.clear();
    }

    public Flowable<Response> getPetsResponse() {
//        list = new ArrayList<>();
        loveMatch.setCode(200);
        loveMatch.setPetdata(getPetsResponse);
        getPetsResponse.setData(list);
        return Observable.just(loveMatch).toFlowable(BackpressureStrategy.BUFFER);
    }

}
