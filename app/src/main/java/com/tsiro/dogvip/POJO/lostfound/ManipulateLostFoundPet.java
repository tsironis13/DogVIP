package com.tsiro.dogvip.POJO.lostfound;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 11/7/2017.
 */

public class ManipulateLostFoundPet implements Parcelable {

    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("authtoken")
    @Expose
    private String authtoken;
    @SerializedName("obj")
    @Expose
    private LostFoundObj lostfoundobj;

    public ManipulateLostFoundPet() {}

    private ManipulateLostFoundPet(Parcel in) {
        action = in.readString();
        authtoken = in.readString();
        lostfoundobj = in.readParcelable(LostFoundObj.class.getClassLoader());
    }

    public static final Creator<ManipulateLostFoundPet> CREATOR = new Creator<ManipulateLostFoundPet>() {
        @Override
        public ManipulateLostFoundPet createFromParcel(Parcel in) {
            return new ManipulateLostFoundPet(in);
        }

        @Override
        public ManipulateLostFoundPet[] newArray(int size) {
            return new ManipulateLostFoundPet[size];
        }
    };

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getAuthtoken() { return authtoken; }

    public void setAuthtoken(String authtoken) { this.authtoken = authtoken; }

    public LostFoundObj getLostfoundobj() { return lostfoundobj; }

    public void setLostfoundobj(LostFoundObj lostfoundobj) { this.lostfoundobj = lostfoundobj; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(action);
        dest.writeString(authtoken);
        dest.writeParcelable(lostfoundobj, flags);
    }
}
