package com.tsiro.dogvip.mychatrooms;

import android.view.View;

/**
 * Created by giannis on 20/7/2017.
 */

public class MyChatRoomsPresenter implements MyChatRoomsContract.Presenter {

    private MyChatRoomsContract.View mView;

    public MyChatRoomsPresenter(MyChatRoomsContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void onBaseViewClick(View view) {
        mView.onBaseViewClick(view);
    }
}
