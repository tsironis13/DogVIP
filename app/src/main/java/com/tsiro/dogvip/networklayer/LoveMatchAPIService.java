package com.tsiro.dogvip.networklayer;

import com.tsiro.dogvip.POJO.lovematch.LoveMatchRequest;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchResponse;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.lovematch.viewmodel.LoveMatchViewModel;
import com.tsiro.dogvip.retrofit.ServiceAPI;

import org.reactivestreams.Subscription;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by giannis on 2/7/2017.
 */

public class LoveMatchAPIService {

    private ServiceAPI serviceAPI;

    @Inject
    public LoveMatchAPIService(ServiceAPI serviceAPI) {
        this.serviceAPI = serviceAPI;
    }

    public Flowable<LoveMatchResponse> getPetsByFilter(final LoveMatchRequest request, final LoveMatchViewModel loveMatchViewModel) {

        return serviceAPI.getPetsByFilter(request)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        loveMatchViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        loveMatchViewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<LoveMatchResponse>() {
                    @Override
                    public void accept(@NonNull LoveMatchResponse response) throws Exception {
                        loveMatchViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

}
