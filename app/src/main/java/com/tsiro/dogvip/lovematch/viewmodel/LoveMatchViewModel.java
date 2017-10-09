package com.tsiro.dogvip.lovematch.viewmodel;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.tsiro.dogvip.POJO.lovematch.Command;
import com.tsiro.dogvip.POJO.lovematch.GetPetsCommand;
import com.tsiro.dogvip.POJO.lovematch.GetPetsResponse;
import com.tsiro.dogvip.POJO.lovematch.LikeDislikeCommand;
import com.tsiro.dogvip.POJO.lovematch.LikeDislikeResponse;
import com.tsiro.dogvip.POJO.lovematch.LoveMatch;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchRequest;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchResponse;
import com.tsiro.dogvip.POJO.lovematch.RemoteControl;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.di.qualifiers.ApplicationContext;
import com.tsiro.dogvip.lovematch.LoveMatchContract;
import com.tsiro.dogvip.requestmngrlayer.LoveMatchRequestManager;

import javax.inject.Inject;

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
    public LoveMatchContract.View mViewClback;
    public LoveMatchRequestManager mLoveMatchRequestManager;
//    private AsyncProcessor<LoveMatchResponse> mProcessor;
private AsyncProcessor<LoveMatch> mProcessor;
    private int requestState;
    private Disposable mLoveMatchDisp;
//    public abstract void onViewResumed();
    private RemoteControl remoteControl = new RemoteControl();
    private LoveMatch loveMatch = new LoveMatch();

    @Inject
    public LoveMatchViewModel(LoveMatchRequestManager mLoveMatchRequestManager) {
        this.mLoveMatchRequestManager = mLoveMatchRequestManager;
    }

    @Override
    public final void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewClback = (LoveMatchContract.View) viewCallback;
    }

    @Override
    public void onViewResumed() {
        if (mLoveMatchDisp != null && getRequestState() != AppConfig.REQUEST_RUNNING) {
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

    public int getRequestState() { return requestState; }

//    private void onSuccessGetPets(LoveMatchResponse response) {
//        mLoveMatchDisp = null;
//        mViewClback.onSuccess(response);
//    }
//
//    private void onErrorGetPets(int code) {
//        mLoveMatchDisp = null;
//        mViewClback.onError(code);
//        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
//    }

    public void onSuccessGetPets(GetPetsResponse response) {
        Log.e(debugTag, response.getData() + " data");
        mLoveMatchDisp = null;
        mViewClback.onPetDataSuccess(response);
    }

    public void onSuccessLikeDislikePet(LikeDislikeResponse response) {

    }

//    private void onSuccessGetPets(GetPetsResponse response) {
//        mLoveMatchDisp = null;
////        mViewClback.onSuccess(response);
//    }

    private void onErrorGetPets(int code) {
        mLoveMatchDisp = null;
        mViewClback.onError(code);
        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    public void likeDislikePet(LoveMatchRequest request) {
        if (getRequestState() != AppConfig.REQUEST_RUNNING) {
            Command likeDislikeCommand = new LikeDislikeCommand();
            remoteControl.setCommand(likeDislikeCommand);
//            mViewClback = view;
            setRequestState(AppConfig.REQUEST_RUNNING);
            mProcessor = AsyncProcessor.create();
            mLoveMatchDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new GetPetsByFilterObserver());

            mLoveMatchRequestManager.likeDislikePet(request, this).subscribe(mProcessor);
        }
    }

    public void getPetsByFilter(LoveMatchRequest request) {
        if (getRequestState() != AppConfig.REQUEST_RUNNING) {
            Command getPetsCommand = new GetPetsCommand();
            remoteControl.setCommand(getPetsCommand);
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
    public void onViewClick(View view) {
        mViewClback.onViewClick(view);
    }

    @Override
    public void onLoveImageViewClick(View view) {
        mViewClback.onLoveImageViewClick(view);
    }

    @Override
    public void onMessageIconClick(View view) {
        mViewClback.onMessageIconClick(view);
    }


//    private class GetPetsByFilterObserver extends DisposableSubscriber<LoveMatchResponse> {
//
//        @Override
//        public void onNext(LoveMatchResponse response) {
//            if (response.getCode() != AppConfig.STATUS_OK) {
//                onErrorGetPets(AppConfig.getCodes().get(response.getCode()));
//            } else {
//                onSuccessGetPets(response);
//            }
//        }
//
//        @Override
//        public void onError(Throwable t) {
//            onErrorGetPets(AppConfig.getCodes().get(AppConfig.STATUS_ERROR));
//        }
//
//        @Override
//        public void onComplete() {
//
//        }
//    }
private class GetPetsByFilterObserver extends DisposableSubscriber<LoveMatch> {

    @Override
    public void onNext(LoveMatch response) {
        Log.e("kalase", response.getCode() + " ");
        if (response.getCode() != AppConfig.STATUS_OK) {
//            remoteControl.pressButton(LoveMatchViewModel.this, response);

            onErrorGetPets(AppConfig.getCodes().get(response.getCode()));
        } else {
//            onSuccessGetPets(response);

            remoteControl.pressButton(LoveMatchViewModel.this, response);
        }
    }

    @Override
    public void onError(Throwable t) {
        Log.e(debugTag, t + " ");
        onErrorGetPets(AppConfig.getCodes().get(AppConfig.STATUS_ERROR));
    }

    @Override
    public void onComplete() {
    }
}
}
