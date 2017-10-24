package com.tsiro.dogvip.mychatrooms;

import android.os.Bundle;

import com.tsiro.dogvip.POJO.chat.FetchChatRoomsRequest;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomsResponse;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.requestmngrlayer.ChatRequestManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 19/7/2017.
 */

public class MyChatRoomsViewModel implements MyChatRoomsContract.ViewModel {

    private MyChatRoomsContract.View mViewClback;
    private ChatRequestManager mFcmRequestManager;
    private AsyncProcessor<FetchChatRoomsResponse> mProcessor;
    private int requestState;
    private Disposable mDisp;

    public MyChatRoomsViewModel(ChatRequestManager requestManager) {
        this.mFcmRequestManager = requestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewClback = (MyChatRoomsContract.View) viewCallback;
    }

    @Override
    public void onViewResumed() {
        if (mDisp != null && requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyChatRoomsObserver());
        }
    }

    @Override
    public void onViewDetached() {
        mViewClback = null;
        if (mDisp != null) mDisp.dispose();
    }

    @Override
    public void setRequestState(int state) {
        requestState = state;
    }

    @Override
    public void getChatRooms(FetchChatRoomsRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            requestState = AppConfig.REQUEST_RUNNING;
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new MyChatRoomsObserver());

            mFcmRequestManager.getChatRooms(request, this).subscribe(mProcessor);
        }
    }

    private void onSuccessAction(FetchChatRoomsResponse response) {
        mDisp = null;
        mViewClback.onSuccess(response);
    }

    private void onErrorAction(int resource) {
        mDisp = null;
        mViewClback.onError(resource);
        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private class MyChatRoomsObserver extends DisposableSubscriber<FetchChatRoomsResponse> {

        @Override
        public void onNext(FetchChatRoomsResponse response) {
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
        public void onComplete() {}
    }

}
