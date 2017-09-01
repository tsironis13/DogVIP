package com.tsiro.dogvip.lostfound.manipulatefoundpet;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.lostfound.ManipulateLostFoundPet;
import com.tsiro.dogvip.POJO.lostfound.ManipulateLostFoundPetResponse;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.lostfound.LostFoundViewModel;
import com.tsiro.dogvip.lostfound.manipulatelostpet.ManipulateLostPetContract;
import com.tsiro.dogvip.lostfound.manipulatelostpet.ManipulateLostPetViewModel;
import com.tsiro.dogvip.mypets.owner.OwnerContract;
import com.tsiro.dogvip.requestmngrlayer.ManipulateLostFoundRequestManager;
import com.tsiro.dogvip.utilities.ImageUploadSubscriber;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by giannis on 13/7/2017.
 */

public class ManipulateFoundPetViewModel implements ManipulateFoundPetContract.ViewModel, Lifecycle.ImageUploadModel {

    private ManipulateFoundPetContract.View mViewClback, mViewImageUploadClback;
    private ManipulateLostFoundRequestManager mManipulateLostFoundRequestManager;
    private AsyncProcessor<ManipulateLostFoundPetResponse> mProcessor;
    private AsyncProcessor<Image> imageProcessor;
    private int requestState;
    private Disposable mDisp, mUploadDisp;

    public ManipulateFoundPetViewModel(ManipulateLostFoundRequestManager requestManager) {
        this.mManipulateLostFoundRequestManager = requestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewClback = (ManipulateFoundPetContract.View) viewCallback;
        this.mViewImageUploadClback = (ManipulateFoundPetContract.View) viewCallback;
    }

    @Override
    public void onViewResumed() {
        if (mDisp != null && requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ManipulateLostFoundObserver());
        }
        if (mUploadDisp != null && requestState != AppConfig.REQUEST_RUNNING && imageProcessor != null) imageProcessor.subscribe(new ImageUploadSubscriber(this));
    }

    @Override
    public void onViewDetached() {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            mViewImageUploadClback = null;
            if (mUploadDisp != null) mUploadDisp.dispose();
        }
        mViewClback = null;
        if (mDisp != null) mDisp.dispose();
    }

    @Override
    public void uploadImage(RequestBody action, RequestBody token, RequestBody id, MultipartBody.Part image) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            imageProcessor = AsyncProcessor.create();
            mUploadDisp = imageProcessor.subscribeWith(new ImageUploadSubscriber(this));

            mManipulateLostFoundRequestManager.uploadImage(action, token, id, image, this).subscribe(imageProcessor);
        }
    }

    @Override
    public void deleteImage(Image image) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            imageProcessor = AsyncProcessor.create();
            mUploadDisp = imageProcessor.subscribeWith(new ImageUploadSubscriber(this));

            mManipulateLostFoundRequestManager.deleteImage(image, this).subscribe(imageProcessor);
        }
    }

    @Override
    public void manipulateFoundPet(ManipulateLostFoundPet request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            requestState = AppConfig.REQUEST_RUNNING;
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new ManipulateLostFoundObserver());

            mManipulateLostFoundRequestManager.manipulateFoundFound(request, this).subscribe(mProcessor);
        }
    }

    @Override
    public void setRequestState(int state) {
        requestState = state;
    }

    @Override
    public void onSuccessImageAction(Image image) {
        mUploadDisp = null;
        //hack for devices which call onDestroy method (OwnerFrgmt should be moved in MyPetsActivity)
        if (mViewImageUploadClback !=null)mViewImageUploadClback.onImageActionSuccess(image);
    }

    @Override
    public void onErrorImageAction() {
        mUploadDisp = null;
        mViewImageUploadClback.onImageActionError();
        if (mViewImageUploadClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private void onSuccessAction(ManipulateLostFoundPetResponse response) {
        mDisp = null;
        mViewClback.onSuccess(response);
    }

    private void onErrorAction(int code) {
        mDisp = null;
        mViewClback.onError(code);
        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private class ManipulateLostFoundObserver extends DisposableSubscriber<ManipulateLostFoundPetResponse> {

        @Override
        public void onNext(ManipulateLostFoundPetResponse response) {
            if (response.getCode() != AppConfig.STATUS_OK) {
                onErrorAction(AppConfig.getCodes().get(response.getCode()));
            } else {
                onSuccessAction(response);
            }
        }

        @Override
        public void onError(Throwable t) {
            onErrorAction(AppConfig.getCodes().get(AppConfig.STATUS_ERROR));
        }

        @Override
        public void onComplete() {

        }
    }

}
