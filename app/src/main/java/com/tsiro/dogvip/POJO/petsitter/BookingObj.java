package com.tsiro.dogvip.POJO.petsitter;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 14/9/2017.
 */

public class BookingObj implements Parcelable{

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
    @SerializedName("date_created")
    @Expose
    private String date_created;
    @SerializedName("start_date")
    @Expose
    private String start_date;
    @SerializedName("end_date")
    @Expose
    private String end_date;

    public BookingObj() {}

    private BookingObj(Parcel in) {
        id = in.readInt();
        sitter_id = in.readInt();
        owner_id = in.readInt();
        pet_id = in.readInt();
        date_created = in.readString();
        start_date = in.readString();
        end_date = in.readString();
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

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getSitter_id() { return sitter_id; }

    public void setSitter_id(int sitter_id) { this.sitter_id = sitter_id; }

    public int getOwner_id() { return owner_id; }

    public void setOwner_id(int owner_id) { this.owner_id = owner_id; }

    public int getPet_id() { return pet_id; }

    public void setPet_id(int pet_id) { this.pet_id = pet_id; }

    public String getDate_created() { return date_created; }

    public void setDate_created(String date_created) { this.date_created = date_created; }

    public String getStart_date() { return start_date; }

    public void setStart_date(String start_date) { this.start_date = start_date; }

    public String getEnd_date() { return end_date; }

    public void setEnd_date(String end_date) { this.end_date = end_date; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(sitter_id);
        dest.writeInt(owner_id);
        dest.writeInt(pet_id);
        dest.writeString(date_created);
        dest.writeString(start_date);
        dest.writeString(end_date);
    }
}