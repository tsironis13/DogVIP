package com.tsiro.dogvip.image_states;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.TestImage;

import java.io.File;

/**
 * Created by giannis on 30/9/2017.
 */

public class GalleryImageState implements State, Parcelable {

    public GalleryImageState() {}

    private GalleryImageState(Parcel in) {
    }

    public static final Creator<GalleryImageState> CREATOR = new Creator<GalleryImageState>() {
        @Override
        public GalleryImageState createFromParcel(Parcel in) {
            return new GalleryImageState(in);
        }

        @Override
        public GalleryImageState[] newArray(int size) {
            return new GalleryImageState[size];
        }
    };

    @Override
    public void loadImage(ImageUploadViewModel viewModel, TestImage testImage) {
        viewModel.isGalleryImageValid(testImage, this);
    }

    @Override
    public void uploadImageToServer(ImageUploadViewModel viewModel, String action, String token, int ownerId, File file) {
        viewModel.uploadImage(this, action, token, ownerId, file);
    }

    @Override
    public void onSuccess(Image image, ImageUploadViewModel viewModel) {
        viewModel.onSuccessUpload(image);
    }

    @Override
    public void onError(ImageUploadViewModel viewModel) {
        viewModel.onErrorUpload();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
