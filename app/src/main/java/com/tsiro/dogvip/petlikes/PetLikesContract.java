package com.tsiro.dogvip.petlikes;

import android.view.View;

import com.tsiro.dogvip.POJO.petlikes.PetLikesRequest;
import com.tsiro.dogvip.POJO.petlikes.PetLikesResponse;
import com.tsiro.dogvip.app.Lifecycle;

/**
 * Created by giannis on 5/7/2017.
 */

public interface PetLikesContract {

    interface Presenter {
        void onPetImageClick(android.view.View view);
        void onMessageIconClick(android.view.View view);

    }

    interface View extends Lifecycle.View {
        void onSuccess(PetLikesResponse response);
        void onError(int resource);
        void onPetImageClick(android.view.View view);
        void onMessageIconClick(android.view.View view);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void getPetLikes(PetLikesRequest request);
        void setRequestState(int state);
    }

}
