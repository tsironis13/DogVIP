package com.tsiro.dogvip.utilities.animation;

import android.util.Log;
import android.view.animation.Animation;

import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;


/**
 * Created by giannis on 20/5/2017.
 */
public class AnimationListener extends Animation implements Animation.AnimationListener {

    public AnimationListener() {
    }

    @Override
    public void onAnimationStart(Animation animation) {
        RxEventBus.createSubject(AppConfig.FRAGMENT_ANIMATION, AppConfig.PUBLISH_SUBJ).post(true);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        RxEventBus.createSubject(AppConfig.FRAGMENT_ANIMATION, AppConfig.PUBLISH_SUBJ).post(false);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

}

