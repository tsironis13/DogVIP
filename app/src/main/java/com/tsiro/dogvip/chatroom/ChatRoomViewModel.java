package com.tsiro.dogvip.chatroom;

import android.os.Bundle;

import com.tsiro.dogvip.POJO.chat.FetchChatRoomRequest;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomResponse;
import com.tsiro.dogvip.POJO.chat.SendMessageRequest;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.requestmngrlayer.ChatRequestManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 16/7/2017.
 */

public class ChatRoomViewModel implements ChatRoomContract.ViewModel {

    private ChatRoomContract.View mViewClback;
    private ChatRequestManager mFcmRequestManager;
    private AsyncProcessor<FetchChatRoomResponse> mProcessor;
    private int requestState;
    private Disposable mDisp;

    public ChatRoomViewModel(ChatRequestManager requestManager) {
        this.mFcmRequestManager = requestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewClback = (ChatRoomContract.View) viewCallback;
    }

    @Override
    public void onViewResumed() {
        if (mDisp != null && requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ChatRoomObserver());
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
    public void getChatRoomMsgs(FetchChatRoomRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            requestState = AppConfig.REQUEST_RUNNING;
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new ChatRoomObserver());

            mFcmRequestManager.getChatRoomMsgs(request, this).subscribe(mProcessor);
        }
    }

    @Override
    public void updateReadMsgs(SendMessageRequest request) {
        sendMsg(request);
    }

    @Override
    public void sendMsg(SendMessageRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            requestState = AppConfig.REQUEST_RUNNING;
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new ChatRoomObserver());

            mFcmRequestManager.sendMsg(request, this).subscribe(mProcessor);
        }
    }

    private void onSuccessAction(FetchChatRoomResponse response) {
        mDisp = null;
        mViewClback.onSuccess(response);
    }

    private void onErrorAction(int resource) {
        mDisp = null;
        mViewClback.onError(resource);
        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private class ChatRoomObserver extends DisposableSubscriber<FetchChatRoomResponse> {

        @Override
        public void onNext(FetchChatRoomResponse response) {
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
