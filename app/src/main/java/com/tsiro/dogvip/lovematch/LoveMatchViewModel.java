package com.tsiro.dogvip.lovematch;

import android.util.Log;

import com.tsiro.dogvip.POJO.lovematch.LoveMatchRequest;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchResponse;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.requestmngrlayer.LoveMatchRequestManager;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 2/7/2017.
 */

public class LoveMatchViewModel implements LoveMatchContract.ViewModel {

    private static final String debugTag = LoveMatchViewModel.class.getSimpleName();
    private LoveMatchContract.View mViewClback;
    private LoveMatchRequestManager mLoveMatchRequestManager;
    private AsyncProcessor<LoveMatchResponse> mProcessor;
    private int requestState;
    private Disposable mLoveMatchDisp;

    public LoveMatchViewModel(LoveMatchRequestManager mLoveMatchRequestManager) {
        this.mLoveMatchRequestManager = mLoveMatchRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewClback = (LoveMatchContract.View) viewCallback;
    }

    @Override
    public void onViewResumed() {
        if (mLoveMatchDisp != null && requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new GetPetsByFilterObserver());
        }
    }

    @Override
    public void onViewDetached() {
        mViewClback = null;
        if (mLoveMatchDisp != null) mLoveMatchDisp.dispose();
    }

    @Override
    public void setRequestState(int state) {
        requestState = state;
    }

    private void onSuccessGetPets(LoveMatchResponse response) {
        mLoveMatchDisp = null;
        mViewClback.onSuccess(response);
    }

    private void onErrorGetPets(int code) {
        mLoveMatchDisp = null;
        mViewClback.onError(code);
        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    @Override
    public void getPetsByFilter(LoveMatchRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            requestState = AppConfig.REQUEST_RUNNING;
            mProcessor = AsyncProcessor.create();
            mLoveMatchDisp = mProcessor
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeWith(new GetPetsByFilterObserver());

            mLoveMatchRequestManager.getPetsByFilter(request, this).subscribe(mProcessor);
        }
    }

    private class GetPetsByFilterObserver extends DisposableSubscriber<LoveMatchResponse> {

        @Override
        public void onNext(LoveMatchResponse response) {
            if (response.getCode() != AppConfig.STATUS_OK) {
                onErrorGetPets(AppConfig.getCodes().get(response.getCode()));
            } else {
                onSuccessGetPets(response);
            }
        }

        @Override
        public void onError(Throwable t) {
            Log.e(debugTag, t.toString());
            onErrorGetPets(AppConfig.getCodes().get(AppConfig.STATUS_ERROR));
        }

        @Override
        public void onComplete() {

        }
    }
}
