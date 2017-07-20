package com.tsiro.dogvip.POJO.chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by giannis on 17/7/2017.
 */

public class FetchChatRoomResponse {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("data")
    @Expose
    private ArrayList<Message> data;
    @SerializedName("my_user_id") //also from_id
    @Expose
    private int my_user_id;
    @SerializedName("chat_room_id")
    @Expose
    private int chat_room_id;

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public ArrayList<Message> getData() { return data; }

    public void setData(ArrayList<Message> data) { this.data = data; }

    public int getMy_user_id() { return my_user_id; }

    public void setMy_user_id(int my_user_id) { this.my_user_id = my_user_id; }

    public int getChat_room_id() { return chat_room_id; }

    public void setChat_room_id(int chat_room_id) { this.chat_room_id = chat_room_id; }
}
