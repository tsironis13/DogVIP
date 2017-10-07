package com.tsiro.dogvip.POJO.profs;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.databinding.library.baseAdapters.BR;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giannis on 30/9/2017.
 */

public class ProfObj extends BaseObservable implements Parcelable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("place_address")
    @Expose
    private String place_address;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("mobile_number")
    @Expose
    private String mobile_number;
    @SerializedName("image_url")
    @Expose
    private String image_url;
    @SerializedName("categories")
    @Expose
    private List<Integer> categories;

    protected ProfObj(Parcel in) {
        id = in.readInt();
        email = in.readString();
        name = in.readString();
        place_address = in.readString();
        city = in.readString();
        website = in.readString();
        mobile_number = in.readString();
        image_url = in.readString();
        if (categories == null) categories = new ArrayList<>();
        in.readList(categories, Integer.class.getClassLoader());
    }

    public static final Creator<ProfObj> CREATOR = new Creator<ProfObj>() {
        @Override
        public ProfObj createFromParcel(Parcel in) {
            return new ProfObj(in);
        }

        @Override
        public ProfObj[] newArray(int size) {
            return new ProfObj[size];
        }
    };
    @Bindable
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getPlace_address() {
        return place_address;
    }

    public void setPlace_address(String place_address) {
        this.place_address = place_address;
        notifyPropertyChanged(BR.place_address);
    }

    @Bindable
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
        notifyPropertyChanged(BR.city);
    }

    @Bindable
    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
        notifyPropertyChanged(BR.website);
    }

    @Bindable
    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
        notifyPropertyChanged(BR.mobile_number);
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public List<Integer> getCategories() { return categories; }

    public void setCategories(List<Integer> categories) { this.categories = categories; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(place_address);
        dest.writeString(city);
        dest.writeString(website);
        dest.writeString(mobile_number);
        dest.writeString(image_url);
        dest.writeList(categories);
    }
}
