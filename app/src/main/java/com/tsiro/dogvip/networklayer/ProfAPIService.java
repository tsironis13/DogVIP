package com.tsiro.dogvip.networklayer;

import com.tsiro.dogvip.POJO.BaseResponseObj;
import com.tsiro.dogvip.POJO.profs.DeleteProfRequest;
import com.tsiro.dogvip.POJO.profs.GetProfDetailsRequest;
import com.tsiro.dogvip.POJO.profs.ProfDetailsResponse;
import com.tsiro.dogvip.POJO.profs.SaveProfDetailsRequest;
import com.tsiro.dogvip.POJO.profs.SearchProfsRequest;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.profs.profprofile.ProfProfileViewModel;
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

    public Flowable<ProfDetailsResponse> getProfDetails(final GetProfDetailsRequest request, final ProfProfileViewModel viewModel) {
        return serviceAPI.getProfDetails(request)
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
                .doOnNext(new Consumer<ProfDetailsResponse>() {
                    @Override
                    public void accept(@NonNull ProfDetailsResponse response) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

    public Flowable<ProfDetailsResponse> saveProfDetails(final SaveProfDetailsRequest request, final ProfProfileViewModel viewModel) {
        return serviceAPI.saveProfDetails(request)
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
                .doOnNext(new Consumer<ProfDetailsResponse>() {
                    @Override
                    public void accept(@NonNull ProfDetailsResponse response) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

    public Flowable<ProfDetailsResponse> deleteProf(final DeleteProfRequest request, final ProfProfileViewModel viewModel) {
        return serviceAPI.deleteProf(request)
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
                .doOnNext(new Consumer<ProfDetailsResponse>() {
                    @Override
                    public void accept(@NonNull ProfDetailsResponse response) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

    public Flowable<ProfDetailsResponse> searchProf(final SearchProfsRequest request, final ProfProfileViewModel viewModel) {
        return serviceAPI.searchProf(request)
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
                .doOnNext(new Consumer<ProfDetailsResponse>() {
                    @Override
                    public void accept(@NonNull ProfDetailsResponse response) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

}
