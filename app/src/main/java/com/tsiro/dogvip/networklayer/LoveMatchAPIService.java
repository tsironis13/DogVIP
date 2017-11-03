package com.tsiro.dogvip.networklayer;

import android.util.Log;

import com.tsiro.dogvip.POJO.Response;
import com.tsiro.dogvip.POJO.lovematch.LikeDislikeRequest;
import com.tsiro.dogvip.POJO.lovematch.GetPetsByFilterRequest;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.lovematch.LoveMatchViewModel;
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

//    public Flowable<LoveMatchResponse> getPetsByFilter(final GetPetsByFilterRequest request, final LoveMatchViewModel loveMatchViewModel) {
//
//        return serviceAPI.getPetsByFilter(request)
//                .doOnSubscribe(new Consumer<Subscription>() {
//                    @Override
//                    public void accept(@NonNull Subscription subscription) throws Exception {
//                        loveMatchViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnError(new Consumer<Throwable>() {
//                    @Override
//                    public void accept(@NonNull Throwable throwable) throws Exception {
//                        loveMatchViewModel.setRequestState(AppConfig.REQUEST_FAILED);
//                    }
//                })
//                .doOnNext(new Consumer<LoveMatchResponse>() {
//                    @Override
//                    public void accept(@NonNull LoveMatchResponse response) throws Exception {
//                        loveMatchViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
//                    }
//                });
//    }

    public Flowable<Response> getPetsByFilter(final GetPetsByFilterRequest request, final LoveMatchViewModel loveMatchViewModel) {

        return serviceAPI.getPetsByFilter(request)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
//                        Log.e("REQUEST_RUNNING", "REQUEST_RUNNING");
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
                .doOnNext(new Consumer<Response>() {
                    @Override
                    public void accept(@NonNull Response response) throws Exception {
                        loveMatchViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

    public Flowable<Response> likeDislikePet(final LikeDislikeRequest request, final LoveMatchViewModel loveMatchViewModel) {

        return serviceAPI.likeDislikePet(request)
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
                .doOnNext(new Consumer<Response>() {
                    @Override
                    public void accept(@NonNull Response response) throws Exception {
                        loveMatchViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

}
