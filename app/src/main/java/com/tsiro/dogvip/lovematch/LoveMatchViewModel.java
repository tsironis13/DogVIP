package com.tsiro.dogvip.lovematch;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.tsiro.dogvip.POJO.Response;
import com.tsiro.dogvip.POJO.lovematch.GetPetsByFilterRequest;
import com.tsiro.dogvip.responsecontroller.lovematch.GetPetsCommand;
import com.tsiro.dogvip.responsecontroller.lovematch.LikeDislikeCommand;
import com.tsiro.dogvip.POJO.lovematch.LikeDislikeRequest;
import com.tsiro.dogvip.POJO.lovematch.LikeDislikeResponse;
import com.tsiro.dogvip.responsecontroller.ResponseController;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.requestmngrlayer.LoveMatchRequestManager;
import com.tsiro.dogvip.utilities.NetworkUtls;
import com.tsiro.dogvip.utilities.RetryWithDelay;

import org.reactivestreams.Publisher;

import java.util.InvalidPropertiesFormatException;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 2/7/2017.
 */

public class LoveMatchViewModel implements LoveMatchContract.ViewModel {

    private static final String debugTag = LoveMatchViewModel.class.getSimpleName();
    public LoveMatchContract.View mViewClback;
    private LoveMatchRequestManager mLoveMatchRequestManager;
    private AsyncProcessor<Response> mProcessor;
    private int requestState;
    private Disposable mLoveMatchDisp;

    @Inject
    ResponseController responseController;
    @Inject
    GetPetsCommand getPetsCommand;
    @Inject
    LikeDislikeCommand likeDislikeCommand;
    @Inject
    NetworkUtls networkUtls;
    @Inject
    RetryWithDelay retryWithDelay;

    @Inject
    public LoveMatchViewModel(LoveMatchRequestManager mLoveMatchRequestManager) {
        this.mLoveMatchRequestManager = mLoveMatchRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        this.mViewClback = (LoveMatchContract.View) viewCallback;
    }
    @Override
    public void onViewResumed() {
        Log.e(debugTag, "onVIEWRESUMED "+ mLoveMatchDisp + " request state "+requestState);
        if (mLoveMatchDisp != null && requestState != AppConfig.REQUEST_RUNNING && requestState != AppConfig.REQUEST_FAILED) {
            mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new GetPetsByFilterObserver());
        }
    }

    @Override
    public void onViewDetached() {
        mViewClback = null;
        if (mLoveMatchDisp != null) {
            mLoveMatchDisp.dispose();
        }
    }

    @Override
    public void setRequestState(int state) {
        requestState = state;
    }

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

