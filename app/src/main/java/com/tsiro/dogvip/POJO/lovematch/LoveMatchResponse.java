package com.tsiro.dogvip.POJO.lovematch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;

import java.util.ArrayList;

/**
 * Created by giannis on 2/7/2017.
 */

public class LoveMatchResponse {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("exists")
    @Expose
    private boolean exists;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("subaction")
    @Expose
    private String subaction;
    @SerializedName("data")
    @Expose
    private ArrayList<PetObj> data;

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public boolean isExists() { return exists; }

    public void setExists(boolean exists) { this.exists = exists; }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getSubaction() { return subaction; }

    public void setSubaction(String subaction) { this.subaction = subaction; }

    public ArrayList<PetObj> getData() { return data; }

    public void setData(ArrayList<PetObj> data) { this.data = data; }
}
