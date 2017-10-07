package com.tsiro.dogvip.POJO.profs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by giannis on 4/10/2017.
 */

public class SaveProfDetailsRequest {

    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("authtoken")
    @Expose
    private String authtoken;
    @SerializedName("obj")
    @Expose
    private ProfObj obj;

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getAuthtoken() { return authtoken; }

    public void setAuthtoken(String authtoken) { this.authtoken = authtoken; }

    public ProfObj getData() { return obj; }

    public void setData(ProfObj obj) { this.obj = obj; }

}
