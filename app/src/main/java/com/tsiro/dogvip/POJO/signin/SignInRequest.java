package com.tsiro.dogvip.POJO.signin;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 28/5/2017.
 */

public class SignInRequest {

    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("deviceid")
    @Expose
    private String deviceid;
    @SerializedName("regtype")
    @Expose
    private int regtype;

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action;}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceid() { return deviceid; }

    public void setDeviceid(String deviceid) { this.deviceid = deviceid; }

    public int getRegstrType() {
        return regtype;
    }

    public void setRegstrType(int regtype) {
        this.regtype = regtype;
    }

}
