package com.tsiro.dogvip.petprofile;

import com.tsiro.dogvip.petlikes.PetLikesPresenter;

/**
 * Created by giannis on 6/7/2017.
 */

public class PetProfilePresenter implements PetProfileContract.Presenter {

    private PetProfileContract.View mView;

    public PetProfilePresenter(PetProfileContract.View view) {
        mView = view;
    }

    @Override
    public void onPetImgClick() {
        mView.onPetImgClick();
    }
}
