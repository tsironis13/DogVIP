package com.tsiro.dogvip.ownerpets;

import android.view.View;

import com.tsiro.dogvip.POJO.mypets.OwnerRequest;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.app.Lifecycle;

/**
 * Created by giannis on 6/7/2017.
 */

public interface OwnerPetsContract {

    interface Presenter {
        void onPetImageClick(android.view.View view);
    }

    interface View extends Lifecycle.View {
        void onSuccess(OwnerObj response);
        void onError(int resource);
        void onPetImageClick(android.view.View view);
//        void onLoveViewClick(android.view.View view);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void getOwnerPets(OwnerRequest request);
        void setRequestState(int state);
    }

}
