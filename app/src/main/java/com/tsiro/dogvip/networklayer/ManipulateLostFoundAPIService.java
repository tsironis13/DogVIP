package com.tsiro.dogvip.networklayer;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.lostfound.ManipulateLostFoundPet;
import com.tsiro.dogvip.POJO.lostfound.ManipulateLostFoundPetResponse;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.lostfound.manipulatefoundpet.ManipulateFoundPetViewModel;
import com.tsiro.dogvip.lostfound.manipulatelostpet.ManipulateLostPetViewModel;
import com.tsiro.dogvip.mypets.owner.OwnerViewModel;
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
 * Created by giannis on 11/7/2017.
 */

public class ManipulateLostFoundAPIService {

    private ServiceAPI serviceAPI;

    public ManipulateLostFoundAPIService() {
        serviceAPI = RetrofitFactory.getInstance().getServiceAPI();
    }

    public Flowable<ManipulateLostFoundPetResponse> manipulateLostFound(final ManipulateLostFoundPet request, final ManipulateLostPetViewModel viewModel) {
        return serviceAPI.manipulateLostFound(request)
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
                .doOnNext(new Consumer<ManipulateLostFoundPetResponse>() {
                    @Override
                    public void accept(@NonNull ManipulateLostFoundPetResponse response) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

    public Flowable<ManipulateLostFoundPetResponse> manipulateFoundFound(final ManipulateLostFoundPet request, final ManipulateFoundPetViewModel viewModel) {
        return serviceAPI.manipulateLostFound(request)
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
                .doOnNext(new Consumer<ManipulateLostFoundPetResponse>() {
                    @Override
                    public void accept(@NonNull ManipulateLostFoundPetResponse response) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

    public Flowable<Image> uploadImage(RequestBody action, RequestBody token, RequestBody id, MultipartBody.Part image, final ManipulateFoundPetViewModel viewModel) {
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

    public Flowable<Image> deleteImage(Image image, final ManipulateFoundPetViewModel viewModel) {
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

}
