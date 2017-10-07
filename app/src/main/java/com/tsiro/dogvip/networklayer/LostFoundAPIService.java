package com.tsiro.dogvip.networklayer;

import com.tsiro.dogvip.POJO.lostfound.LostFoundRequest;
import com.tsiro.dogvip.POJO.lostfound.LostFoundResponse;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.lostfound.LostFoundViewModel;
import com.tsiro.dogvip.retrofit.RetrofitFactory;
import com.tsiro.dogvip.retrofit.ServiceAPI;

import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by giannis on 10/7/2017.
 */

public class LostFoundAPIService {

    private ServiceAPI serviceAPI;

    public LostFoundAPIService() {
        serviceAPI = RetrofitFactory.getInstance().getServiceAPI();
    }

    public Flowable<LostFoundResponse> getLostFound(final LostFoundRequest request, final LostFoundViewModel lostFoundViewModel) {
        return serviceAPI.getLostFound(request)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        lostFoundViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        lostFoundViewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<LostFoundResponse>() {
                    @Override
                    public void accept(@NonNull LostFoundResponse response) throws Exception {
                        lostFoundViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

}