//    public void onSuccessGetPets(GetPetsResponse response) {
//        Log.e(debugTag, response.getData() + " data");
//        mLoveMatchDisp = null;
//        mViewClback.onPetDataSuccess(response);
//    }

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

    public void likeDislikePet(final LikeDislikeRequest request) {
        if (requestState != AppConfig.REQUEST_RUNNING && requestState != AppConfig.REQUEST_FAILED) {
            likeDislikeCommand.setViewCallback(mViewClback);
            responseController.setCommand(likeDislikeCommand);

            mProcessor = AsyncProcessor.create();
            mLoveMatchDisp = mProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribeWith(new GetPetsByFilterObserver());

            networkUtls.getNetworkFlowable.
                    flatMap(new Function<Boolean, Publisher<Response>>() {
                        @Override
                        public Publisher<Response> apply(@NonNull Boolean aBoolean) throws Exception {
                            return mLoveMatchRequestManager
                                                        .likeDislikePet(request, LoveMatchViewModel.this)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .retryWhen(configureRetryWithDelayParams(3, 2000));
                        }
                    })
                    .flatMap(new Function<Response, Publisher<Response>>() {
                        @Override
                        public Publisher<Response> apply(@NonNull Response response) throws Exception {
                            return responseCodeStatusFlowable(response).retryWhen(configureRetryWithDelayParams(0, 0));
                        }
                    })
                    .doOnError(new Consumer<Throwable>() {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception {
                            handleError(throwable);
                        }
                    })
                    .retryWhen(new Function<Flowable<Throwable>, Publisher<?>>() {
                        @Override
                        public Publisher<?> apply(@NonNull Flowable<Throwable> throwableFlowable) throws Exception {
                            return retryWhen();
                        }
                    })
                    .take(1)
                    .subscribe(mProcessor);

        }
    }

    public int getRequestState() {
        return requestState;
    }

    public void getPetsByFilter(final GetPetsByFilterRequest request) {

        if (requestState != AppConfig.REQUEST_RUNNING && requestState != AppConfig.REQUEST_FAILED) {
            Log.e(debugTag, "REQUESTTTTTTTTTTTTT  "+ requestState);
            getPetsCommand.setViewCallback(mViewClback);
            responseController.setCommand(getPetsCommand);

            mProcessor = AsyncProcessor.create();
            mLoveMatchDisp = mProcessor
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread()).subscribeWith(new GetPetsByFilterObserver());

            Flowable.concat(
                    mLoveMatchRequestManager.getPetsResponse().filter(new Predicate<Response>() {
                        @Override
                        public boolean test(@NonNull Response response) throws Exception {
                            return !response.getPetdata().getData().isEmpty() && response.getPetdata().isUpToDate();
                        }
                    }),
                    networkUtls.getNetworkFlowable
                            .flatMap(new Function<Boolean, Publisher<Response>>() {
                                @Override
                                public Publisher<Response> apply(@NonNull Boolean aBoolean) throws Exception {
                                    return mLoveMatchRequestManager
                                                                .getPetsByFilter(request, LoveMatchViewModel.this)
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .retryWhen(configureRetryWithDelayParams(3, 2000));
                                }
                            })
                    .filter(new Predicate<Response>() {
                        @Override
                        public boolean test(@NonNull Response response) throws Exception {
                            Log.e(debugTag, System.currentTimeMillis()/1000  + " is uptodate "+ response.getPetdata().isUpToDate());
                            return !response.getPetdata().getData().isEmpty();
                        }
                    })
                    .flatMap(new Function<Response, Publisher<Response>>() {
                        @Override
                        public Publisher<Response> apply(@NonNull Response response) throws Exception {
                            return responseCodeStatusFlowable(response).retryWhen(configureRetryWithDelayParams(0, 0));
                        }
                    })
                    .doOnError(new Consumer<Throwable>() {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception {
                            handleError(throwable);
                        }
                    })
                    .retryWhen(new Function<Flowable<Throwable>, Publisher<?>>() {
                        @Override
                        public Publisher<?> apply(@NonNull Flowable<Throwable> throwableFlowable) throws Exception {
                            return retryWhen();
                        }
                    }))
                    .take(1)
                    .subscribe(mProcessor);
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

    private RetryWithDelay configureRetryWithDelayParams(int maxRetries, int retryDelayMillis) {
        retryWithDelay.setMaxRetries(maxRetries);
        retryWithDelay.setRetryDelayMillis(retryDelayMillis);
        return retryWithDelay;
    }

    private Flowable<Response> responseCodeStatusFlowable(final Response response) {
        return Flowable.create(new FlowableOnSubscribe<Response>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Response> e) throws Exception {
                if (response.getCode() != 200) {
                    e.onError(new InvalidPropertiesFormatException(""));
                } else {
                    e.onNext(response);

                }
            }
        }, BackpressureStrategy.LATEST);
    }

    public void retry() {
        retryWithDelay.getRetrySubject().onNext(new Object());
    }

    private void handleError(Throwable throwable) {
        if (throwable instanceof IllegalStateException) { //server error
            mViewClback.onError(R.string.error);
            Log.e(debugTag, " server error");
        } else if (throwable instanceof InvalidPropertiesFormatException) {
            mViewClback.onError(R.string.please_fill_out_search_filters);
        } else {//no network connection error
            Log.e(debugTag, " on error");
            mViewClback.onError(R.string.no_internet_connection);
        }
        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
    }

    private Flowable<Object> retryWhen() {
        retryWithDelay.setRetryButtonClicked();
        return retryWithDelay.getRetrySubject().toFlowable(BackpressureStrategy.LATEST);
    }

    private class GetPetsByFilterObserver extends DisposableSubscriber<Response> {

        @Override
        public void onNext(Response response) {
//            Log.e(debugTag, response.getCode() + " ");
            mLoveMatchDisp = null;
            if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
            if (response.getCode() != AppConfig.STATUS_OK) {
//                onErrorGetPets(AppConfig.getCodes().get(response.getCode()));
                responseController.executeCommandOnError(AppConfig.getCodes().get(response.getCode()));
            } else {
                responseController.executeCommandOnSuccess(response);
            }
        }

        @Override
        public void onError(Throwable t) {
            mLoveMatchDisp = null;
            if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
//            Log.e(debugTag, t + " ");
//            onErrorGetPets(AppConfig.getCodes().get(AppConfig.STATUS_ERROR));
        }

        @Override
        public void onComplete() {}
    }
}
