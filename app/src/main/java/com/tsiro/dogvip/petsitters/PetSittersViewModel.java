package com.tsiro.dogvip.petsitters;

import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsRequest;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsResponse;
import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.POJO.petsitter.RateBookingRequest;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.requestmngrlayer.PetSitterRequestManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 14/9/2017.
 */

public class PetSittersViewModel implements PetSittersContract.ViewModel {

    private PetSitterRequestManager mRequestManager;
    private AsyncProcessor<OwnerSitterBookingsResponse> mProcessor;
    private PetSittersContract.View mViewCallback;
    private Disposable mDisp;
    private int requestState;

    public PetSittersViewModel(PetSitterRequestManager mRequestManager) {
        this.mRequestManager = mRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewCallback = (PetSittersContract.View) viewCallback;
    }

    @Override
    public void onViewResumed() {
        if (mDisp != null && requestState != AppConfig.REQUEST_RUNNING && mProcessor != null)
            mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new PetSittersObserver());
    }

    @Override
    public void onViewDetached() {
        mViewCallback = null;
        if (mDisp != null) mDisp.dispose();
    }

    @Override
    public void getSitterComments(OwnerSitterBookingsRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new PetSittersObserver());

            mRequestManager.getSitterComments(request, this).subscribe(mProcessor);
        }
    }

    @Override
    public void rateSitterBoooking(RateBookingRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new PetSittersObserver());

            mRequestManager.rateSitterBooking(request, this).subscribe(mProcessor);
        }
    }

    @Override
    public void getPendingBookings(OwnerSitterBookingsRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new PetSittersObserver());

            mRequestManager.getOwnerSitterBookings(request, this).subscribe(mProcessor);
        }
    }

    @Override
    public void getBookingDetails(OwnerSitterBookingsRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new PetSittersObserver());

            mRequestManager.getOwnerSitterBookings(request, this).subscribe(mProcessor);
        }
    }

    @Override
    public void getOwnerSitterBookings(OwnerSitterBookingsRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new PetSittersObserver());

            mRequestManager.getOwnerSitterBookings(request, this).subscribe(mProcessor);
        }
    }

    @Override
    public void setRequestState(int state) {
        this.requestState = state;
    }

    private void onPetSittersSuccess(OwnerSitterBookingsResponse response) {
        mDisp = null;
        mViewCallback.onSuccess(response);
    }

    private void onPetSittersError(int resource) {
        mDisp = null;
        mViewCallback.onError(resource);
        if (mViewCallback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private class PetSittersObserver extends DisposableSubscriber<OwnerSitterBookingsResponse> {

        @Override
        public void onNext(OwnerSitterBookingsResponse response) {
            if (response.getCode() == AppConfig.STATUS_ERROR) {
                onPetSittersError(AppConfig.getCodes().get(response.getCode()));
            } else {
                onPetSittersSuccess(response);
            }
        }

        @Override
        public void onError(Throwable t) {
            onPetSittersError(AppConfig.getCodes().get(AppConfig.STATUS_ERROR));
        }

        @Override
        public void onComplete() {

        }
    }
}
