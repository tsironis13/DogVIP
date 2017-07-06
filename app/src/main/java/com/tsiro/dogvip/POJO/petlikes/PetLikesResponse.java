package com.tsiro.dogvip.POJO.petlikes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;

import java.util.ArrayList;

/**
 * Created by giannis on 5/7/2017.
 */

public class PetLikesResponse {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("data")
    @Expose
    private ArrayList<OwnerObj> data;

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public ArrayList<OwnerObj> getData() { return data; }

    public void setData(ArrayList<OwnerObj> data) { this.data = data; }
}
