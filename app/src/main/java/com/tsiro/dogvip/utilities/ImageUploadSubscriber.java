package com.tsiro.dogvip.utilities;

import android.util.Log;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.app.Lifecycle;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 20/6/2017.
 */

public class ImageUploadSubscriber extends DisposableSubscriber<Image> {

    private static final String debugTag = ImageUploadSubscriber.class.getSimpleName();
    private Lifecycle.ImageUploadModel imageUploadModel;

    public ImageUploadSubscriber(Lifecycle.ImageUploadModel imageUploadModel) {
        this.imageUploadModel = imageUploadModel;
    }

    @Override
    public void onNext(Image image) {
        Log.e(debugTag, image.getCode()+"");
        imageUploadModel.onSuccessImageAction(image);
    }

    @Override
    public void onError(Throwable t) {
        Log.e(debugTag, "onError"+t.toString());
        imageUploadModel.onErrorImageAction();
    }

    @Override
    public void onComplete() {
        Log.e(debugTag, "onComplete");
    }
}
