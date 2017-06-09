package com.tsiro.dogvip.POJO.forgotpasswrd;

import android.support.v4.app.ActivityCompat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.resetpasswrd.ForgotPaswrdContract;

import io.reactivex.annotations.NonNull;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 29/5/2017.
 */

public class ForgotPaswrdObj {

    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("subaction")
    @Expose
    private String subaction;
    @SerializedName("user_id")
    @Expose
    private int userId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("newpassword")
    @Expose
    private String newpassword;
    @SerializedName("conf_newpassword")
    @Expose
    private String confNewpassword;
    @SerializedName("code")
    @Expose
    private int code;
    /*
     * used to display multi line or single line snackbar
     * depending the error msg string length
     * TRUE FOR BIG TEXT, FALSE FOR SMALL ONE
     */
    @SerializedName("stringlength")
    @Expose
    private boolean stringlength;

    public String getAction() { return action; }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSubaction() {
        return subaction;
    }

    public void setSubaction(String subaction) {
        this.subaction = subaction;
    }

    public boolean isStringlength() {
        return stringlength;
    }

    public void setStringlength(boolean stringlength) {
        this.stringlength = stringlength;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    public String getConfNewpassword() {
        return confNewpassword;
    }

    public void setConfNewpassword(String confNewpassword) { this.confNewpassword = confNewpassword; }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
