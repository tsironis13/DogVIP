package com.tsiro.dogvip.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 16/7/2017.
 */

public class FcmTokenUpload {

    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("authtoken")
    @Expose
    private String authtoken;
    @SerializedName("deviceid")
    @Expose
    private String deviceid;
    @SerializedName("fcmtoken")
    @Expose
    private String fcmtoken;
    @SerializedName("code")
    @Expose
    private int code;

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getAuthtoken() { return authtoken; }

    public void setAuthtoken(String authtoken) { this.authtoken = authtoken; }

    public String getDeviceid() { return deviceid; }

    public void setDeviceid(String deviceid) { this.deviceid = deviceid; }

    public String getFcmtoken() { return fcmtoken; }

    public void setFcmtoken(String fcmtoken) { this.fcmtoken = fcmtoken; }

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }
}
