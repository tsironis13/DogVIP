package com.tsiro.dogvip.ownerpets;

import android.os.Bundle;

import com.tsiro.dogvip.POJO.mypets.OwnerRequest;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.mypets.ownerprofile.OwnerProfileContract;
import com.tsiro.dogvip.mypets.ownerprofile.OwnerProfileViewModel;
import com.tsiro.dogvip.requestmngrlayer.MyPetsRequestManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 6/7/2017.
 */

public class OwnerPetsViewModel implements OwnerPetsContract.ViewModel {

    private MyPetsRequestManager mMyPetsRequestManager;
    private OwnerPetsContract.View mViewClback;
    private Disposable mDisp;
    private AsyncProcessor<OwnerObj> mProcessor;
    private int requestState;

    public OwnerPetsViewModel(MyPetsRequestManager mMyPetsRequestManager) {
        this.mMyPetsRequestManager = mMyPetsRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewClback = (OwnerPetsContract.View) viewCallback;
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
    public void getOwnerPets(OwnerRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            requestState = AppConfig.REQUEST_RUNNING;
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new OwnerProfileObserver());

            mMyPetsRequestManager.getOwnerDetails(request, this).subscribe(mProcessor);
        }
    }

    @Override
    public void setRequestState(int state) {
        requestState = state;
    }

    private void onSuccessOwnerPets(OwnerObj response) {
        mDisp = null;
        mViewClback.onSuccess(response);
    }

    private void onErrorOwnerPets(int resource) {
        mDisp = null;
        mViewClback.onError(resource);
        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private class OwnerProfileObserver extends DisposableSubscriber<OwnerObj> {

        @Override
        public void onNext(@NonNull OwnerObj response) {
            if (response.getCode() != AppConfig.STATUS_OK) {
                onErrorOwnerPets(AppConfig.getCodes().get(response.getCode()));
            } else {
                onSuccessOwnerPets(response);
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {
            onErrorOwnerPets(AppConfig.getCodes().get(AppConfig.STATUS_ERROR));
        }

        @Override
        public void onComplete() {
        }
    }
}
