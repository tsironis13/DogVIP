package com.tsiro.dogvip.POJO.petsitter;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.databinding.library.baseAdapters.BR;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tsiro.dogvip.POJO.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giannis on 6/9/2017.
 */

public class PetSitterObj extends BaseObservable implements Parcelable {

    @SerializedName("code")
    @Expose
    private int code;
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
    @SerializedName("subaction")
    @Expose
    private String subaction;
    @SerializedName("authtoken")
    @Expose
    private String authtoken;
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
    @SerializedName("petsize")
    @Expose
    private boolean petsize;
    @SerializedName("yearsexpr")
    @Expose
    private String yearsexpr;
    @SerializedName("services")
    @Expose
    private ArrayList<Integer> services;
    @SerializedName("urls")
    @Expose
    private ArrayList<Image> urls;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("petplace")
    @Expose
    private int petplace;
    @SerializedName("placetype")
    @Expose
    private int placetype;
    //update SitterServices table rows if has_services is true
    //otherwise insert new rows in the table
    @SerializedName("has_services")
    @Expose
    private boolean has_services;
    @SerializedName("start_date")
    @Expose
    private long start_date;
    @SerializedName("end_date")
    @Expose
    private long end_date;

    public PetSitterObj() {}

    private PetSitterObj(Parcel in) {
        id = in.readInt();
        code = in.readInt();
        stringlength = in.readByte() != 0;
        action = in.readString();
        subaction = in.readString();
        authtoken = in.readString();
        name = in.readString();
        surname = in.readString();
        displayage = in.readString();
        age = in.readLong();
        city = in.readString();
        phone = in.readString();
        imageurl = in.readString();
        petsize = in.readByte() != 0;
        yearsexpr = in.readString();
        if (services == null) services = new ArrayList<>();
        in.readList(services, Integer.class.getClassLoader());
        if (urls == null) urls = new ArrayList<>();
        in.readTypedList(urls, Image.CREATOR);
        address = in.readString();
        petplace = in.readInt();
        placetype = in.readInt();
        has_services = in.readByte() != 0;
        start_date = in.readLong();
        end_date = in.readLong();
    }

    public static final Creator<PetSitterObj> CREATOR = new Creator<PetSitterObj>() {
        @Override
        public PetSitterObj createFromParcel(Parcel in) {
            return new PetSitterObj(in);
        }

        @Override
        public PetSitterObj[] newArray(int size) {
            return new PetSitterObj[size];
        }
    };

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public boolean isStringlength() { return stringlength; }

    public void setStringlength(boolean stringlength) { this.stringlength = stringlength; }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getSubaction() { return subaction; }

    public void setSubaction(String subaction) { this.subaction = subaction; }

    @Bindable
    public String getAuthtoken() { return authtoken; }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
        notifyPropertyChanged(BR.authtoken);
    }

    @Bindable
    public Integer getId() { return id; }

    public void setId(Integer id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }

    public void setSurname(String surname) { this.surname = surname; }

    @Bindable
    public String getDisplayage() { return displayage; }

    public void setDisplayage(String displayage) {
        this.displayage = displayage;
        notifyPropertyChanged(BR.displayage);
    }

    @Bindable
    public long getAge() { return age; }

    public void setAge(long age) {
        this.age = age;
        notifyPropertyChanged(BR.age);
    }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getImageurl() { return imageurl; }

    public void setImageurl(String imageurl) { this.imageurl = imageurl; }

    @Bindable
    public boolean getPetsize() { return petsize; }

    public void setPetsize(boolean petsize) {
        this.petsize = petsize;
        notifyPropertyChanged(BR.petsize);
    }

    @Bindable
    public String getYearsexpr() { return yearsexpr; }

    public void setYearsexpr(String yearsexpr) {
        this.yearsexpr = yearsexpr;
        notifyPropertyChanged(BR.yearsexpr);
    }

    @Bindable
    public ArrayList<Integer> getServices() { return services; }

    public void setServices(ArrayList<Integer> services) {
        this.services = services;
        notifyPropertyChanged(BR.services);
    }

    public ArrayList<Image> getUrls() { return urls; }

    public void setUrls(ArrayList<Image> urls) { this.urls = urls; }

    @Bindable
    public String getAddress() { return address; }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

    @Bindable
    public int getPetplace() { return petplace; }

    public void setPetplace(int petplace) {
        this.petplace = petplace;
        notifyPropertyChanged(BR.petplace);
    }

    @Bindable
    public int getPlacetype() { return placetype; }

    public void setPlacetype(int placetype) {
        this.placetype = placetype;
        notifyPropertyChanged(BR.placetype);
    }

    public boolean isHas_services() { return has_services; }

    public void setHas_services(boolean has_services) { this.has_services = has_services; }

    public long getStart_date() { return start_date; }

    public void setStart_date(long start_date) { this.start_date = start_date; }

    public long getEnd_date() { return end_date; }

    public void setEnd_date(long end_date) { this.end_date = end_date; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(code);
        dest.writeByte((byte) (stringlength ? 1 : 0));
        dest.writeString(action);
        dest.writeString(subaction);
        dest.writeString(authtoken);
        dest.writeString(name);
        dest.writeString(surname);
        dest.writeString(displayage);
        dest.writeLong(age);
        dest.writeString(city);
        dest.writeString(phone);
        dest.writeString(imageurl);
        dest.writeByte((byte) (petsize ? 1 : 0));
        dest.writeString(yearsexpr);
        dest.writeList(services);
        dest.writeTypedList(urls);
        dest.writeString(address);
        dest.writeInt(petplace);
        dest.writeInt(placetype);
        dest.writeByte((byte) (has_services ? 1 : 0));
        dest.writeLong(start_date);
        dest.writeLong(end_date);
    }
}
