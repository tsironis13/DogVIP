package com.tsiro.dogvip.networklayer;

import com.tsiro.dogvip.POJO.registration.RegistrationRequest;
import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.login.signup.RegistrationViewModel;
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
 * Created by giannis on 23/5/2017.
 */

public class RegistrationAPIService {

    private ServiceAPI serviceAPI;

    public RegistrationAPIService() {
        serviceAPI = RetrofitFactory.getInstance().getServiceAPI();
    }

    public Flowable<AuthenticationResponse> register(RegistrationRequest request, final RegistrationViewModel registrationViewModel) {
        return serviceAPI.register(request)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        registrationViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        registrationViewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<AuthenticationResponse>() {
                    @Override
                    public void accept(@NonNull AuthenticationResponse registrationResponse) throws Exception {
                        registrationViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }
}
