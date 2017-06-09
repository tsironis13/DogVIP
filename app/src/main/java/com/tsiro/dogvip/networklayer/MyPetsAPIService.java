package com.tsiro.dogvip.networklayer;

import android.util.Log;

import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.POJO.mypets.GetOwnerRequest;
import com.tsiro.dogvip.POJO.mypets.GetOwnerResponse;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.mypets.GetOwnerViewModel;
import com.tsiro.dogvip.retrofit.RetrofitFactory;
import com.tsiro.dogvip.retrofit.ServiceAPI;

import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by giannis on 4/6/2017.
 */

public class MyPetsAPIService {

    private ServiceAPI serviceAPI;

    public MyPetsAPIService() {
        serviceAPI = RetrofitFactory.getInstance().getServiceAPI();
    }
    public Flowable<GetOwnerResponse> getOwnerDetails(final GetOwnerRequest request, final GetOwnerViewModel getOwnerViewModel) {
        return serviceAPI.getOwnerDetails(request)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        getOwnerViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        getOwnerViewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<GetOwnerResponse>() {
                    @Override
                    public void accept(@NonNull GetOwnerResponse response) throws Exception {
                        getOwnerViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }


}
