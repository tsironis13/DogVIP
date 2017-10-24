package com.tsiro.dogvip.mypets.pet;

import android.os.Bundle;
import android.util.Log;

import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
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

public class PetViewModel implements PetContract.ViewModel {

    private PetContract.View mViewClback;
    private MyPetsRequestManager mMyPetsRequestManager;
    private AsyncProcessor<OwnerObj> mPetProcessor;
    private int requestState;
    private Disposable mPetDisp;

    public PetViewModel(MyPetsRequestManager mMyPetsRequestManager) {
        this.mMyPetsRequestManager = mMyPetsRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewClback = (PetContract.View) viewCallback;
    }

    @Override
    public void onViewResumed() {
        if (mPetDisp != null && requestState != AppConfig.REQUEST_RUNNING) {
            mPetProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new GetPetDetailsObserver());
        }
    }

    @Override
    public void onViewDetached() {
        mViewClback = null;
        if (mPetDisp != null) mPetDisp.dispose();
    }

    @Override
    public void submitPet(PetObj request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            requestState = AppConfig.REQUEST_RUNNING;
            mPetProcessor = AsyncProcessor.create();
            mPetDisp = mPetProcessor
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new GetPetDetailsObserver());

            mMyPetsRequestManager.submitPet(request, this).subscribe(mPetProcessor);
        }
    }

    @Override
    public void setRequestState(int state) {
        requestState = state;
    }

    private void onGetPetSuccess(OwnerObj response) {
        mPetDisp = null;
        mViewClback.onSuccess(response);
    }

    private void onGetPetError(int resource, boolean msglength) {
        mPetDisp = null;
        mViewClback.onError(resource, msglength);
        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private class GetPetDetailsObserver extends DisposableSubscriber<OwnerObj> {

        @Override
        public void onNext(@NonNull OwnerObj response) {
            if (response.getCode() != AppConfig.STATUS_OK) {
                onGetPetError(AppConfig.getCodes().get(response.getCode()), response.isStringlength());
            } else {
                onGetPetSuccess(response);
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {
            onGetPetError(AppConfig.getCodes().get(AppConfig.STATUS_ERROR), false);
        }

        @Override
        public void onComplete() {
        }
    }
}
