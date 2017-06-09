package com.tsiro.dogvip.mypets;

import com.tsiro.dogvip.POJO.mypets.GetOwnerRequest;
import com.tsiro.dogvip.POJO.mypets.GetOwnerResponse;
import com.tsiro.dogvip.app.Lifecycle;

/**
 * Created by giannis on 4/6/2017.
 */

public interface GetOwnerContract {

    interface View extends Lifecycle.View {
        void onSuccess(GetOwnerResponse response);
        void onError(int code);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void getOwnerDetails(GetOwnerRequest request);
        void setRequestState(int state);
    }

}
