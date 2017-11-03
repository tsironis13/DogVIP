package com.tsiro.dogvip.POJO.login.forgotpass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tsiro.dogvip.POJO.BaseRequestObj;

import javax.inject.Inject;

/**
 * Created by giannis on 1/11/2017.
 */

public class ForgotPassRequest extends BaseRequestObj {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("newpassword")
    @Expose
    private String newpassword;
    @SerializedName("conf_newpassword")
    @Expose
    private String confNewpassword;
    @SerializedName("user_id")
    @Expose
    private int userId;

    @Inject
    public ForgotPassRequest() {}

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getNewpassword() { return newpassword; }

    public void setNewpassword(String newpassword) { this.newpassword = newpassword; }

    public String getConfNewpassword() { return confNewpassword; }

    public void setConfNewpassword(String confNewpassword) { this.confNewpassword = confNewpassword; }

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }
}
