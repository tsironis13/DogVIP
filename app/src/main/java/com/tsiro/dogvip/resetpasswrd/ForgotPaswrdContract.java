package com.tsiro.dogvip.resetpasswrd;

import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.app.Lifecycle;

/**
 * Created by giannis on 29/5/2017.
 */

public interface ForgotPaswrdContract {

    interface View extends Lifecycle.View {
        void onSuccess(ForgotPaswrdObj response);
        void onError(int resource, boolean msglength);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void fogotpass(ForgotPaswrdObj request);
        void setRequestState(int state);
    }

}
