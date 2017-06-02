package com.tsiro.dogvip.networklayer;

import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.resetpasswrd.ForgotPaswrdViewModel;
import com.tsiro.dogvip.retrofit.RetrofitFactory;
import com.tsiro.dogvip.retrofit.ServiceAPI;

import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by giannis on 29/5/2017.
 */

public class ForgotPaswrdAPIService {

    private ServiceAPI serviceAPI;

    public ForgotPaswrdAPIService() {
        serviceAPI = RetrofitFactory.getInstance().getServiceAPI();
    }

    public Flowable<ForgotPaswrdObj> forgotPaswrd(ForgotPaswrdObj request, final ForgotPaswrdViewModel forgotPaswrdViewModel) {
        return serviceAPI.forgotPaswrd(request)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        forgotPaswrdViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        forgotPaswrdViewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<ForgotPaswrdObj>() {
                    @Override
                    public void accept(@NonNull ForgotPaswrdObj response) throws Exception {
                        forgotPaswrdViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

}
