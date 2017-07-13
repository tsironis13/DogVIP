package com.tsiro.dogvip.POJO.lostfound;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 10/7/2017.
 */

public class LostFoundRequest {

    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("subaction")
    @Expose
    private String subaction;
    @SerializedName("authtoken")
    @Expose
    private String authtoken;
    @SerializedName("entry_id")
    @Expose
    private int entry_id;

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getSubaction() { return subaction; }

    public void setSubaction(String subaction) { this.subaction = subaction; }

    public String getAuthtoken() { return authtoken; }

    public void setAuthtoken(String authtoken) { this.authtoken = authtoken; }

    public int getEntry_id() { return entry_id; }

    public void setEntry_id(int entry_id) { this.entry_id = entry_id; }
}
