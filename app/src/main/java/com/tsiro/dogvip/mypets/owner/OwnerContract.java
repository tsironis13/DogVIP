package com.tsiro.dogvip.mypets.owner;

import android.net.Uri;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.TestImage;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.image_states.State;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by giannis on 4/6/2017.
 */

public class OwnerContract {

    interface View extends Lifecycle.View {
        void onSuccess(OwnerObj response);
        void onError(int resource, boolean msglength);
//        void onImageActionSuccess(Image image); //upload, delete image
//        void onImageActionError();
    }

    public interface ViewModel extends Lifecycle.ViewModel {
        void submitOwner(OwnerObj request);
//        void submitOwner(RequestBody request, RequestBody file);
//        void deleteImage(Image image);
        void setRequestState(int state);
    }

}
