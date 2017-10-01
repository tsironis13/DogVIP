package com.tsiro.dogvip.networklayer;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.mypets.OwnerRequest;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.image_states.ImageUploadViewModel;
import com.tsiro.dogvip.mypets.GetOwnerViewModel;
import com.tsiro.dogvip.mypets.owner.OwnerViewModel;
import com.tsiro.dogvip.mypets.ownerprofile.OwnerProfileViewModel;
import com.tsiro.dogvip.mypets.pet.PetViewModel;
import com.tsiro.dogvip.ownerpets.OwnerPetsViewModel;
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
 * Created by giannis on 4/6/2017.
 */

public class MyPetsAPIService {

    private ServiceAPI serviceAPI;

    public MyPetsAPIService() {
        serviceAPI = RetrofitFactory.getInstance().getServiceAPI();
    }
    public Flowable<OwnerObj> getOwnerDetails(final OwnerRequest request, final GetOwnerViewModel getOwnerViewModel) {
        return serviceAPI.getOwnerDetails(request)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        getOwnerViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        getOwnerViewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<OwnerObj>() {
                    @Override
                    public void accept(@NonNull OwnerObj response) throws Exception {
                        getOwnerViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

    public Flowable<OwnerObj> getOwnerDetails(final OwnerRequest request, final OwnerPetsViewModel viewModel) {
        return serviceAPI.getOwnerDetails(request)
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
                .doOnNext(new Consumer<OwnerObj>() {
                    @Override
                    public void accept(@NonNull OwnerObj response) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

    public Flowable<OwnerObj> submitOwner(OwnerObj request, final OwnerViewModel ownerViewModel) {
        return serviceAPI.submitOwner(request)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        ownerViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        ownerViewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<OwnerObj>() {
                    @Override
                    public void accept(@NonNull OwnerObj response) throws Exception {
                        ownerViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

    public Flowable<Image> uploadImage(RequestBody action, RequestBody token, RequestBody id, MultipartBody.Part image, final ImageUploadViewModel viewModel) {
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

    public Flowable<Image> deleteImage(Image image, final ImageUploadViewModel viewModel) {
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

    public Flowable<OwnerRequest> deleteOwner(OwnerRequest request, final OwnerProfileViewModel ownerProfileViewModel) {
        return serviceAPI.deleteOwner(request)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        ownerProfileViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        ownerProfileViewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<OwnerRequest>() {
                    @Override
                    public void accept(@NonNull OwnerRequest response) throws Exception {
                        ownerProfileViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

    public Flowable<OwnerObj> submitPet(PetObj request, final PetViewModel petViewModel) {
        return serviceAPI.submitPet(request)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        petViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        petViewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<OwnerObj>() {
                    @Override
                    public void accept(@NonNull OwnerObj response) throws Exception {
                        petViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }
//    public Flowable<GetOwnerResponse> submitOwner(RequestBody body, MultipartBody.Part image, final OwnerViewModel ownerViewModel) {
//        return serviceAPI.submitOwner(body, image)
//                .doOnSubscribe(new Consumer<Subscription>() {
//                    @Override
//                    public void accept(@NonNull Subscription subscription) throws Exception {
//                        ownerViewModel.setRequestState(AppConfig.REQUEST_RUNNING);
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnError(new Consumer<Throwable>() {
//                    @Override
//                    public void accept(@NonNull Throwable throwable) throws Exception {
//                        ownerViewModel.setRequestState(AppConfig.REQUEST_FAILED);
//                    }
//                })
//                .doOnNext(new Consumer<GetOwnerResponse>() {
//                    @Override
//                    public void accept(@NonNull GetOwnerResponse response) throws Exception {
//                        ownerViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
//                    }
//                });
//    }
}
