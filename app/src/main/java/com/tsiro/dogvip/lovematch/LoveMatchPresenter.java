package com.tsiro.dogvip.lovematch;

import android.view.View;

/**
 * Created by giannis on 3/7/2017.
 */

public class LoveMatchPresenter implements LoveMatchContract.Presenter {

    private LoveMatchContract.View mView;

    public LoveMatchPresenter(LoveMatchContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void onIndividualViewClick(View view) {
        mView.onIndividualViewClick(view);
    }
}
