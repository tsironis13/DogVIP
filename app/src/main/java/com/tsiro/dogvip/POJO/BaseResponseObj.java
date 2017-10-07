package com.tsiro.dogvip.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 6/10/2017.
 */

public class BaseResponseObj {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("action")
    @Expose
    private String action;

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }
}
