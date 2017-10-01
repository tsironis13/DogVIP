package com.tsiro.dogvip.image_states;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.tsiro.dogvip.POJO.TestImage;

import java.io.File;

/**
 * Created by giannis on 30/9/2017.
 */

public class UrlImageState implements State, Parcelable {

    private String url;

    public UrlImageState(String url) {
        this.url = url;
    }

    private UrlImageState(Parcel in) {
        url = in.readString();
    }

    public static final Creator<UrlImageState> CREATOR = new Creator<UrlImageState>() {
        @Override
        public UrlImageState createFromParcel(Parcel in) {
            return new UrlImageState(in);
        }

        @Override
        public UrlImageState[] newArray(int size) {
            return new UrlImageState[size];
        }
    };

    @Override
    public void loadImage(ImageUploadViewModel viewModel, TestImage testImage) {
        Log.e("state", "UrlImageState");
//        Log.e("", "UrlImageState "+ url + " a "+ this.ownerViewModel  );
        viewModel.loadImageUrl(url, this);
    }

//    @Override
//    public void isImageValid(OwnerContract.ViewModel viewModel, Uri uri, TestImage testImage) {
//    }

    @Override
    public void uploadImageToServer(ImageUploadViewModel viewModel, String action, String token, int ownerId, File file) {
        Log.e("no do nothing", "no do nothing");
    }

    @Override
    public void deleteImage(ImageUploadViewModel viewModel, String action, String token, int id) {}

    @Override
    public void onSuccess(ImageUploadViewModel viewModel) {
//        viewModel.onSuccessUpload();
    }

    @Override
    public void onError(ImageUploadViewModel viewModel) {
//        viewModel.onErrorUpload();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
    }
}
