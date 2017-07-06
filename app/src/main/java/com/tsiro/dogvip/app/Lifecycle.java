package com.tsiro.dogvip.app;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;

/**
 * Created by giannis on 21/5/2017.
 */

public interface Lifecycle {

    interface BaseView {
        void hideSoftKeyboard();
    }

    interface View {}

    interface ImageUploadModel {
        void onSuccessImageAction(Image image);
        void onErrorImageAction();
    }

    interface ViewModel {
        void onViewAttached(View viewCallback);
        void onViewResumed();
        void onViewDetached();
    }

}
