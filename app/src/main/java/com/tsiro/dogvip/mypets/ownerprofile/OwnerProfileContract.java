package com.tsiro.dogvip.mypets.ownerprofile;

import android.view.View;

import com.tsiro.dogvip.POJO.mypets.OwnerRequest;
import com.tsiro.dogvip.app.Lifecycle;

/**
 * Created by giannis on 4/6/2017.
 */

public class OwnerProfileContract {

    interface Presenter {
        void onBaseViewClick(android.view.View view);
        void onPetImgViewClick(android.view.View view);
    }

    interface View extends Lifecycle.View {
        void onSuccess(OwnerRequest response);
        void onError(int resource);
        void onBaseViewClick(android.view.View view);
        void onPetImgViewClick(android.view.View view);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void deleteOwner(OwnerRequest request);
        void setRequestState(int state);
    }

}
