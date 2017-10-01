package com.tsiro.dogvip.image_states;

import android.os.Parcel;
import android.os.Parcelable;

import com.tsiro.dogvip.POJO.TestImage;

import java.io.File;

/**
 * Created by giannis on 30/9/2017.
 */

public class NoImageState implements State, Parcelable {

    public NoImageState() {
    }

    protected NoImageState(Parcel in) {
    }

    public static final Creator<NoImageState> CREATOR = new Creator<NoImageState>() {
        @Override
        public NoImageState createFromParcel(Parcel in) {
            return new NoImageState(in);
        }

        @Override
        public NoImageState[] newArray(int size) {
            return new NoImageState[size];
        }
    };

    @Override
    public void loadImage(ImageUploadViewModel viewModel, TestImage testImage) {
        viewModel.noImageUrl();
    }

//    @Override
//    public void isImageValid(OwnerContract.ViewModel viewModel, Uri uri, TestImage testImage) {
//
//    }

    @Override
    public void uploadImageToServer(ImageUploadViewModel viewModel, String action, String token, int ownerId, File file) {}

    @Override
    public void deleteImage(ImageUploadViewModel viewModel, String action, String token, int id) {}

    @Override
    public void onSuccess(ImageUploadViewModel viewModel) {
        viewModel.onSuccessDelete();
    }

    @Override
    public void onError(ImageUploadViewModel viewModel) {
        viewModel.onErrorDelete();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
