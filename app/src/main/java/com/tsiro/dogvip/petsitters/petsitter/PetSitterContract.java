package com.tsiro.dogvip.petsitters.petsitter;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.app.Lifecycle;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by giannis on 6/9/2017.
 */

public interface PetSitterContract {

    interface View extends Lifecycle.View {
        void onSuccess(PetSitterObj response);
        void onError(int resource, boolean msglength);
        void onImageActionSuccess(Image image); //upload, delete image
        void onImageActionError();
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void petSitterRelatedActions(PetSitterObj petSitterObj);
        void uploadImage(RequestBody action, RequestBody token, RequestBody id, MultipartBody.Part image);
        void deleteImage(Image image);
        void setRequestState(int state);
    }

}
