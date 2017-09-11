package com.tsiro.dogvip.requestmngrlayer;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.networklayer.ImageUploadControlAPIService;
import com.tsiro.dogvip.uploadimagecontrol.ImageUploadControlViewModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by giannis on 25/6/2017.
 */

public class ImageUploadControlRequestManager {

    private static ImageUploadControlRequestManager mInstance;
    private ImageUploadControlAPIService mImageUploadControlAPIService;

    private ImageUploadControlRequestManager() {
        this.mImageUploadControlAPIService = new ImageUploadControlAPIService();
    }

    public static ImageUploadControlRequestManager getInstance() {
        if (mInstance == null) mInstance = new ImageUploadControlRequestManager();
        return mInstance;
    }

    public Flowable<Image> uploadImage(RequestBody action, RequestBody token, RequestBody id, MultipartBody.Part file, ImageUploadControlViewModel imageUploadControlViewModel) {
        return mImageUploadControlAPIService.uploadImage(action, token, id, file, imageUploadControlViewModel);
    }

    public Flowable<Image> uploadPetImage(RequestBody action, RequestBody token, RequestBody user_role_id, RequestBody pet_id, MultipartBody.Part file, RequestBody index, ImageUploadControlViewModel imageUploadControlViewModel) {
        return mImageUploadControlAPIService.uploadPetImage(action, token, user_role_id, pet_id, file, index, imageUploadControlViewModel);
    }

    public Flowable<Image> uploadPetSitterPlaceImage(RequestBody action, RequestBody token, RequestBody id, MultipartBody.Part file, RequestBody index, ImageUploadControlViewModel imageUploadControlViewModel) {
        return mImageUploadControlAPIService.uploadPetSitterPlaceImage(action, token, id, file, index, imageUploadControlViewModel);
    }

    public Flowable<Image> deletePetImage(Image image, ImageUploadControlViewModel imageUploadControlViewModel) {
        return mImageUploadControlAPIService.deletePetImage(image, imageUploadControlViewModel).delay(500, TimeUnit.MILLISECONDS);
    }

}
