package com.tsiro.dogvip.profs.profprofile;

import android.util.Log;
import android.view.View;

import com.tsiro.dogvip.POJO.profs.DeleteProfRequest;
import com.tsiro.dogvip.POJO.profs.GetProfDetailsRequest;
import com.tsiro.dogvip.POJO.profs.ProfDetailsResponse;
import com.tsiro.dogvip.POJO.profs.SaveProfDetailsRequest;
import com.tsiro.dogvip.POJO.profs.SearchProfsRequest;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.requestmngrlayer.ProfRequestManager;

import org.reactivestreams.Publisher;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 30/9/2017.
 */

public class ProfProfileViewModel implements ProfProfileContract.ViewModel {

    private ProfRequestManager mRequestManager;
    private AsyncProcessor<ProfDetailsResponse> mProcessor;
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
    public void searchProf(SearchProfsRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new ProfsObserver());

            mRequestManager.searchProf(request, this).subscribe(mProcessor);
        }
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
    public void onViewClick(View view) {
        mViewCallback.onViewClick(view);
    }

    @Override
    public void onMessageIconClick(View view) {
        mViewCallback.onMessageIconClick(view);
    }

    @Override
    public void onPhoneIconViewClick(View view) {
        mViewCallback.onPhoneIconViewClick(view);
    }

    @Override
    public void deleteProf(DeleteProfRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new ProfsObserver());

            mRequestManager.deleteProf(request, this).subscribe(mProcessor);
        }
    }

    @Override
    public void getProfDetails(GetProfDetailsRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
//                    .flatMap(new Function<ProfDetailsResponse, Publisher<?>>() {
//                        @Override
//                        public Publisher<?> apply(@NonNull ProfDetailsResponse profDetailsResponse) throws Exception {
//                            return null;
//                        }
//                    })
//
//                    .map(new Function<ProfDetailsResponse, ProfDetailsResponse>() {
//                        @Override
//                        public ProfDetailsResponse apply(@NonNull ProfDetailsResponse profDetailsResponse) throws Exception {
////                            Log.e("aa", profDetailsResponse.getProf().setCity("kalase"); + " asdklsdkkjds");
//                            profDetailsResponse.getProf().setCity("kalase");
//                            Log.e("debugatag", profDetailsResponse.getCode() + "code");
//                            return profDetailsResponse;
//                        }
//                    })
                    .subscribeWith(new ProfsObserver());

            mRequestManager.getProfDetails(request, this).subscribe(mProcessor);
        }
    }

    @Override
    public void saveProfDetails(SaveProfDetailsRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            mProcessor = AsyncProcessor.create();
            mDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new ProfsObserver());

            mRequestManager.saveProfDetails(request, this).subscribe(mProcessor);
        }
    }

    @Override
    public void setRequestState(int state) {
        this.requestState = state;
    }

    private void onProfsSuccess(ProfDetailsResponse response) {
        mDisp = null;
        mViewCallback.onSuccess(response);
    }

    private void onProfsError(int resource) {
        mDisp = null;
        mViewCallback.onError(resource);
        if (mViewCallback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private class ProfsObserver extends DisposableSubscriber<ProfDetailsResponse> {

        @Override
        public void onNext(ProfDetailsResponse response) {
            if (response.getCode() == AppConfig.STATUS_ERROR) {
                onProfsError(AppConfig.getCodes().get(response.getCode()));
            } else {
                onProfsSuccess(response);
            }
        }

        @Override
        public void onError(Throwable t) {
//            Log.e("aa", t.toString());
            onProfsError(AppConfig.getCodes().get(AppConfig.STATUS_ERROR));
        }

        @Override
        public void onComplete() {

        }
    }
}
