package com.tsiro.dogvip.image_states;

import android.os.Parcelable;

import com.tsiro.dogvip.POJO.TestImage;

import java.io.File;

/**
 * Created by giannis on 30/9/2017.
 */

public interface State extends Parcelable {

    void loadImage(ImageUploadViewModel viewModel, TestImage testImage);
    void uploadImageToServer(ImageUploadViewModel viewModel, String action, String token, int id, File file);
    void deleteImage(ImageUploadViewModel viewModel, String action, String token, int id);
    void onSuccess(ImageUploadViewModel viewModel);
    void onError(ImageUploadViewModel viewModel);

}
