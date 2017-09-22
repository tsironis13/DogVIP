package com.tsiro.dogvip.POJO.petsitter;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tsiro.dogvip.POJO.lostfound.LostFoundObj;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giannis on 14/9/2017.
 */

public class OwnerSitterBookingsResponse implements Parcelable{

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("id") //user role id
    @Expose
    private int id;
    @SerializedName("sitter_bookings")
    @Expose
    private ArrayList<BookingObj> sitter_bookings;
    @SerializedName("owner_bookings")
    @Expose
    private ArrayList<BookingObj> owner_bookings;
    @SerializedName("owner_pets")
    @Expose
    private ArrayList<PetObj> owner_pets;
    @SerializedName("booking")
    @Expose
    private BookingObj booking;
    @SerializedName("data")
    @Expose
    private List<BookingObj> data;
    @SerializedName("comment_data")
    @Expose
    private List<CommentObj> comment_data;

    public OwnerSitterBookingsResponse() {}

    protected OwnerSitterBookingsResponse(Parcel in) {
        code = in.readInt();
        id = in.readInt();
        if (owner_bookings == null) owner_bookings = new ArrayList<>();
        owner_bookings = in.createTypedArrayList(BookingObj.CREATOR);
        if (sitter_bookings == null) sitter_bookings = new ArrayList<>();
        sitter_bookings = in.createTypedArrayList(BookingObj.CREATOR);
        if (owner_pets == null) owner_pets = new ArrayList<>();
        owner_pets = in.createTypedArrayList(PetObj.CREATOR);
    }

    public static final Creator<OwnerSitterBookingsResponse> CREATOR = new Creator<OwnerSitterBookingsResponse>() {
        @Override
        public OwnerSitterBookingsResponse createFromParcel(Parcel in) {
            return new OwnerSitterBookingsResponse(in);
        }

        @Override
        public OwnerSitterBookingsResponse[] newArray(int size) {
            return new OwnerSitterBookingsResponse[size];
        }
    };

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public ArrayList<BookingObj> getSitter_bookings() { return sitter_bookings; }

    public void setSitter_bookings(ArrayList<BookingObj> sitter_bookings) { this.sitter_bookings = sitter_bookings; }

    public ArrayList<BookingObj> getOwner_bookings() { return owner_bookings; }

    public void setOwner_bookings(ArrayList<BookingObj> owner_bookings) { this.owner_bookings = owner_bookings; }

    public ArrayList<PetObj> getOwner_pets() { return owner_pets; }

    public void setOwner_pets(ArrayList<PetObj> owner_pets) { this.owner_pets = owner_pets; }

    public BookingObj getBooking() { return booking; }

    public void setBooking(BookingObj booking) { this.booking = booking; }

    public List<BookingObj> getData() { return data; }

    public void setData(List<BookingObj> data) {
        this.data = data;
    }

    public List<CommentObj> getComment_data() { return comment_data; }

    public void setComment_data(List<CommentObj> comment_data) { this.comment_data = comment_data; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeInt(id);
        dest.writeTypedList(owner_bookings);
        dest.writeTypedList(sitter_bookings);
        dest.writeTypedList(owner_pets);
    }
}
