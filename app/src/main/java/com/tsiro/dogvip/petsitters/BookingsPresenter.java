package com.tsiro.dogvip.petsitters;

import android.util.Log;
import android.view.View;

/**
 * Created by thomatou on 9/20/17.
 */

public class BookingsPresenter implements PetSittersContract.Presenter {

    private PetSittersContract.FrgmtView mView;

    public BookingsPresenter(PetSittersContract.FrgmtView view) {
        mView = view;
    }

    @Override
    public void onBaseViewClick(View view) {
        mView.onBaseViewClick(view);
    }

}
