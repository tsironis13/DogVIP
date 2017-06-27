package com.tsiro.dogvip.mypets;

import com.tsiro.dogvip.POJO.mypets.OwnerRequest;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.app.Lifecycle;

/**
 * Created by giannis on 4/6/2017.
 */

public interface GetOwnerContract {

    interface View extends Lifecycle.View {
        void onSuccess(OwnerObj response);
        void onError(int code);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void getOwnerDetails(OwnerRequest request);
        void setRequestState(int state);
    }

}
