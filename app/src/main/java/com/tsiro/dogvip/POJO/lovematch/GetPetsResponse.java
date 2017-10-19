package com.tsiro.dogvip.POJO.lovematch;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tsiro.dogvip.POJO.BaseResponseObj;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;

import java.util.ArrayList;

/**
 * Created by giannis on 9/10/2017.
 */

public class GetPetsResponse {

    @SerializedName("data")
    @Expose
    private ArrayList<PetObj> data;
    @SerializedName("timestamp")
    @Expose
    public static long timestamp = System.currentTimeMillis()/1000;
    private static final long STALE_MS = 10; // Data is stale after 5 seconds
    public static long destale;

//    public static long stale = 1507982946;

    public ArrayList<PetObj> getData() {
        return data;
    }

    public void setData(ArrayList<PetObj> data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setState(long timestamp) {
        this.timestamp = timestamp;
    }

    public static boolean isUpToDate() {
//        Log.e("timestmap", timestamp + " current "+System.currentTimeMillis()/1000);
        return System.currentTimeMillis()/1000 - timestamp < STALE_MS;
    }
}
