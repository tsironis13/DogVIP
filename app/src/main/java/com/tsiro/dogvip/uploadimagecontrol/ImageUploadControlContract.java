package com.tsiro.dogvip.uploadimagecontrol;

import android.view.View;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.app.Lifecycle;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by giannis on 25/6/2017.
 */

public interface ImageUploadControlContract {

    interface Presenter {
        void onCheckBoxClick(android.view.View view, boolean isChecked);
        void onImageClick(android.view.View view);
    }

    interface View extends Lifecycle.View {
        void onSuccess(Image image);
        void onError(int resource);
        void onCheckBoxClick(android.view.View view, boolean isChecked);
        void onImageClick(android.view.View view);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void uploadImage(RequestBody action, RequestBody token, RequestBody id, MultipartBody.Part image);
        void uploadPetImage(RequestBody action, RequestBody token, RequestBody user_role_id, RequestBody pet_id, MultipartBody.Part image, RequestBody index);
        void uploadPetSitterPlaceImage(RequestBody action, RequestBody token, RequestBody id, MultipartBody.Part image, RequestBody index);
        void manipulatePetImage(Image image);
        void setRequestState(int state);
    }

}
