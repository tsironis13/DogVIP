package com.tsiro.dogvip.POJO.mypets.owner;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.android.databinding.library.baseAdapters.BR;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giannis on 16/6/2017.
 */
//this class is used to submit and get owner details
public class OwnerObj extends BaseObservable implements Parcelable {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("exists")
    @Expose
    private boolean exists;
    /*
     * used to display multi line or single line snackbar
     * depending the error msg string length
     * TRUE FOR BIG TEXT, FALSE FOR SMALL ONE
     */
    @SerializedName("stringlength")
    @Expose
    private boolean stringlength;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("authtoken")
    @Expose
    private String authtoken;
    /* state 0, no image selected,
     * state 1, gallery image selected,
     * state 2, camera image selected,
     * state 3, url image fetched
     */
//    @SerializedName("state")
//    @Expose
//    private Integer state;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("displayage")
    @Expose
    private String displayage;
    @SerializedName("age")
    @Expose
    private long age;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("imageurl")
    @Expose
    private String imageurl;
    @SerializedName("liked_at")
    @Expose
    private String liked_at;
    @SerializedName("pets")
    @Expose
    private ArrayList<PetObj> pets;

    public OwnerObj() {}

    private OwnerObj(Parcel in) {
        code = in.readInt();
        id = in.readInt();
        exists = in.readByte() != 0;
        stringlength = in.readByte() != 0;
        action = in.readString();
        authtoken = in.readString();
        name = in.readString();
        surname = in.readString();
        age = in.readLong();
        displayage = in.readString();
        city = in.readString();
        phone = in.readString();
        imageurl = in.readString();
        if (pets == null) pets = new ArrayList<>();
        in.readTypedList(pets, PetObj.CREATOR);
    }

    public static final Creator<OwnerObj> CREATOR = new Creator<OwnerObj>() {
        @Override
        public OwnerObj createFromParcel(Parcel in) {
            return new OwnerObj(in);
        }

        @Override
        public OwnerObj[] newArray(int size) {
            return new OwnerObj[size];
        }
    };

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

//    public Integer getState() {
//        return state;
//    }
//
//    public void setState(Integer state) {
//        this.state = state;
//    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Bindable
    public long getAge() { return age; }

    public void setAge(long age) {
        this.age = age;
        notifyPropertyChanged(BR.age);
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
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
        notifyPropertyChanged(BR.surname);
    }
    @Bindable
    public String getDisplayage() {
        return displayage;
    }

    public void setDisplayage(String displayage) {
        this.displayage = displayage;
        notifyPropertyChanged(BR.displayage);
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
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);
    }

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public boolean isExists() { return exists; }

    public void setExists(boolean exists) { this.exists = exists; }

    public boolean isStringlength() { return stringlength; }

    public void setStringlength(boolean stringlength) { this.stringlength = stringlength; }

    public String getImageurl() { return imageurl; }

    public void setImageurl(String imageurl) { this.imageurl = imageurl; }

    public ArrayList<PetObj> getPets() { return pets; }

    public void setPets(ArrayList<PetObj> pets) { this.pets = pets; }

    public String getLiked_at() { return liked_at; }

    public void setLiked_at(String liked_at) { this.liked_at = liked_at; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        if (id != null)dest.writeInt(id);
        dest.writeByte((byte) (exists ? 1 : 0));
        dest.writeByte((byte) (stringlength ? 1 : 0));
        dest.writeString(action);
        dest.writeString(authtoken);
        dest.writeString(name);
        dest.writeString(surname);
        dest.writeLong(age);
        dest.writeString(displayage);
        dest.writeString(city);
        dest.writeString(phone);
        dest.writeString(imageurl);
        dest.writeTypedList(pets);
    }
}
