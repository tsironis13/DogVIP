package com.tsiro.dogvip.lostfound;

import com.tsiro.dogvip.POJO.lostfound.LostFoundObj;
import com.tsiro.dogvip.POJO.lostfound.LostFoundRequest;
import com.tsiro.dogvip.POJO.lostfound.LostFoundResponse;
import com.tsiro.dogvip.POJO.petlikes.PetLikesRequest;
import com.tsiro.dogvip.POJO.petlikes.PetLikesResponse;
import com.tsiro.dogvip.app.Lifecycle;

/**
 * Created by giannis on 10/7/2017.
 */

public interface LostFoundContract {

    interface Presenter {
        void onBaseViewClick(android.view.View view);
        void onShareIconClick(android.view.View view);
    }

    interface FrgmtView {
        void onBaseViewClick(android.view.View view);
        void onShareIconClick(android.view.View view);
    }

    interface View extends Lifecycle.View {
        void onSuccess(LostFoundResponse response);
        void onError(int resource);
        void onBaseViewClick(LostFoundObj lostFoundObj, int type); //type=>0->lost, 1->found
        void onShareIconClick(LostFoundObj lostFoundObj);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void getLostPets(LostFoundRequest request);
        void getFoundPets(LostFoundRequest request);
        void setRequestState(int state);
    }

}
