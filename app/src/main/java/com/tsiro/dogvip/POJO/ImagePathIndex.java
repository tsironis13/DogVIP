package com.tsiro.dogvip.POJO;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 27/6/2017.
 */

public class ImagePathIndex implements Parcelable{

    @SerializedName("index")
    @Expose
    private int index;
    @SerializedName("path")
    @Expose
    private String path;

    public ImagePathIndex() {}

    private ImagePathIndex(Parcel in) {
        index = in.readInt();
        path = in.readString();
    }

    public static final Creator<ImagePathIndex> CREATOR = new Creator<ImagePathIndex>() {
        @Override
        public ImagePathIndex createFromParcel(Parcel in) {
            return new ImagePathIndex(in);
        }

        @Override
        public ImagePathIndex[] newArray(int size) {
            return new ImagePathIndex[size];
        }
    };

    public int getIndex() { return index; }

    public void setIndex(int index) { this.index = index; }

    public String getPath() { return path; }

    public void setPath(String path) { this.path = path; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(index);
        dest.writeString(path);
    }
}
