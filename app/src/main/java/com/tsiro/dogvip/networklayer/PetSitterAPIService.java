package com.tsiro.dogvip.networklayer;

import android.util.Log;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.mypets.OwnerRequest;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsRequest;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsResponse;
import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.mypets.GetOwnerViewModel;
import com.tsiro.dogvip.mypets.owner.OwnerViewModel;
import com.tsiro.dogvip.petsitters.PetSittersViewModel;
import com.tsiro.dogvip.petsitters.petsitter.PetSitterViewModel;
import com.tsiro.dogvip.retrofit.RetrofitFactory;
import com.tsiro.dogvip.retrofit.ServiceAPI;

import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by giannis on 6/9/2017.
 */

public class PetSitterAPIService {

    private ServiceAPI serviceAPI;

    public PetSitterAPIService() {
        serviceAPI = RetrofitFactory.getInstance().getServiceAPI();
    }

    public Flowable<PetSitterObj> petSitterRelatedActions(final PetSitterObj request, final PetSitterViewModel petSitterViewModel) {
        return serviceAPI.petSitterRelatedActions(request)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        petSitterViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        petSitterViewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<PetSitterObj>() {
                    @Override
                    public void accept(@NonNull PetSitterObj response) throws Exception {
                        petSitterViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

    public Flowable<Image> uploadImage(RequestBody action, RequestBody token, RequestBody id, MultipartBody.Part image, final PetSitterViewModel viewModel) {
        return serviceAPI.uploadImage(action, token, id, image)
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
                .doOnNext(new Consumer<Image>() {
                    @Override
                    public void accept(@NonNull Image response) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

    public Flowable<Image> deleteImage(Image image, final PetSitterViewModel viewModel) {
        return serviceAPI.deleteImage(image)
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
                .doOnNext(new Consumer<Image>() {
                    @Override
                    public void accept(@NonNull Image response) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

    public Flowable<OwnerSitterBookingsResponse> getOwnerSitterBookings(OwnerSitterBookingsRequest request, final PetSittersViewModel viewModel) {
        return serviceAPI.getOwnerSitterBookings(request)
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
                .doOnNext(new Consumer<OwnerSitterBookingsResponse>() {
                    @Override
                    public void accept(@NonNull OwnerSitterBookingsResponse response) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

}
