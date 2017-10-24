package com.tsiro.dogvip.lostfound.manipulatelostpet;

import android.os.Bundle;

import com.tsiro.dogvip.POJO.lostfound.LostFoundResponse;
import com.tsiro.dogvip.POJO.lostfound.ManipulateLostFoundPet;
import com.tsiro.dogvip.POJO.lostfound.ManipulateLostFoundPetResponse;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.lostfound.LostFoundContract;
import com.tsiro.dogvip.lostfound.LostFoundViewModel;
import com.tsiro.dogvip.requestmngrlayer.LostFoundRequestManager;
import com.tsiro.dogvip.requestmngrlayer.ManipulateLostFoundRequestManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 11/7/2017.
 */

public class ManipulateLostPetViewModel implements ManipulateLostPetContract.ViewModel {

    private ManipulateLostPetContract.View mViewClback;
    private ManipulateLostFoundRequestManager mManipulateLostFoundRequestManager;
    private AsyncProcessor<ManipulateLostFoundPetResponse> mProcessor;
    private int requestState;
    private Disposable mDisp;

    public ManipulateLostPetViewModel(ManipulateLostFoundRequestManager requestManager) {
        this.mManipulateLostFoundRequestManager = requestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewClback = (ManipulateLostPetContract.View) viewCallback;
    }

    @Override
    public void onViewResumed() {
        if (mDisp != null && requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ManipulateLostFoundObserver());
        }
    }

    @Override
    public void onViewDetached() {
        mViewClback = null;
        if (mDisp != null) mDisp.dispose();
    }

    @Override
    public void manipulateLostPet(ManipulateLostFoundPet request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            requestState = AppConfig.REQUEST_RUNNING;
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new ManipulateLostFoundObserver());

            mManipulateLostFoundRequestManager.manipulateLostFound(request, this).subscribe(mProcessor);
        }
    }

    @Override
    public void setRequestState(int state) {
        requestState = state;
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
