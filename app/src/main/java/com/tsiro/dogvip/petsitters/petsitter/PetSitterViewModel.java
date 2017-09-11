package com.tsiro.dogvip.petsitters.petsitter;

import android.util.Log;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.mypets.owner.OwnerContract;
import com.tsiro.dogvip.mypets.owner.OwnerViewModel;
import com.tsiro.dogvip.requestmngrlayer.MyPetsRequestManager;
import com.tsiro.dogvip.requestmngrlayer.PetSitterRequestManager;
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
 * Created by giannis on 6/9/2017.
 */

public class PetSitterViewModel implements PetSitterContract.ViewModel, Lifecycle.ImageUploadModel {

    private static final String debugTag = PetSitterViewModel.class.getSimpleName();
    private PetSitterRequestManager mPetSitterRequestManager;
    private PetSitterContract.View mViewClback, mViewImageUploadClback;
    private Disposable mDisp, mUploadDisp;
    private AsyncProcessor<PetSitterObj> mProcessor;
    private AsyncProcessor<Image> imageProcessor;
    private int requestState;

    public PetSitterViewModel(PetSitterRequestManager mPetSitterRequestManager) {
        this.mPetSitterRequestManager = mPetSitterRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
//        Log.e(debugTag, "onAttached");
        this.mViewClback = (PetSitterContract.View) viewCallback;
        this.mViewImageUploadClback = (PetSitterContract.View) viewCallback;
    }

    @Override
    public void onViewResumed() {
//        Log.e(debugTag, "onViewResumed");
//        Log.e(debugTag, "DISP => "+mUploadDisp + " REQUEST STATE => "+requestState+ " PROCESSOR => "+imageProcessor);
        if (mDisp != null && requestState != AppConfig.REQUEST_RUNNING && mProcessor != null)
            mProcessor
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new PetSitterObserver());
        if (mUploadDisp != null && requestState != AppConfig.REQUEST_RUNNING && imageProcessor != null) {
            imageProcessor.subscribe(new ImageUploadSubscriber(this));
        }
    }

    @Override
    public void onViewDetached() {
//        Log.e(debugTag, "onViewDetached");
        if (requestState != AppConfig.REQUEST_RUNNING) {
            mViewImageUploadClback = null;
            if (mUploadDisp != null) mUploadDisp.dispose();
        }
        mViewClback = null;
        if (mDisp != null) mDisp.dispose();
    }

    @Override
    public void onSuccessImageAction(Image image) {
//        Log.e(debugTag, "onSuccessImageAction");
        mUploadDisp = null;
        mViewImageUploadClback.onImageActionSuccess(image);
    }

    @Override
    public void onErrorImageAction() {
        mUploadDisp = null;
        mViewImageUploadClback.onImageActionError();
        if (mViewImageUploadClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    @Override
    public void petSitterRelatedActions(PetSitterObj petSitterObj) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new PetSitterObserver());

            mPetSitterRequestManager.petSitterRelatedActions(petSitterObj, this).subscribe(mProcessor);
        }
    }

    @Override
    public void uploadImage(RequestBody action, RequestBody token, RequestBody id, MultipartBody.Part image) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            imageProcessor = AsyncProcessor.create();
            mUploadDisp = imageProcessor.subscribeWith(new ImageUploadSubscriber(this));

            mPetSitterRequestManager.uploadImage(action, token, id, image, this).subscribe(imageProcessor);
        }
    }

    @Override
    public void deleteImage(Image image) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            imageProcessor = AsyncProcessor.create();
            mUploadDisp = imageProcessor.subscribeWith(new ImageUploadSubscriber(this));

            mPetSitterRequestManager.deleteImage(image, this).subscribe(imageProcessor);
        }
    }

    @Override
    public void setRequestState(int state) {
        requestState = state;
    }

    private void onPetSitterSuccess(PetSitterObj response) {
        mDisp = null;
        mViewClback.onSuccess(response);
    }

    private void onPetSitterError(int resource, boolean msglength) {
        mDisp = null;
        mViewClback.onError(resource, msglength);
        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private class PetSitterObserver extends DisposableSubscriber<PetSitterObj> {

        @Override
        public void onNext(@NonNull PetSitterObj response) {
            if (response.getCode() != AppConfig.STATUS_OK) {
                onPetSitterError(AppConfig.getCodes().get(response.getCode()), response.isStringlength());
            } else {
                onPetSitterSuccess(response);
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {
            onPetSitterError(AppConfig.getCodes().get(AppConfig.STATUS_ERROR), false);
        }

        @Override
        public void onComplete() {
        }
    }
}
