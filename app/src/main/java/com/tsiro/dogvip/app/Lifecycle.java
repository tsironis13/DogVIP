package com.tsiro.dogvip.app;

import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;

/**
 * Created by giannis on 21/5/2017.
 */

public interface Lifecycle {

    interface BaseView {
        void hideSoftKeyboard();
        void logUserIn(AuthenticationResponse response);
    }

    interface View {}

    interface ViewModel {
        void onViewResumed();
        void onViewAttached(View viewCallback);
        void onViewDetached();
    }

}
