package com.tsiro.dogvip.utilities;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.image_states.ImageUploadViewModel;
import com.tsiro.dogvip.image_states.State;

import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 1/10/2017.
 */

public class GenericImageUploadSubscriber extends DisposableSubscriber<Image> {

    private ImageUploadViewModel imageUploadModel;
    private State state;

    public GenericImageUploadSubscriber(State state, ImageUploadViewModel imageUploadModel) {
        this.state = state;
        this.imageUploadModel = imageUploadModel;
    }

    @Override
    public void onNext(Image image) {
        state.onSuccess(image, imageUploadModel);
    }

    @Override
    public void onError(Throwable t) {
        state.onError(imageUploadModel);
    }

    @Override
    public void onComplete() {}
}
