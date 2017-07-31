package com.tsiro.dogvip.mypets.owner;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.requestmngrlayer.MyPetsRequestManager;
import com.tsiro.dogvip.utilities.ImageUploadSubscriber;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.subscribers.DisposableSubscriber;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by giannis on 4/6/2017.
 */

public class OwnerViewModel implements OwnerContract.ViewModel, Lifecycle.ImageUploadModel {

    private MyPetsRequestManager mMyPetsRequestManager;
    private OwnerContract.View mViewClback, mViewImageUploadClback;
    private Disposable mDisp, mUploadDisp;
    private AsyncProcessor<OwnerObj> mProcessor;
    private AsyncProcessor<Image> imageProcessor;
    private int requestState;

    public OwnerViewModel(MyPetsRequestManager mMyPetsRequestManager) {
        this.mMyPetsRequestManager = mMyPetsRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewClback = (OwnerContract.View) viewCallback;
        this.mViewImageUploadClback = (OwnerContract.View) viewCallback;
    }

    @Override
    public void onViewResumed() {
        if (mDisp != null && requestState != AppConfig.REQUEST_RUNNING && mProcessor != null) mProcessor.subscribe(new OwnerObserver());
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
    public void submitOwner(OwnerObj request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor.subscribeWith(new OwnerObserver());

            mMyPetsRequestManager.submitOwner(request, this).subscribe(mProcessor);
        }
    }

    @Override
    public void uploadImage(RequestBody action, RequestBody token, RequestBody id, MultipartBody.Part image) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            imageProcessor = AsyncProcessor.create();
            mUploadDisp = imageProcessor.subscribeWith(new ImageUploadSubscriber(this));

            mMyPetsRequestManager.uploadImage(action, token, id, image, this).subscribe(imageProcessor);
        }
    }

    @Override
    public void deleteImage(Image image) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            imageProcessor = AsyncProcessor.create();
            mUploadDisp = imageProcessor.subscribeWith(new ImageUploadSubscriber(this));

            mMyPetsRequestManager.deleteImage(image, this).subscribe(imageProcessor);
        }
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

    @Override
    public void setRequestState(int state) {
        requestState = state;
    }

    private void onGetOwnerSuccess(OwnerObj response) {
        mDisp = null;
        mViewClback.onSuccess(response);
    }

    private void onGetOwnerError(int resource, boolean msglength) {
        mDisp = null;
        mViewClback.onError(resource, msglength);
        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private class OwnerObserver extends DisposableSubscriber<OwnerObj> {

        @Override
        public void onNext(@NonNull OwnerObj response) {
            if (response.getCode() != AppConfig.STATUS_OK) {
                onGetOwnerError(AppConfig.getCodes().get(response.getCode()), response.isStringlength());
            } else {
                onGetOwnerSuccess(response);
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {
            onGetOwnerError(AppConfig.getCodes().get(AppConfig.STATUS_ERROR), false);
        }

        @Override
        public void onComplete() {
        }
    }
}
