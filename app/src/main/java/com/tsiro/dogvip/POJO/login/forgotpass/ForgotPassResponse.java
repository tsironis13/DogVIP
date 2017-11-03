package com.tsiro.dogvip.POJO.login.forgotpass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tsiro.dogvip.POJO.BaseResponseObj;

/**
 * Created by giannis on 1/11/2017.
 */

public class ForgotPassResponse extends BaseResponseObj {

    @SerializedName("user_id")
    @Expose
    private int userId;

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }
}
