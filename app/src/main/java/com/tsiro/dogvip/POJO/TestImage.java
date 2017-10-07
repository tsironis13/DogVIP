package com.tsiro.dogvip.POJO;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.tsiro.dogvip.image_states.ImageUploadViewModel;
import com.tsiro.dogvip.image_states.NoImageState;
import com.tsiro.dogvip.image_states.State;
import com.tsiro.dogvip.image_states.UrlImageState;

import java.io.File;

/**
 * Created by giannis on 30/9/2017.
 */

public class TestImage implements Parcelable {

    private State state;
    private String url;
    private File file;
    private Uri uri;
    private boolean hasValidSize, isFileTypeInvalid;

    public TestImage(String url) {
        state = url.isEmpty() ? new NoImageState() : new UrlImageState(url);
        this.url = url;
    }

    private TestImage(Parcel in) {
        state = in.readParcelable(State.class.getClassLoader());
        uri = in.readParcelable(Uri.class.getClassLoader());
        file = (File) in.readSerializable();
        url = in.readString();
        hasValidSize = in.readByte() != 0;
        isFileTypeInvalid = in.readByte() != 0;
    }

    public static final Creator<TestImage> CREATOR = new Creator<TestImage>() {
        @Override
        public TestImage createFromParcel(Parcel in) {
            return new TestImage(in);
        }

        @Override
        public TestImage[] newArray(int size) {
            return new TestImage[size];
        }
    };

    public Uri getUri() { return uri; }

    public void setUri(Uri uri) { this.uri = uri; }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isHasValidSize() {
        return hasValidSize;
    }

    public void setHasValidSize(boolean hasValidSize) {
        this.hasValidSize = hasValidSize;
    }

    public boolean isFileTypeInvalid() {
        return isFileTypeInvalid;
    }

    public void setFileTypeInvalid(boolean isFileTypeInvalid) {
        isFileTypeInvalid = isFileTypeInvalid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(state, flags);
        dest.writeParcelable(uri, flags);
        dest.writeSerializable(file);
        dest.writeString(url);
        dest.writeByte((byte) (hasValidSize ? 1 : 0));
        dest.writeByte((byte) (isFileTypeInvalid ? 1 : 0));
    }
}
