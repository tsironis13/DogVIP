package com.tsiro.dogvip.POJO.mypets.pet;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.databinding.library.baseAdapters.BR;
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
    private int p_id;
    @SerializedName("user_role_id")
    @Expose
    private int user_role_id;
    @SerializedName("p_name")
    @Expose
    private String p_name;
    @SerializedName("race")
    @Expose
    private String race;
    @SerializedName("microchip")
    @Expose
    private String microchip;
    @SerializedName("p_displayage")
    @Expose
    private String p_displayage;
    @SerializedName("p_age")
    @Expose
    private long p_age;
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
    @SerializedName("main_image")
    @Expose
    private String main_image;
    @SerializedName("urls")
    @Expose
    private ArrayList<Image> urls;
    @SerializedName("liked")
    @Expose
    private int liked;
    @SerializedName("total_likes")
    @Expose
    private int total_likes;
    @SerializedName("strurls")
    @Expose
    private String strurls;
    @SerializedName("name")
    @Expose
    private String ownername;
    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("displayage")
    @Expose
    private String ownerage;
    @SerializedName("city")
    @Expose
    private String ownercity;
    @SerializedName("image_url")
    @Expose
    private String image_url;
//    @SerializedName("liked_at")
//    @Expose
//    private String liked_at;

    public PetObj() {}

    private PetObj(Parcel in) {
        p_id = in.readInt();
        user_role_id = in.readInt();
        p_name = in.readString();
        race = in.readString();
        microchip = in.readString();
        p_displayage = in.readString();
        p_age = in.readLong();
        p_city = in.readString();
        weight = in.readString();
        neutered = in.readInt();
        main_image = in.readString();
        total_likes = in.readInt();
        genre = in.readInt();
        chtr = in.readString();
        if (urls == null) urls = new ArrayList<>();
        in.readTypedList(urls, Image.CREATOR);
        ownername = in.readString();
        surname = in.readString();
        ownerage = in.readString();
        ownercity = in.readString();
        image_url = in.readString();
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

    public String getP_name() { return p_name; }

    public void setP_name(String p_name) { this.p_name = p_name; }

    public String getRace() { return race; }

    public int isGenre() { return genre; }

    public void setGenre(int genre) { this.genre = genre; }

    public void setRace(String race) { this.race = race; }

    public String getMicroship() { return microchip; }

    public void setMicroship(String microship) { this.microchip = microship; }
    @Bindable
    public Long getAge() { return p_age; }

    public void setAge(Long p_age) {
        this.p_age = p_age;
        notifyPropertyChanged(BR.age);
    }
    @Bindable
    public String getP_displayage() { return p_displayage; }

    public void setP_displayage(String p_displayage) {
        this.p_displayage = p_displayage;
        notifyPropertyChanged(BR.p_displayage);
    }

    public String getMain_url() { return main_image; }

    public void setMain_url(String main_url) { this.main_image = main_url; }

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

    public int isLiked() { return liked; }

    public void setLiked(int liked) { this.liked = liked; }

    public int getTotal_likes() { return total_likes; }

    public void setTotal_likes(int total_likes) { this.total_likes = total_likes; }

    public String getStrurls() { return strurls; }

    public void setStrurls(String strurls) { this.strurls = strurls; }

    public String getOwnername() { return ownername; }

    public void setOwnername(String ownername) { this.ownername = ownername; }

    public String getOwnerage() { return ownerage; }

    public void setOwnerage(String ownerage) { this.ownerage = ownerage; }

    public String getOwnercity() { return ownercity; }

    public void setOwnercity(String ownercity) { this.ownercity = ownercity; }

    public String getSurname() { return surname; }

    public void setSurname(String surname) { this.surname = surname; }

    public String getUserUrl() { return image_url; }

    public void setUserUrl(String image_url) { this.image_url = image_url; }

    public String getImage_url() { return image_url; }

    public void setImage_url(String image_url) { this.image_url = image_url; }

//    public String getLiked_at() { return liked_at; }
//
//    public void setLiked_at(String liked_at) { this.liked_at = liked_at; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(p_id);
        dest.writeInt(user_role_id);
        dest.writeString(p_name);
        dest.writeString(race);
        dest.writeString(microchip);
        dest.writeString(p_displayage);
        dest.writeLong(p_age);
        dest.writeString(p_city);
        dest.writeString(weight);
        dest.writeInt(neutered);
        dest.writeString(main_image);
        dest.writeInt(total_likes);
        dest.writeInt(genre);
        dest.writeString(chtr);
        dest.writeTypedList(urls);
        dest.writeString(ownername);
        dest.writeString(surname);
        dest.writeString(ownerage);
        dest.writeString(ownercity);
        dest.writeString(image_url);
    }
}
