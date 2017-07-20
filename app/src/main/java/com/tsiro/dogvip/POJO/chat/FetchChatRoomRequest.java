package com.tsiro.dogvip.POJO.chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 17/7/2017.
 */

public class FetchChatRoomRequest {

    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("authtoken")
    @Expose
    private String authtoken;
    @SerializedName("to_id")
    @Expose
    private int to_id;
    @SerializedName("from_id")
    @Expose
    private int from_id;
    @SerializedName("role")
    @Expose
    private int role;
    @SerializedName("pet_id")
    @Expose
    private int pet_id;
    @SerializedName("chatid")//use this field when we know chat room id
    @Expose
    private int chatid;

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getAuthtoken() { return authtoken; }

    public void setAuthtoken(String authtoken) { this.authtoken = authtoken; }

    public int getTo_id() { return to_id; }

    public void setTo_id(int to_id) { this.to_id = to_id; }

    public int getFrom_id() { return from_id; }

    public void setFrom_id(int from_id) { this.from_id = from_id; }

    public int getRole() { return role; }

    public void setRole(int role) { this.role = role; }

    public int getPet_id() { return pet_id; }

    public void setPet_id(int pet_id) { this.pet_id = pet_id; }

    public int getChatid() { return chatid; }

    public void setChatid(int chatid) { this.chatid = chatid; }
}
