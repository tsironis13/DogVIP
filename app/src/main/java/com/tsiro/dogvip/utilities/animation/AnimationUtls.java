package com.tsiro.dogvip.utilities.animation;

import android.view.View;

import javax.inject.Inject;

/**
 * Created by giannis on 23/10/2017.
 */

public class AnimationUtls {

    @Inject
    public AnimationUtls() {}

    public void animateLoadingIndicator(View loadingView) {
        loadingView.setAlpha(0.f);
        loadingView.setScaleX(0.f);
        loadingView.setScaleY(0.f);
        loadingView.animate()
                        .alpha(1.f)
                        .scaleX(1.f).scaleY(1.f)
                        .start();
    }


}
