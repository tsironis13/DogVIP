package com.tsiro.dogvip.app;

import android.os.Parcelable;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
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

    interface ViewModel {
        void onViewAttached(View viewCallback);
        void onViewResumed();
        void onViewDetached();
    }

    interface GenericImageUploadViewModel extends ViewModel {
        void onSuccessImageAction(Image image);
        void onErrorImageAction();
    }

    interface ImageUploadView {
        void noImageUrl();
        void loadImageUrl(Object obj, final State state, File file);
        void imageUploading();
        void onSuccessImageAction(Image image);
        void onErrorImageAction();
//        void onSuccessUpload();
//        void onErrorUpload();
//        void onSuccessDelete();
//        void onErrorDelete();
    }

}
