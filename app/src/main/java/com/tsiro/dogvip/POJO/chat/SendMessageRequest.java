package com.tsiro.dogvip.POJO.chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 17/7/2017.
 */

public class SendMessageRequest {

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
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("chat_room_id")
    @Expose
    private int chat_room_id;
    @SerializedName("role")
    @Expose
    private int role;
    @SerializedName("pet_id")
    @Expose
    private int pet_id;
    @SerializedName("pet_name")
    @Expose
    private String pet_name;

    public String getAction() { return action; }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public int getTo_id() {
        return to_id;
    }

    public void setTo_id(int to_id) {
        this.to_id = to_id;
    }

    public int getFrom_id() { return from_id; }

    public void setFrom_id(int from_id) { this.from_id = from_id; }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getChat_room_id() {
        return chat_room_id;
    }

    public void setChat_room_id(int chat_room_id) {
        this.chat_room_id = chat_room_id;
    }

    public int getRole() { return role; }

    public void setRole(int role) { this.role = role; }

    public int getPet_id() { return pet_id; }

    public void setPet_id(int pet_id) { this.pet_id = pet_id; }

    public String getPet_name() { return pet_name; }

    public void setPet_name(String pet_name) { this.pet_name = pet_name; }
}
