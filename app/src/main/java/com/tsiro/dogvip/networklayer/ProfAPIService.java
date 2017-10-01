package com.tsiro.dogvip.networklayer;

import com.tsiro.dogvip.POJO.petlikes.PetLikesRequest;
import com.tsiro.dogvip.POJO.petlikes.PetLikesResponse;
import com.tsiro.dogvip.POJO.profs.GetUserProfRequest;
import com.tsiro.dogvip.POJO.profs.GetUserProfResponse;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.petlikes.PetLikesViewModel;
import com.tsiro.dogvip.profs.ProfProfileViewModel;
import com.tsiro.dogvip.retrofit.RetrofitFactory;
import com.tsiro.dogvip.retrofit.ServiceAPI;

import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by giannis on 30/9/2017.
 */

public class ProfAPIService {

    private ServiceAPI serviceAPI;

    public ProfAPIService() {
        serviceAPI = RetrofitFactory.getInstance().getServiceAPI();
    }

    public Flowable<GetUserProfResponse> getUserProf(final GetUserProfRequest request, final ProfProfileViewModel viewModel) {
        return serviceAPI.getUserProf(request)
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
                .doOnNext(new Consumer<GetUserProfResponse>() {
                    @Override
                    public void accept(@NonNull GetUserProfResponse response) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

}
