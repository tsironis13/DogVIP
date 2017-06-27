package com.tsiro.dogvip.POJO.mypets;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 4/6/2017.
 */

public class OwnerRequest {

    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("authtoken")
    @Expose
    private String authtoken;
    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("id")
    @Expose
    private int id;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAuthtoken() { return authtoken; }

    public void setAuthtoken(String authtoken) { this.authtoken = authtoken; }

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }
}
