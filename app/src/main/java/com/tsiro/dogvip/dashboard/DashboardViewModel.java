package com.tsiro.dogvip.dashboard;

import android.util.Log;

import com.tsiro.dogvip.POJO.chat.FetchChatRoomResponse;
import com.tsiro.dogvip.POJO.dashboard.DashboardRequest;
import com.tsiro.dogvip.POJO.dashboard.DashboardResponse;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.chatroom.ChatRoomContract;
import com.tsiro.dogvip.chatroom.ChatRoomViewModel;
import com.tsiro.dogvip.lovematch.LoveMatchViewModel;
import com.tsiro.dogvip.requestmngrlayer.ChatRequestManager;
import com.tsiro.dogvip.requestmngrlayer.DashboardRequestManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 21/7/2017.
 */

public class DashboardViewModel implements DashboardContract.ViewModel {

    private DashboardContract.View mViewClback;
    private DashboardRequestManager mDashboardRequestManager;
    private AsyncProcessor<DashboardResponse> mProcessor;
    private int requestState;
    private Disposable mDisp;

    public DashboardViewModel(DashboardRequestManager requestManager) {
        this.mDashboardRequestManager = requestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewClback = (DashboardContract.View) viewCallback;
    }

    @Override
    public void onViewResumed() {
        if (mDisp != null && requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DashboardObserver());
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
    public void logoutUser(DashboardRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            requestState = AppConfig.REQUEST_RUNNING;
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DashboardObserver());

            mDashboardRequestManager.logoutUser(request, this).subscribe(mProcessor);
        }
    }

    @Override
    public void getTotelUnreadMsgs(DashboardRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            requestState = AppConfig.REQUEST_RUNNING;
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DashboardObserver());

            mDashboardRequestManager.getTotalUnreadMsgs(request, this).subscribe(mProcessor);
        }
    }

    private void onSuccessAction(DashboardResponse response) {
        mDisp = null;
        mViewClback.onSuccess(response);
    }

    private void onErrorAction(int resource) {
        mDisp = null;
        mViewClback.onError(resource);
        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private class DashboardObserver extends DisposableSubscriber<DashboardResponse> {

        @Override
        public void onNext(DashboardResponse response) {
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
