package com.tsiro.dogvip.lostfound.manipulatefoundpet;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.lostfound.ManipulateLostFoundPet;
import com.tsiro.dogvip.POJO.lostfound.ManipulateLostFoundPetResponse;
import com.tsiro.dogvip.app.Lifecycle;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by giannis on 13/7/2017.
 */

public interface ManipulateFoundPetContract {

    interface View extends Lifecycle.View {
        void onSuccess(ManipulateLostFoundPetResponse response);
        void onError(int resource);
        void onImageActionSuccess(Image image); //upload, delete image
        void onImageActionError();
//        void onBaseViewClick(android.view.View view);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void manipulateFoundPet(ManipulateLostFoundPet request);
        void uploadImage(RequestBody action, RequestBody token, RequestBody id, MultipartBody.Part image);
        void deleteImage(Image image);
        void setRequestState(int state);
    }

}
