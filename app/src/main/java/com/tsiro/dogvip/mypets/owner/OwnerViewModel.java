package com.tsiro.dogvip.mypets.owner;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.requestmngrlayer.MyPetsRequestManager;
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
 * Created by giannis on 4/6/2017.
 */

public class OwnerViewModel implements OwnerContract.ViewModel {

    private static final String debugTag = OwnerViewModel.class.getSimpleName();
    private MyPetsRequestManager mMyPetsRequestManager;
    private OwnerContract.View mViewClback;
    private Disposable mDisp;
    private AsyncProcessor<OwnerObj> mProcessor;
    private int requestState;

    public OwnerViewModel(MyPetsRequestManager mMyPetsRequestManager) {
        this.mMyPetsRequestManager = mMyPetsRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewClback = (OwnerContract.View) viewCallback;
//        Log.e(debugTag, "onViewAttached "+mViewClback);
    }

    @Override
    public void onViewResumed() {
        if (mDisp != null && requestState != AppConfig.REQUEST_RUNNING && mProcessor != null)
            mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new OwnerObserver());
    }

    @Override
    public void onViewDetached() {
        mViewClback = null;
        if (mDisp != null) mDisp.dispose();
    }

    @Override
    public void submitOwner(OwnerObj request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new OwnerObserver());

            mMyPetsRequestManager.submitOwner(request, this).subscribe(mProcessor);
        }
    }

//    @Override
//    public void deleteImage(Image image) {
////        if (requestState != AppConfig.REQUEST_RUNNING) {
////            imageProcessor = AsyncProcessor.create();
////            mUploadDisp = imageProcessor.subscribeWith(new ImageUploadSubscriber(this));
////
////            mMyPetsRequestManager.deleteImage(image, this).subscribe(imageProcessor);
////        }
//    }

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
