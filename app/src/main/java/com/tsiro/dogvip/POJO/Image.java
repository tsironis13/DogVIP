package com.tsiro.dogvip.POJO;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by giannis on 17/6/2017.
 */
//class used also to get image upload response data
public class Image extends BaseObservable implements Parcelable{

    private transient File image;
    private transient boolean issize_valid;
    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("authtoken")
    @Expose
    private String authtoken;
    @SerializedName("imageurl")
    @Expose
    private String imageurl;
    @SerializedName("mainurl")
    @Expose
    private String mainurl;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("user_role_id")
    @Expose
    private int user_role_id; //used to delete pet image
    //refers to main pet image
    @SerializedName("main")
    @Expose
    private boolean main;
    //use this field when there is only 1 photo available
    //and user should be able to delete it
    @SerializedName("canchecked")
    @Expose
    private boolean canchecked;
    @SerializedName("checked_urls")
    @Expose
    private ArrayList<ImagePathIndex> checked_urls;
    private transient boolean isChecked;

    public Image() {}

    protected Image(Parcel in) {
        code = in.readInt();
        action = in.readString();
        authtoken = in.readString();
        imageurl = in.readString();
        id = in.readInt();
//        main_index = in.readInt();
        main = in.readByte() != 0;
        canchecked = in.readByte() != 0;
        if (checked_urls == null) checked_urls = new ArrayList<>();
        in.readTypedList(checked_urls, ImagePathIndex.CREATOR);
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getAuthtoken() { return authtoken; }

    public int getUser_role_id() { return user_role_id; }

    public void setUser_role_id(int user_role_id) { this.user_role_id = user_role_id; }

    public void setAuthtoken(String authtoken) { this.authtoken = authtoken; }

    public File getImage() { return image; }

    public String getMainurl() { return mainurl; }

    public void setMainurl(String mainurl) { this.mainurl = mainurl; }

    public void setImage(File image) {
        this.image = image;
    }

    public boolean getSize() {
        return issize_valid;
    }

    public void setSize(boolean issize_valid) {
        this.issize_valid = issize_valid;
    }

    public String getImageurl() { return imageurl; }

    public void setImageurl(String imageurl) { this.imageurl = imageurl; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public boolean isMain() { return main; }

    public boolean isCanchecked() { return canchecked; }

    public void setCanchecked(boolean canchecked) { this.canchecked = canchecked; }

    public void setMain(boolean main) { this.main = main; }

    public ArrayList<ImagePathIndex> getChecked_urls() { return checked_urls; }

    public void setChecked_urls(ArrayList<ImagePathIndex> checked_urls) { this.checked_urls = checked_urls; }

    @Bindable
    public boolean isChecked() { return isChecked; }

    public void setChecked(boolean checked) {
        isChecked = checked;
        notifyPropertyChanged(BR.checked);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeString(action);
        dest.writeString(authtoken);
        dest.writeString(imageurl);
        dest.writeInt(id);
//        dest.writeInt(main_index);
        dest.writeByte((byte) (main ? 1 : 0));
        dest.writeByte((byte) (canchecked ? 1 : 0));
        dest.writeTypedList(checked_urls);
//        dest.writeIntArray(checked_urls);
    }
}
