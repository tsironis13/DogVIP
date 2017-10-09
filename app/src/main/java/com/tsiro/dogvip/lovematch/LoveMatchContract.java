package com.tsiro.dogvip.lovematch;

import com.tsiro.dogvip.POJO.lovematch.GetPetsResponse;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchRequest;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchResponse;
import com.tsiro.dogvip.app.Lifecycle;

import io.reactivex.processors.FlowableProcessor;

/**
 * Created by giannis on 2/7/2017.
 */

public interface LoveMatchContract {

    interface View extends Lifecycle.View {
        void onPetDataSuccess(GetPetsResponse response);
        void onSuccess(LoveMatchResponse response);
        void onError(int resource);
        void onLoveImageViewClick(android.view.View view);
        void onMessageIconClick(android.view.View view);
        void onViewClick(android.view.View view);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void setRequestState(int state);
        void onLoveImageViewClick(android.view.View view);
        void onMessageIconClick(android.view.View view);
        void onViewClick(android.view.View view);
    }

    interface GetPetsViewModel {
        void getPetsByFilter(LoveMatchRequest request);
    }

    interface LikeDislikePetViewModel {
        void likeDislikePet(LoveMatchRequest request);
    }

}
