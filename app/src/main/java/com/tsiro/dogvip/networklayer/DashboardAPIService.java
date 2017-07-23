package com.tsiro.dogvip.networklayer;

import com.tsiro.dogvip.POJO.dashboard.DashboardRequest;
import com.tsiro.dogvip.POJO.dashboard.DashboardResponse;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.dashboard.DashboardViewModel;
import com.tsiro.dogvip.retrofit.RetrofitFactory;
import com.tsiro.dogvip.retrofit.ServiceAPI;

import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by giannis on 21/7/2017.
 */

public class DashboardAPIService {

    private ServiceAPI serviceAPI;

    public DashboardAPIService() {
        serviceAPI = RetrofitFactory.getInstance().getServiceAPI();
    }

    public Flowable<DashboardResponse> genericDashboardRequest(final DashboardRequest request, final DashboardViewModel viewModel) {

        return serviceAPI.genericDashboardRequest(request)
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
                .doOnNext(new Consumer<DashboardResponse>() {
                    @Override
                    public void accept(@NonNull DashboardResponse response) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

}
