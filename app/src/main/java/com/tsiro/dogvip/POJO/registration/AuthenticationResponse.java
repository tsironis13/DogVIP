package com.tsiro.dogvip.POJO.registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 23/5/2017.
 */

public class AuthenticationResponse {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("regtype")
    @Expose
    private int regtype;
    @SerializedName("userid")
    @Expose
    private int userid;
    @SerializedName("authtoken")
    @Expose
    private String authtoken;
    @SerializedName("email")
    @Expose
    private String email;
    /*
     * used to display multi line or single line snackbar
     * depending the error msg string length
     * TRUE FOR BIG TEXT, FALSE FOR SMALL ONE
     */
    @SerializedName("stringlength")
    @Expose
    private boolean stringlength;
    @SerializedName("ownerexists")
    @Expose
    private boolean ownerexists;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getUserid() { return userid; }

    public void setUserid(int userid) { this.userid = userid; }

    public String getAuthtoken() { return authtoken; }

    public void setAuthtoken(String authtoken) { this.authtoken = authtoken; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public int getRegtype() { return regtype; }

    public void setRegtype(int regtype) { this.regtype = regtype; }

    public boolean isStringlength() { return stringlength; }

    public void setStringlength(boolean stringlength) { this.stringlength = stringlength; }

    public boolean isOwnerexists() { return ownerexists; }

    public void setOwnerexists(boolean ownerexists) { this.ownerexists = ownerexists; }
}
