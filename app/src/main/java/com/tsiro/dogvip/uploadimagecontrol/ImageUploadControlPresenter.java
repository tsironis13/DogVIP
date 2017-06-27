package com.tsiro.dogvip.uploadimagecontrol;

import android.util.Log;
import android.view.View;

/**
 * Created by giannis on 27/6/2017.
 */

public class ImageUploadControlPresenter implements ImageUploadControlContract.Presenter {

    private ImageUploadControlContract.View viewcntr;

    public ImageUploadControlPresenter(ImageUploadControlContract.View viewcntr) {
        this.viewcntr = viewcntr;
    }

    @Override
    public void onCheckBoxClick(View view, boolean isChecked) {
        viewcntr.onCheckBoxClick(view, isChecked);
    }
}
