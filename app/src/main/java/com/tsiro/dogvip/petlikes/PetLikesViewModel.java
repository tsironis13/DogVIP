package com.tsiro.dogvip.petlikes;

import android.os.Bundle;

import com.tsiro.dogvip.POJO.petlikes.PetLikesRequest;
import com.tsiro.dogvip.POJO.petlikes.PetLikesResponse;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.requestmngrlayer.LoveMatchRequestManager;
import com.tsiro.dogvip.requestmngrlayer.PetLikesRequestManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 5/7/2017.
 */

public class PetLikesViewModel implements PetLikesContract.ViewModel {

    private PetLikesContract.View mViewClback;
    private PetLikesRequestManager mPetLikesRequestManager;
    private AsyncProcessor<PetLikesResponse> mProcessor;
    private int requestState;
    private Disposable mDisp;

    public PetLikesViewModel(PetLikesRequestManager mPetLikesRequestManager) {
        this.mPetLikesRequestManager = mPetLikesRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewClback = (PetLikesContract.View) viewCallback;
    }

    @Override
    public void onViewResumed() {
        if (mDisp != null && requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new PetLikesObserver());
        }
    }

    @Override
    public void onViewDetached() {
        mViewClback = null;
        if (mDisp != null) mDisp.dispose();
    }

    @Override
    public void getPetLikes(PetLikesRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            requestState = AppConfig.REQUEST_RUNNING;
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new PetLikesObserver());

            mPetLikesRequestManager.getPetLikes(request, this).subscribe(mProcessor);
        }
    }

    @Override
    public void setRequestState(int state) {
        requestState = state;
    }

    private void onSuccessGetPetLikes(PetLikesResponse response) {
        mDisp = null;
        mViewClback.onSuccess(response);
    }

    private void onErrorGetPetLikes(int code) {
        mDisp = null;
        mViewClback.onError(code);
        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private class PetLikesObserver extends DisposableSubscriber<PetLikesResponse> {


        @Override
        public void onNext(PetLikesResponse response) {
            if (response.getCode() != AppConfig.STATUS_OK) {
                onErrorGetPetLikes(AppConfig.getCodes().get(response.getCode()));
            } else {
                onSuccessGetPetLikes(response);
            }
        }

        @Override
        public void onError(Throwable t) {
            onErrorGetPetLikes(AppConfig.getCodes().get(AppConfig.STATUS_ERROR));
        }

        @Override
        public void onComplete() {

        }
    }
}
