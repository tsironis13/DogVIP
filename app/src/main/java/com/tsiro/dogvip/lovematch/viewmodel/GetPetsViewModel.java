package com.tsiro.dogvip.lovematch.viewmodel;

import android.util.Log;

import com.tsiro.dogvip.POJO.lovematch.LoveMatchRequest;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchResponse;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.lovematch.LoveMatchContract;
import com.tsiro.dogvip.requestmngrlayer.LoveMatchRequestManager;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 7/10/2017.
 */

public class GetPetsViewModel extends LoveMatchViewModel implements LoveMatchContract.GetPetsViewModel {

    private AsyncProcessor<LoveMatchResponse> mProcessor;
    private Disposable mLoveMatchDisp;
    private LoveMatchContract.View mViewClback;

    @Inject
    public GetPetsViewModel(LoveMatchRequestManager mLoveMatchRequestManager) {
        super(mLoveMatchRequestManager);
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        super.onViewAttached(viewCallback);
        this.mViewClback =  (LoveMatchContract.View) viewCallback;
        Log.e("aa", viewCallback + " kalase");
    }

    @Override
    public void onViewResumed() {
        super.onViewResumed();
        if (mLoveMatchDisp != null && getRequestState() != AppConfig.REQUEST_RUNNING) {
            mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new GetPetsByFilterObserver());
        }
    }

    @Override
    public void onViewDetached() {
        super.onViewDetached();
        mViewClback = null;
        if (mLoveMatchDisp != null) mLoveMatchDisp.dispose();
    }

    private void onSuccessGetPets(LoveMatchResponse response) {
        mLoveMatchDisp = null;
        mViewClback.onSuccess(response);
    }

    private void onErrorGetPets(int code) {
        mLoveMatchDisp = null;
        mViewClback.onError(code);
        if (mViewClback != null) setRequestState(AppConfig.REQUEST_NONE);
    }

    public void kala(LoveMatchRequest request, LoveMatchContract.View view) {
//        Log.e("ehre", view.toString());
        if (getRequestState() != AppConfig.REQUEST_RUNNING) {
//            mViewClback = view;
            setRequestState(AppConfig.REQUEST_RUNNING);
            mProcessor = AsyncProcessor.create();
            mLoveMatchDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new GetPetsByFilterObserver());

            mLoveMatchRequestManager.getPetsByFilter(request, this).subscribe(mProcessor);
        }
    }

    @Override
    public void getPetsByFilter(LoveMatchRequest request) {
//        if (getRequestState() != AppConfig.REQUEST_RUNNING) {
//            setRequestState(AppConfig.REQUEST_RUNNING);
//            mProcessor = AsyncProcessor.create();
//            mLoveMatchDisp = mProcessor
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribeWith(new GetPetsByFilterObserver());
//
//            mLoveMatchRequestManager.getPetsByFilter(request, this).subscribe(mProcessor);
//        }
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
            onErrorGetPets(AppConfig.getCodes().get(AppConfig.STATUS_ERROR));
        }

        @Override
        public void onComplete() {

        }
    }

}
