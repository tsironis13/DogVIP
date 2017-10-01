package com.tsiro.dogvip.profs;

import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsResponse;
import com.tsiro.dogvip.POJO.profs.GetUserProfRequest;
import com.tsiro.dogvip.POJO.profs.GetUserProfResponse;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.petsitters.PetSittersContract;
import com.tsiro.dogvip.petsitters.PetSittersViewModel;
import com.tsiro.dogvip.requestmngrlayer.PetSitterRequestManager;
import com.tsiro.dogvip.requestmngrlayer.ProfRequestManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 30/9/2017.
 */

public class ProfProfileViewModel implements ProfProfileContract.ViewModel {

    private ProfRequestManager mRequestManager;
    private AsyncProcessor<GetUserProfResponse> mProcessor;
    private ProfProfileContract.View mViewCallback;
    private Disposable mDisp;
    private int requestState;

    public ProfProfileViewModel(ProfRequestManager mRequestManager) {
        this.mRequestManager = mRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewCallback = (ProfProfileContract.View) viewCallback;
    }

    @Override
    public void onViewResumed() {
        if (mDisp != null && requestState != AppConfig.REQUEST_RUNNING && mProcessor != null)
            mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ProfsObserver());
    }

    @Override
    public void onViewDetached() {
        mViewCallback = null;
        if (mDisp != null) mDisp.dispose();
    }

    @Override
    public void getUserProf(GetUserProfRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new ProfsObserver());

            mRequestManager.getUserProf(request, this).subscribe(mProcessor);
        }
    }

    @Override
    public void setRequestState(int state) {
        this.requestState = state;
    }

    private void onProfsSuccess(GetUserProfResponse response) {
        mDisp = null;
        mViewCallback.onSuccess(response);
    }

    private void onProfsError(int resource) {
        mDisp = null;
        mViewCallback.onError(resource);
        if (mViewCallback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private class ProfsObserver extends DisposableSubscriber<GetUserProfResponse> {

        @Override
        public void onNext(GetUserProfResponse response) {
            if (response.getCode() == AppConfig.STATUS_ERROR) {
                onProfsError(AppConfig.getCodes().get(response.getCode()));
            } else {
                onProfsSuccess(response);
            }
        }

        @Override
        public void onError(Throwable t) {
            onProfsError(AppConfig.getCodes().get(AppConfig.STATUS_ERROR));
        }

        @Override
        public void onComplete() {

        }
    }
}
