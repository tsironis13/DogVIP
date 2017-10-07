package com.tsiro.dogvip.uploadimagecontrol;

import android.util.Log;
import android.view.View;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.mypets.owner.OwnerViewModel;
import com.tsiro.dogvip.requestmngrlayer.ImageUploadControlRequestManager;
import com.tsiro.dogvip.utilities.ImageUploadSubscriber;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by giannis on 25/6/2017.
 */

public class ImageUploadControlViewModel implements ImageUploadControlContract.ViewModel, Lifecycle.ImageUploadModel {

    private static final String debugTag = ImageUploadControlViewModel.class.getSimpleName();
    private ImageUploadControlRequestManager mImageUploadControlRequestManager;
    private ImageUploadControlContract.View mViewClback;
    private Disposable mDisp, mManipulatePetImageDisp;
    private AsyncProcessor<Image> imageProcessor, manipulatePetImageProcessor;
    private int requestState;

    public ImageUploadControlViewModel(ImageUploadControlRequestManager imageUploadControlRequestManager) {
        this.mImageUploadControlRequestManager = imageUploadControlRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewClback = (ImageUploadControlContract.View) viewCallback;
    }

    @Override
    public void onViewResumed() {
        if (mDisp != null && requestState != AppConfig.REQUEST_RUNNING && imageProcessor != null){
            imageProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ImageUploadSubscriber(this));
        }
        if (mManipulatePetImageDisp != null && requestState != AppConfig.REQUEST_RUNNING && manipulatePetImageProcessor != null)
                manipulatePetImageProcessor
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new ManipulatePetImageObserver());
    }

    @Override
    public void onViewDetached() {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            if (mDisp != null) mDisp.dispose();
            mViewClback = null;
        }
        if (mManipulatePetImageDisp != null) mManipulatePetImageDisp.dispose();
    }

    @Override
    public void uploadImage(RequestBody action, RequestBody token, RequestBody id, MultipartBody.Part image) {}

    @Override
    public void uploadPetSitterPlaceImage(RequestBody action, RequestBody token, RequestBody id, MultipartBody.Part image, RequestBody index) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            requestState = AppConfig.REQUEST_RUNNING;
            imageProcessor = AsyncProcessor.create();
            mDisp = imageProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new ImageUploadSubscriber(this));

            mImageUploadControlRequestManager.uploadPetSitterPlaceImage(action, token, id, image, index, this).subscribe(imageProcessor);
        }
    }

    @Override
    public void uploadPetImage(RequestBody action, RequestBody token, RequestBody user_role_id, RequestBody pet_id, MultipartBody.Part image, RequestBody index) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            requestState = AppConfig.REQUEST_RUNNING;
            imageProcessor = AsyncProcessor.create();
            mDisp = imageProcessor
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new ImageUploadSubscriber(this));

            mImageUploadControlRequestManager.uploadPetImage(action, token, user_role_id, pet_id, image, index, this).subscribe(imageProcessor);
        }
    }

    @Override
    public void manipulatePetImage(Image image) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            requestState = AppConfig.REQUEST_RUNNING;
            manipulatePetImageProcessor = AsyncProcessor.create();
            mManipulatePetImageDisp = manipulatePetImageProcessor
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribeWith(new ManipulatePetImageObserver());

            mImageUploadControlRequestManager.deletePetImage(image, this).subscribe(manipulatePetImageProcessor);
        }
    }

    @Override
    public void setRequestState(int state) {
        requestState = state;
    }

    @Override
    public void onSuccessImageAction(Image image) {
        mDisp = null;
        if (image.getCode() != AppConfig.STATUS_OK) {
            mViewClback.onError(AppConfig.getCodes().get(image.getCode()));
        } else {
            mViewClback.onSuccess(image);
        }
    }

    @Override
    public void onErrorImageAction() {
        mDisp = null;
        mViewClback.onError(AppConfig.getCodes().get(AppConfig.STATUS_ERROR));
        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private void onSuccessImageManipulationAction(Image response) {
        mManipulatePetImageDisp = null;
        mViewClback.onSuccess(response);
    }

    private void onErrorImageManipulationAction(int resource) {
        mManipulatePetImageDisp = null;
        mViewClback.onError(resource);
        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private class ManipulatePetImageObserver extends DisposableSubscriber<Image> {

        @Override
        public void onNext(@NonNull Image response) {
            if (response.getCode() != AppConfig.STATUS_OK) {
                onErrorImageManipulationAction(AppConfig.getCodes().get(response.getCode()));
            } else {
                onSuccessImageManipulationAction(response);
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {
            onErrorImageManipulationAction(AppConfig.getCodes().get(AppConfig.STATUS_ERROR));
        }

        @Override
        public void onComplete() {
        }
    }
}
