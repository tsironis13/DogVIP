package com.tsiro.dogvip.utilities;

import android.util.Log;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.app.Lifecycle;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 20/6/2017.
 */

public class ImageUploadSubscriber extends DisposableSubscriber<Image> {

    private Lifecycle.ImageUploadModel imageUploadModel;

    public ImageUploadSubscriber(Lifecycle.ImageUploadModel imageUploadModel) {
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
    public void onComplete() {
    }
}
