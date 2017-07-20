package com.tsiro.dogvip.petlikes;

import android.view.View;

/**
 * Created by giannis on 5/7/2017.
 */

public class PetLikesPresenter implements PetLikesContract.Presenter {

    private PetLikesContract.View mView;

    public PetLikesPresenter(PetLikesContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void onPetImageClick(View view) {
        mView.onPetImageClick(view);
    }

    @Override
    public void onMessageIconClick(View view) {
        mView.onMessageIconClick(view);
    }
}
