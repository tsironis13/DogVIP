package com.tsiro.dogvip.lovematch;

import com.tsiro.dogvip.POJO.lovematch.LoveMatchRequest;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchResponse;
import com.tsiro.dogvip.app.Lifecycle;

import io.reactivex.processors.FlowableProcessor;

/**
 * Created by giannis on 2/7/2017.
 */

public interface LoveMatchContract {

    interface Presenter {
        void onPetImageViewClick(android.view.View view);
        void onLoveImageViewClick(android.view.View view);
    }

    interface View extends Lifecycle.View {
        void onSuccess(LoveMatchResponse response);
        void onError(int resource);
        void onPetImageViewClick(android.view.View view);
        void onLoveImageViewClick(android.view.View view);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void setRequestState(int state);
        void getPetsByFilter(LoveMatchRequest request);

    }

}