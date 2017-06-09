package com.tsiro.dogvip.mypets;

import android.util.Log;

import com.tsiro.dogvip.POJO.mypets.GetOwnerRequest;
import com.tsiro.dogvip.POJO.mypets.GetOwnerResponse;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.requestmngrlayer.MyPetsRequestManager;

import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 4/6/2017.
 */

public class GetOwnerViewModel implements GetOwnerContract.ViewModel {

    private static final String debugTag = GetOwnerViewModel.class.getSimpleName();
    private GetOwnerContract.View mViewClback;
    private MyPetsRequestManager mMyPetsRequestManager;
    private AsyncProcessor<GetOwnerResponse> mGetOwnerDetailsProcessor;
    private int requestState;
    private Disposable mGetOwnerDetailsDisp;

    public GetOwnerViewModel(MyPetsRequestManager mMyPetsRequestManager) {
        this.mMyPetsRequestManager = mMyPetsRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewClback = (GetOwnerContract.View) viewCallback;
    }

    @Override
    public void onViewResumed() {
        if (mGetOwnerDetailsDisp != null && requestState != AppConfig.REQUEST_RUNNING) {
            mGetOwnerDetailsProcessor.subscribe(new GetOwnerDetailsObserver());
        }
    }

    @Override
    public void onViewDetached() {
        Log.e(debugTag, "onViewDetached");
        mViewClback = null;
        if (mGetOwnerDetailsDisp != null) mGetOwnerDetailsDisp.dispose();
    }

    @Override
    public void getOwnerDetails(GetOwnerRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            requestState = AppConfig.REQUEST_RUNNING;
            mGetOwnerDetailsProcessor = AsyncProcessor.create();
            mGetOwnerDetailsDisp = mGetOwnerDetailsProcessor.subscribeWith(new GetOwnerDetailsObserver());

            mMyPetsRequestManager.getOwnerDetails(request, this).subscribe(mGetOwnerDetailsProcessor);
        }
    }

    @Override
    public void setRequestState(int state) {
        requestState = state;
    }

    private void onGetOwnerSuccess(GetOwnerResponse response) {
        Log.e(debugTag, "onGetOwnerSuccess");
        mGetOwnerDetailsDisp = null;
        mViewClback.onSuccess(response);
    }

    private void onGetOwnerError(int code) {
        mGetOwnerDetailsDisp = null;
        mViewClback.onError(code);
        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private class GetOwnerDetailsObserver extends DisposableSubscriber<GetOwnerResponse> {

        @Override
        public void onNext(@NonNull GetOwnerResponse response) {
            if (response.getCode() != AppConfig.STATUS_OK) {
                onGetOwnerError(AppConfig.getCodes().get(response.getCode()));
            } else {
                Log.e(debugTag, "ON NEXT HERE");
                onGetOwnerSuccess(response);
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.e(debugTag, e.toString());
            onGetOwnerError(AppConfig.getCodes().get(AppConfig.STATUS_ERROR));
        }

        @Override
        public void onComplete() {
//            Log.e(debugTag, "onComplete");
        }
    }
}
