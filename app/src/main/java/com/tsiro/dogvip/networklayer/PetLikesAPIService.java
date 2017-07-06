package com.tsiro.dogvip.networklayer;

import com.tsiro.dogvip.POJO.petlikes.PetLikesRequest;
import com.tsiro.dogvip.POJO.petlikes.PetLikesResponse;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.petlikes.PetLikesViewModel;
import com.tsiro.dogvip.retrofit.RetrofitFactory;
import com.tsiro.dogvip.retrofit.ServiceAPI;

import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by giannis on 5/7/2017.
 */

public class PetLikesAPIService {

    private ServiceAPI serviceAPI;

    public PetLikesAPIService() {
        serviceAPI = RetrofitFactory.getInstance().getServiceAPI();
    }
    public Flowable<PetLikesResponse> getPetLikes(final PetLikesRequest request, final PetLikesViewModel viewModel) {
        return serviceAPI.getPetLikes(request)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<PetLikesResponse>() {
                    @Override
                    public void accept(@NonNull PetLikesResponse response) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

}
