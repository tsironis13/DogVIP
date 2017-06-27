package com.tsiro.dogvip.POJO.mypets.pet;

import android.databinding.BaseObservable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tsiro.dogvip.POJO.Image;

import java.util.ArrayList;

/**
 * Created by giannis on 23/6/2017.
 */

public class PetObj extends BaseObservable implements Parcelable {

    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("authtoken")
    @Expose
    private String authtoken;
    @SerializedName("p_id")
    @Expose
    private Integer p_id;
    @SerializedName("user_role_id")
    @Expose
    private Integer user_role_id;
    @SerializedName("p_name")
    @Expose
    private String p_name;
    @SerializedName("race")
    @Expose
    private String race;
    @SerializedName("microchip")
    @Expose
    private String microchip;
    @SerializedName("p_age")
    @Expose
    private String p_age;
    @SerializedName("p_city")
    @Expose
    private String p_city;
    @SerializedName("weight")
    @Expose
    private String weight;
    @SerializedName("neutered")
    @Expose
    private int neutered;
    @SerializedName("genre")
    @Expose
    private int genre;
    @SerializedName("chtr")
    @Expose
    private String chtr;
    @SerializedName("urls")
    @Expose
    private ArrayList<Image> urls;

    public PetObj() {}

    private PetObj(Parcel in) {
        p_id = in.readInt();
        user_role_id = in.readInt();
//        action = in.readString();
//        authtoken = in.readString();
        p_name = in.readString();
        race = in.readString();
        microchip = in.readString();
        p_age = in.readString();
        p_city = in.readString();
        weight = in.readString();
        neutered = in.readInt();
        genre = in.readInt();
        chtr = in.readString();
        if (urls == null) urls = new ArrayList<>();
        in.readTypedList(urls, Image.CREATOR);
    }

    public static final Creator<PetObj> CREATOR = new Creator<PetObj>() {
        @Override
        public PetObj createFromParcel(Parcel in) {
            return new PetObj(in);
        }

        @Override
        public PetObj[] newArray(int size) {
            return new PetObj[size];
        }
    };

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getAuthtoken() { return authtoken; }

    public void setAuthtoken(String authtoken) { this.authtoken = authtoken; }

    public Integer getId() { return p_id; }

    public void setId(Integer p_id) { this.p_id = p_id; }

    public Integer getUser_role_id() { return user_role_id; }

    public void setUser_role_id(Integer user_role_id) { this.user_role_id = user_role_id; }

    public String getName() { return p_name; }

    public void setName(String p_name) { this.p_name = p_name; }

    public String getRace() { return race; }

    public int isGenre() { return genre; }

    public void setGenre(int genre) { this.genre = genre; }

    public void setRace(String race) { this.race = race; }

    public String getMicroship() { return microchip; }

    public void setMicroship(String microship) { this.microchip = microship; }

    public String getAge() { return p_age; }

    public void setAge(String p_age) { this.p_age = p_age; }

    public String getCity() { return p_city; }

    public void setCity(String p_city) { this.p_city = p_city; }

    public String getWeight() { return weight; }

    public void setWeight(String weight) { this.weight = weight; }

    public int isNeutered() { return neutered; }

    public void setNeutered(int neutered) { this.neutered = neutered; }

    public String getCharacter() { return chtr; }

    public void setCharacter(String chtr) { this.chtr = chtr; }

    public ArrayList<Image> getUrls() { return urls; }

    public void setUrls(ArrayList<Image> urls) { this.urls = urls; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(action);
        dest.writeInt(p_id);
        dest.writeInt(user_role_id);
        dest.writeString(p_name);
        dest.writeString(race);
        dest.writeString(microchip);
        dest.writeString(p_age);
        dest.writeString(p_city);
        dest.writeString(weight);
        dest.writeInt(neutered);
        dest.writeInt(genre);
        dest.writeString(chtr);
        dest.writeTypedList(urls);
    }
}
