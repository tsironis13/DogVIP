package com.tsiro.dogvip.POJO.lostfound;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.databinding.library.baseAdapters.BR;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 10/7/2017.
 */

public class LostFoundObj extends BaseObservable implements Parcelable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("user_id")
    @Expose
    private int user_id;
    @SerializedName("p_id")
    @Expose
    private int p_id;
    @SerializedName("p_name")
    @Expose
    private String p_name;
    @SerializedName("race")
    @Expose
    private String race;
    @SerializedName("genre")
    @Expose
    private int genre;
    @SerializedName("half_blood")
    @Expose
    private int halfblood;
    @SerializedName("microchip")
    @Expose
    private String microchip;
    @SerializedName("p_displayage")
    @Expose
    private String p_displayage;
    @SerializedName("main_image")
    @Expose
    private String main_image;
    @SerializedName("thumb_image")
    @Expose
    private String thumb_image;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("date_lost")
    @Expose
    private long date_lost;
    @SerializedName("displaydate")
    @Expose
    private String displaydate;
    @SerializedName("time_lost")
    @Expose
    private String time_lost;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("info")
    @Expose
    private String info;

    public LostFoundObj() {}

    private LostFoundObj(Parcel in) {
        id = in.readInt();
        user_id = in.readInt();
        p_id = in.readInt();
        p_name = in.readString();
        race = in.readString();
        halfblood = in.readInt();
        microchip = in.readString();
        main_image = in.readString();
        thumb_image = in.readString();
        genre = in.readInt();
        location = in.readString();
        p_displayage = in.readString();
        displaydate = in.readString();
        date_lost = in.readLong();
        time_lost = in.readString();
        phone = in.readString();
        info = in.readString();
    }

    public static final Creator<LostFoundObj> CREATOR = new Creator<LostFoundObj>() {
        @Override
        public LostFoundObj createFromParcel(Parcel in) {
            return new LostFoundObj(in);
        }

        @Override
        public LostFoundObj[] newArray(int size) {
            return new LostFoundObj[size];
        }
    };

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getUser_id() { return user_id; }

    public void setUser_id(int user_id) { this.user_id = user_id; }

    public int getP_id() { return p_id; }

    public void setP_id(int p_id) { this.p_id = p_id; }

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public int getHalfblood() { return halfblood; }

    public void setHalfblood(int halfblood) { this.halfblood = halfblood; }

    public String getMicroship() { return microchip; }

    public void setMicroship(String microchip) { this.microchip = microchip; }

    public String getP_displayage() { return p_displayage; }

    public void setP_displayage(String p_displayage) { this.p_displayage = p_displayage; }

    public String getMain_image() { return main_image; }

    public void setMain_image(String main_image) { this.main_image = main_image; }

    public String getThumb_image() { return thumb_image; }

    public void setThumb_image(String thumb_image) { this.thumb_image = thumb_image; }

    public int getGenre() {
        return genre;
    }

    public void setGenre(int genre) {
        this.genre = genre;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getDate_lost() {
        return date_lost;
    }

    public void setDate_lost(long date_lost) {
        this.date_lost = date_lost;
    }
    @Bindable
    public String getDisplaydate() { return displaydate; }

    public void setDisplaydate(String displaydate) {
        this.displaydate = displaydate;
        notifyPropertyChanged(BR.displaydate);
    }
    @Bindable
    public String getTime_lost() {
        return time_lost;
    }

    public void setTime_lost(String time_lost) {
        this.time_lost = time_lost;
        notifyPropertyChanged(BR.time_lost);
    }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getInfo() { return info; }

    public void setInfo(String info) { this.info = info; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(user_id);
        dest.writeInt(p_id);
        dest.writeString(p_name);
        dest.writeString(race);
        dest.writeInt(halfblood);
        dest.writeString(microchip);
        dest.writeString(main_image);
        dest.writeString(thumb_image);
        dest.writeInt(genre);
        dest.writeString(location);
        dest.writeString(p_displayage);
        dest.writeString(displaydate);
        dest.writeLong(date_lost);
        dest.writeString(time_lost);
        dest.writeString(phone);
        dest.writeString(info);
    }
}
