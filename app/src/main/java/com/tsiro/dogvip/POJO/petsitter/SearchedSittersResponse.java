package com.tsiro.dogvip.POJO.petsitter;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomatou on 9/13/17.
 */

public class SearchedSittersResponse implements Parcelable {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("data")
    @Expose
    private List<PetSitterObj> data;
    @SerializedName("id")
    @Expose
    private int id; //user role id
    private transient List<Integer> services;
    private transient String location;
    private transient String diplayStartDate;
    private transient String displayEndDate;
    private transient long startDate;
    private transient long endDate;

    public SearchedSittersResponse() {}

    private SearchedSittersResponse(Parcel in) {
        code = in.readInt();
        data = in.createTypedArrayList(PetSitterObj.CREATOR);
        id = in.readInt();
        if (services == null) services = new ArrayList<>();
        in.readList(services, Integer.class.getClassLoader());
        location = in.readString();
        diplayStartDate = in.readString();
        displayEndDate = in.readString();
        startDate = in.readLong();
        endDate = in.readLong();
    }

    public static final Creator<SearchedSittersResponse> CREATOR = new Creator<SearchedSittersResponse>() {
        @Override
        public SearchedSittersResponse createFromParcel(Parcel in) {
            return new SearchedSittersResponse(in);
        }

        @Override
        public SearchedSittersResponse[] newArray(int size) {
            return new SearchedSittersResponse[size];
        }
    };

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public List<PetSitterObj> getData() { return data; }

    public void setData(List<PetSitterObj> data) { this.data = data; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public List<Integer> getServices() { return services; }

    public void setServices(List<Integer> services) { this.services = services; }

    public String getLocation() { return location; }

    public void setLocation(String location) { this.location = location; }

    public String getDiplayStartDate() { return diplayStartDate; }

    public void setDiplayStartDate(String diplayStartDate) { this.diplayStartDate = diplayStartDate; }

    public String getDisplayEndDate() { return displayEndDate; }

    public void setDisplayEndDate(String displayEndDate) { this.displayEndDate = displayEndDate; }

    public long getStartDate() { return startDate; }

    public void setStartDate(long startDate) { this.startDate = startDate; }

    public long getEndDate() { return endDate; }

    public void setEndDate(long endDate) { this.endDate = endDate; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(code);
        parcel.writeTypedList(data);
        parcel.writeInt(id);
        parcel.writeList(services);
        parcel.writeString(location);
        parcel.writeString(diplayStartDate);
        parcel.writeString(displayEndDate);
        parcel.writeLong(startDate);
        parcel.writeLong(endDate);
    }
}
