package com.tsiro.dogvip.requestmngrlayer;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.image_states.ImageUploadViewModel;
import com.tsiro.dogvip.networklayer.ImageActionsAPIService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by giannis on 5/10/2017.
 */

public class ImageActionsRequestManager {

    private ImageActionsAPIService mImageActionsAPIService;

    @Inject
    public ImageActionsRequestManager(ImageActionsAPIService imageActionsAPIService) {
        this.mImageActionsAPIService = imageActionsAPIService;
    }

    public Flowable<Image> uploadImage(RequestBody action, RequestBody token, RequestBody id, MultipartBody.Part file, ImageUploadViewModel viewModel) {
        return mImageActionsAPIService.uploadImage(action, token, id, file, viewModel);
    }

    public Flowable<Image> deleteImage(Image image, ImageUploadViewModel viewModel) {
        return mImageActionsAPIService.deleteImage(image, viewModel);
    }

}
