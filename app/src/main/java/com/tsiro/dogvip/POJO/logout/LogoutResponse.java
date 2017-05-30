package com.tsiro.dogvip.POJO.logout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 27/5/2017.
 */

public class LogoutResponse {

    @SerializedName("code")
    @Expose
    private int code;

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }
}
