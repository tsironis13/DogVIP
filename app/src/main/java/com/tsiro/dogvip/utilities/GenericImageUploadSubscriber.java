package com.tsiro.dogvip.utilities;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.image_states.ImageUploadViewModel;

import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 1/10/2017.
 */

public class GenericImageUploadSubscriber extends DisposableSubscriber<Image> {

    private ImageUploadViewModel imageUploadModel;

    public GenericImageUploadSubscriber(ImageUploadViewModel imageUploadModel) {
        this.imageUploadModel = imageUploadModel;
    }

    @Override
    public void onNext(Image image) {
        imageUploadModel.onSuccessImageAction(image);
    }

    @Override
    public void onError(Throwable t) {
        imageUploadModel.onErrorImageAction();
    }

    @Override
    public void onComplete() {}
}
