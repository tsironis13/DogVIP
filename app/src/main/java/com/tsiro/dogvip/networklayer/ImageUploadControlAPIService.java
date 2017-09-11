package com.tsiro.dogvip.networklayer;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.mypets.owner.OwnerViewModel;
import com.tsiro.dogvip.retrofit.RetrofitFactory;
import com.tsiro.dogvip.retrofit.ServiceAPI;
import com.tsiro.dogvip.uploadimagecontrol.ImageUploadControlViewModel;

import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by giannis on 25/6/2017.
 */

public class ImageUploadControlAPIService {

    private ServiceAPI serviceAPI;

    public ImageUploadControlAPIService() {
        serviceAPI = RetrofitFactory.getInstance().getServiceAPI();
    }

    public Flowable<Image> uploadImage(RequestBody action, RequestBody token, RequestBody id, MultipartBody.Part image, final ImageUploadControlViewModel imageUploadControlViewModel) {
        return serviceAPI.uploadImage(action, token, id, image)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        imageUploadControlViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        imageUploadControlViewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<Image>() {
                    @Override
                    public void accept(@NonNull Image response) throws Exception {
                        imageUploadControlViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

    public Flowable<Image> uploadPetImage(RequestBody action, RequestBody token, RequestBody user_role_id, RequestBody pet_id, MultipartBody.Part image, RequestBody index, final ImageUploadControlViewModel imageUploadControlViewModel) {
        return serviceAPI.uploadPetImage(action, token, user_role_id, pet_id, image, index)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        imageUploadControlViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        imageUploadControlViewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<Image>() {
                    @Override
                    public void accept(@NonNull Image response) throws Exception {
                        imageUploadControlViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

    public Flowable<Image> uploadPetSitterPlaceImage(RequestBody action, RequestBody token, RequestBody id, MultipartBody.Part image, RequestBody index, final ImageUploadControlViewModel imageUploadControlViewModel) {
        return serviceAPI.uploadPetSitterPlaceImage(action, token, id, image, index)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        imageUploadControlViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        imageUploadControlViewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<Image>() {
                    @Override
                    public void accept(@NonNull Image response) throws Exception {
                        imageUploadControlViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

    public Flowable<Image> deletePetImage(Image image, final ImageUploadControlViewModel imageUploadControlViewModel) {
        return serviceAPI.deletePetImage(image)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        imageUploadControlViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        imageUploadControlViewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<Image>() {
                    @Override
                    public void accept(@NonNull Image response) throws Exception {
                        imageUploadControlViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

}
