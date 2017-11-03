package com.tsiro.dogvip.app;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.image_states.State;

import java.io.File;

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

//    interface TestViewModel {
//        void onViewAttached(View viewCallback);
//        void onConfigurationChange(Bundle savedInstanceState);
//        void onViewResumed();
//        void onViewDetached();
//    }

    interface ViewModel {
        void onViewAttached(View viewCallback);
        void onViewResumed();
        void onViewDetached();
    }

    interface GenericImageUploadViewModel extends ViewModel {
        void onViewResumed(State state);
    }

    interface ImageUploadView {
        void noImageUrl();
        void loadImageUrl(Object obj, final State state, File file);
        void imageUploading();
        void onSuccessUpload(Image image);
        void onErrorUpload();
        void onSuccessDelete();
        void onErrorDelete();
    }

}
