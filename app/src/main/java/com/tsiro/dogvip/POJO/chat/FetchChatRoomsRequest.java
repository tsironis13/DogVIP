package com.tsiro.dogvip.POJO.chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 19/7/2017.
 */

public class FetchChatRoomsRequest {

    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("authtoken")
    @Expose
    private String authtoken;

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getAuthtoken() { return authtoken; }

    public void setAuthtoken(String authtoken) { this.authtoken = authtoken; }
}
