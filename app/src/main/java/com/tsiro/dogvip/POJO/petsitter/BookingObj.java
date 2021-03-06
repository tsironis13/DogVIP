package com.tsiro.dogvip.POJO.petsitter;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giannis on 14/9/2017.
 */

public class BookingObj implements Parcelable{

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("authtoken")
    @Expose
    private String authtoken;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("sitter_id")
    @Expose
    private int sitter_id;
    @SerializedName("owner_id")
    @Expose
    private int owner_id;
    @SerializedName("pet_id")
    @Expose
    private int pet_id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("p_name")
    @Expose
    private String p_name;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("image_url")
    @Expose
    private String image_url;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("main_image")
    @Expose
    private String main_image;
    @SerializedName("date_created")
    @Expose
    private String date_created;
    @SerializedName("start_date")
    @Expose
    private String start_date;
    @SerializedName("end_date")
    @Expose
    private String end_date;
    @SerializedName("long_start_date")
    @Expose
    private long long_start_date;
    @SerializedName("long_end_date")
    @Expose
    private long long_end_date;
    @SerializedName("services")
    @Expose
    private List<Integer> services;
    @SerializedName("completed")
    @Expose
    private int completed;
    @SerializedName("vote")
    @Expose
    private float vote;
    @SerializedName("state")
    @Expose
    private int state;

    public BookingObj() {}

    private BookingObj(Parcel in) {
        code = in.readInt();
        authtoken = in.readString();
        action = in.readString();
        id = in.readInt();
        sitter_id = in.readInt();
        owner_id = in.readInt();
        pet_id = in.readInt();
        name = in.readString();
        surname = in.readString();
        p_name = in.readString();
        city = in.readString();
        date_created = in.readString();
        start_date = in.readString();
        end_date = in.readString();
        if (services == null) services = new ArrayList<>();
        in.readList(services, Integer.class.getClassLoader());
        completed = in.readInt();
        vote = in.readFloat();
        state = in.readInt();
        image_url = in.readString();
    }

    public static final Creator<BookingObj> CREATOR = new Creator<BookingObj>() {
        @Override
        public BookingObj createFromParcel(Parcel in) {
            return new BookingObj(in);
        }

        @Override
        public BookingObj[] newArray(int size) {
            return new BookingObj[size];
        }
    };

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public String getAuthtoken() { return authtoken; }

    public void setAuthtoken(String authtoken) { this.authtoken = authtoken; }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getSitter_id() { return sitter_id; }

    public void setSitter_id(int sitter_id) { this.sitter_id = sitter_id; }

    public int getOwner_id() { return owner_id; }

    public void setOwner_id(int owner_id) { this.owner_id = owner_id; }

    public int getPet_id() { return pet_id; }

    public void setPet_id(int pet_id) { this.pet_id = pet_id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }

    public void setSurname(String surname) { this.surname = surname; }

    public String getP_name() { return p_name; }

    public void setP_name(String p_name) { this.p_name = p_name; }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getMain_image() { return main_image; }

    public void setMain_image(String main_image) { this.main_image = main_image; }

    public String getImage_url() { return image_url; }

    public void setImage_url(String image_url) { this.image_url = image_url; }

    public String getAge() { return age; }

    public void setAge(String age) { this.age = age; }

    public String getDate_created() { return date_created; }

    public void setDate_created(String date_created) { this.date_created = date_created; }

    public String getStart_date() { return start_date; }

    public void setStart_date(String start_date) { this.start_date = start_date; }

    public String getEnd_date() { return end_date; }

    public void setEnd_date(String end_date) { this.end_date = end_date; }

    public long getLong_start_date() { return long_start_date; }

    public void setLong_start_date(long long_start_date) { this.long_start_date = long_start_date; }

    public long getLong_end_date() { return long_end_date; }

    public void setLong_end_date(long long_end_date) { this.long_end_date = long_end_date; }

    public List<Integer> getServices() { return services; }

    public void setServices(List<Integer> services) { this.services = services; }

    public int getCompleted() { return completed; }

    public void setCompleted(int completed) { this.completed = completed; }

    public float getVote() { return vote; }

    public void setVote(float vote) { this.vote = vote; }

    public int getState() { return state; }

    public void setState(int state) { this.state = state; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeString(authtoken);
        dest.writeString(action);
        dest.writeInt(id);
        dest.writeInt(sitter_id);
        dest.writeInt(owner_id);
        dest.writeInt(pet_id);
        dest.writeString(name);
        dest.writeString(surname);
        dest.writeString(p_name);
        dest.writeString(city);
        dest.writeString(date_created);
        dest.writeString(start_date);
        dest.writeString(end_date);
        dest.writeList(services);
        dest.writeInt(completed);
        dest.writeFloat(vote);
        dest.writeInt(state);
        dest.writeString(image_url);
    }
}
