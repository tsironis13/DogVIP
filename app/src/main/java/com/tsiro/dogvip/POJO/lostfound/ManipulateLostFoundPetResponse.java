package com.tsiro.dogvip.POJO.lostfound;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 11/7/2017.
 */

public class ManipulateLostFoundPetResponse {

    @SerializedName("code")
    @Expose
    private int code;

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }
}
