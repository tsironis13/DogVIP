package com.tsiro.dogvip.petsitters.sitter_assignment;

import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.POJO.petsitter.SearchedSittersResponse;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.petsitters.petsitter.PetSitterViewModel;
import com.tsiro.dogvip.requestmngrlayer.SitterAssignmentRequestManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by thomatou on 9/13/17.
 */

public class SitterAssignmentViewModel implements SitterAssignmentContract.ViewModel {

    private SitterAssignmentRequestManager mSitterAssignmentRequestManager;
    private SitterAssignmentContract.View mViewCallback;
    private Disposable mDisp;
    private AsyncProcessor<SearchedSittersResponse> mProcessor;
    private int requestState;


    public SitterAssignmentViewModel(SitterAssignmentRequestManager sitterAssignmentRequestManager) {
        this.mSitterAssignmentRequestManager = sitterAssignmentRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewCallback = (SitterAssignmentContract.View) viewCallback;
    }

    @Override
    public void onViewResumed() {
        if (mDisp != null && requestState != AppConfig.REQUEST_RUNNING && mProcessor != null)
            mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SitterAssignmentObserver());
    }

    @Override
    public void onViewDetached() {
        mViewCallback = null;
        if (mDisp != null) mDisp.dispose();
    }

    @Override
    public void searchSitters(PetSitterObj petSitterObj) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new SitterAssignmentObserver());

            mSitterAssignmentRequestManager.searchSitters(petSitterObj, this).subscribe(mProcessor);
        }
    }

    @Override
    public void setRequestState(int state) {
        requestState = state;
    }

    private void onSitterAssignmentSuccess(SearchedSittersResponse response) {
        mDisp = null;
        mViewCallback.onSuccess(response);
    }

    private void onSitterAssignmentError(int resource) {
        mDisp = null;
        mViewCallback.onError(resource);
        if (mViewCallback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private class SitterAssignmentObserver extends DisposableSubscriber<SearchedSittersResponse> {

        @Override
        public void onNext(@NonNull SearchedSittersResponse response) {
            if (response.getCode() != AppConfig.STATUS_OK) {
                onSitterAssignmentError(AppConfig.getCodes().get(response.getCode()));
            } else {
                onSitterAssignmentSuccess(response);
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {
            onSitterAssignmentError(AppConfig.getCodes().get(AppConfig.STATUS_ERROR));
        }

        @Override
        public void onComplete() {
        }
    }
}
