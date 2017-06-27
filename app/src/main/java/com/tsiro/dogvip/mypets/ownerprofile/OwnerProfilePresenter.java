package com.tsiro.dogvip.mypets.ownerprofile;

import android.util.Log;
import android.view.View;

/**
 * Created by giannis on 24/6/2017.
 */

public class OwnerProfilePresenter implements OwnerProfileContract.Presenter {

    private OwnerProfileContract.View viewcntr;

    public OwnerProfilePresenter(OwnerProfileContract.View viewcntr) {
        this.viewcntr = viewcntr;
    }

    @Override
    public void onBaseViewClick(View view) {
        viewcntr.onBaseViewClick(view);
    }

    @Override
    public void onPetImgViewClick(View view) {
        viewcntr.onPetImgViewClick(view);
    }
}
