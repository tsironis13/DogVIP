package com.tsiro.dogvip.networklayer;

import android.util.Log;

import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
import com.tsiro.dogvip.POJO.signin.SignInRequest;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.login.SignInViewModel;
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
 * Created by giannis on 28/5/2017.
 */

public class SignInAPIService {

    private ServiceAPI serviceAPI;

    public SignInAPIService() {
        serviceAPI = RetrofitFactory.getInstance().getServiceAPI();
    }

    public Flowable<AuthenticationResponse> signin(SignInRequest request, final SignInViewModel signInViewModel) {
        return serviceAPI.signin(request)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        signInViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.e("onError", throwable.toString());
                        signInViewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<AuthenticationResponse>() {
                    @Override
                    public void accept(@NonNull AuthenticationResponse response) throws Exception {
                        signInViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

}
