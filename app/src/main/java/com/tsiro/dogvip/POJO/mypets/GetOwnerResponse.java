package com.tsiro.dogvip.POJO.mypets;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 4/6/2017.
 */

public class GetOwnerResponse {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("exists")
    @Expose
    private boolean exists;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isExists() { return exists; }

    public void setExists(boolean exists) { this.exists = exists; }
}
