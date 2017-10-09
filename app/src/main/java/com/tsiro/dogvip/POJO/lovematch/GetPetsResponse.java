package com.tsiro.dogvip.POJO.lovematch;

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

    public ArrayList<PetObj> getData() {
        return data;
    }

    public void setData(ArrayList<PetObj> data) {
        this.data = data;
    }
}
