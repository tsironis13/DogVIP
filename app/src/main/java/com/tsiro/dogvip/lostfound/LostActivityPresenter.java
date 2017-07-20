package com.tsiro.dogvip.lostfound;

import android.view.View;

import com.tsiro.dogvip.lostfound.manipulatelostpet.ManipulateLostPetContract;

/**
 * Created by giannis on 11/7/2017.
 */

public class LostActivityPresenter implements LostFoundContract.Presenter {

    private LostFoundContract.FrgmtView viewcntr;

    public LostActivityPresenter(LostFoundContract.FrgmtView viewcntr) {
        this.viewcntr = viewcntr;
    }

    @Override
    public void onBaseViewClick(View view) {
        viewcntr.onBaseViewClick(view);
    }

}
