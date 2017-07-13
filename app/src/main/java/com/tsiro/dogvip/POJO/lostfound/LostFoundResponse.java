package com.tsiro.dogvip.POJO.lostfound;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;

import java.util.ArrayList;

/**
 * Created by giannis on 10/7/2017.
 */

public class LostFoundResponse {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("id") //user role id
    @Expose
    private int id;
    @SerializedName("data")
    @Expose
    private ArrayList<LostFoundObj> data;
    @SerializedName("my_data")
    @Expose
    private ArrayList<LostFoundObj> my_data;
    @SerializedName("my_pets")
    @Expose
    private ArrayList<PetObj> my_pets;

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public ArrayList<LostFoundObj> getData() { return data; }

    public void setData(ArrayList<LostFoundObj> data) { this.data = data; }

    public ArrayList<LostFoundObj> getMy_data() { return my_data; }

    public void setMy_data(ArrayList<LostFoundObj> my_data) { this.my_data = my_data; }

    public ArrayList<PetObj> getMy_pets() { return my_pets; }

    public void setMy_pets(ArrayList<PetObj> my_pets) { this.my_pets = my_pets; }
}
