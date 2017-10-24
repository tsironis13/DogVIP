package com.tsiro.dogvip.mypets.ownerprofile;

import android.os.Bundle;

import com.tsiro.dogvip.POJO.mypets.OwnerRequest;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.requestmngrlayer.MyPetsRequestManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 4/6/2017.
 */

public class OwnerProfileViewModel implements OwnerProfileContract.ViewModel {

    private MyPetsRequestManager mMyPetsRequestManager;
    private OwnerProfileContract.View mViewClback;
    private Disposable mDisp;
    private AsyncProcessor<OwnerRequest> mProcessor;
    private int requestState;

    public OwnerProfileViewModel(MyPetsRequestManager mMyPetsRequestManager) {
        this.mMyPetsRequestManager = mMyPetsRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewClback = (OwnerProfileContract.View) viewCallback;
    }

    @Override
    public void onViewResumed() {
        if (mDisp != null && requestState != AppConfig.REQUEST_RUNNING && mProcessor != null)
                mProcessor
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new OwnerProfileObserver());
    }

    @Override
    public void onViewDetached() {
        mViewClback = null;
        if (mDisp != null) mDisp.dispose();
    }

    @Override
    public void manipulateOwner(OwnerRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new OwnerProfileObserver());

            mMyPetsRequestManager.deleteOwner(request, this).subscribe(mProcessor);
        }
    }

    @Override
    public void setRequestState(int state) {
        requestState = state;
    }

    private void onDeleteOwnerSuccess(OwnerRequest response) {
        mDisp = null;
        mViewClback.onSuccess(response);
    }

    private void onDeleteOwnerError(int resource) {
        mDisp = null;
        mViewClback.onError(resource);
        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private class OwnerProfileObserver extends DisposableSubscriber<OwnerRequest> {

        @Override
        public void onNext(@NonNull OwnerRequest response) {
            if (response.getCode() != AppConfig.STATUS_OK) {
                onDeleteOwnerError(AppConfig.getCodes().get(response.getCode()));
            } else {
                onDeleteOwnerSuccess(response);
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {
            onDeleteOwnerError(AppConfig.getCodes().get(AppConfig.STATUS_ERROR));
        }

        @Override
        public void onComplete() {
        }
    }
}
