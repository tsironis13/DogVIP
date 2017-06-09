package com.tsiro.dogvip.mypets.owner;

import com.tsiro.dogvip.app.Lifecycle;

/**
 * Created by giannis on 4/6/2017.
 */

public class OwnerContract {

    interface View extends Lifecycle.View {
        void onSuccess();
        void onError(int code);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void submitOwner();
        void setRequestState(int state);
    }

}
