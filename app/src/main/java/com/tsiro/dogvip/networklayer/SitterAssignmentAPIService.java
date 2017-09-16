package com.tsiro.dogvip.networklayer;

import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.POJO.petsitter.SearchedSittersResponse;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.petsitters.petsitter.PetSitterViewModel;
import com.tsiro.dogvip.petsitters.sitter_assignment.SitterAssignmentViewModel;
import com.tsiro.dogvip.retrofit.RetrofitFactory;
import com.tsiro.dogvip.retrofit.ServiceAPI;

import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by thomatou on 9/13/17.
 */

public class SitterAssignmentAPIService {

    private ServiceAPI serviceAPI;

    public SitterAssignmentAPIService() {
        serviceAPI = RetrofitFactory.getInstance().getServiceAPI();
    }

    public Flowable<SearchedSittersResponse> searchSitters(final PetSitterObj request, final SitterAssignmentViewModel sitterAssignmentViewModel) {
        return serviceAPI.searchSitters(request)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        sitterAssignmentViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        sitterAssignmentViewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<SearchedSittersResponse>() {
                    @Override
                    public void accept(@NonNull SearchedSittersResponse response) throws Exception {
                        sitterAssignmentViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

}
