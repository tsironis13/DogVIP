package com.tsiro.dogvip.ownerpets;

import android.view.View;

/**
 * Created by giannis on 6/7/2017.
 */

public class OwnerPetsPresenter implements OwnerPetsContract.Presenter {

    private OwnerPetsContract.View mView;

    public OwnerPetsPresenter(OwnerPetsContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void onPetImageClick(View view) {
        mView.onPetImageClick(view);
    }

}
