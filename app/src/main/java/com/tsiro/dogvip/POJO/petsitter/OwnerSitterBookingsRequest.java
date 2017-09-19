package com.tsiro.dogvip.POJO.petsitter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 14/9/2017.
 */

public class OwnerSitterBookingsRequest {

    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("authtoken")
    @Expose
    private String authtoken;
    @SerializedName("id")
    @Expose
    private int id;

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getAuthtoken() { return authtoken; }

    public void setAuthtoken(String authtoken) { this.authtoken = authtoken; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }
}
