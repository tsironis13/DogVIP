package com.tsiro.dogvip.image_states;

import android.os.Parcel;
import android.os.Parcelable;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.TestImage;

import java.io.File;

/**
 * Created by giannis on 30/9/2017.
 */

public class CameraImageState implements State, Parcelable {

    public CameraImageState() {

    }

    protected CameraImageState(Parcel in) {
    }

    public static final Creator<CameraImageState> CREATOR = new Creator<CameraImageState>() {
        @Override
        public CameraImageState createFromParcel(Parcel in) {
            return new CameraImageState(in);
        }

        @Override
        public CameraImageState[] newArray(int size) {
            return new CameraImageState[size];
        }
    };

    @Override
    public void loadImage(ImageUploadViewModel viewModel, TestImage testImage) {
        viewModel.isCameraImageValid(testImage, this);
    }

//    @Override
//    public void isImageValid(OwnerContract.ViewModel viewModel, Uri uri, TestImage testImage) {
//
//    }

    @Override
    public void uploadImageToServer(ImageUploadViewModel viewModel, String action, String token, int ownerId, File file) {
        viewModel.uploadImage(this, action, token, ownerId, file);
    }

//    @Override
//    public void deleteImage(ImageUploadViewModel viewModel, String action, String token, int id) {}

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
