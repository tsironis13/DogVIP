package com.tsiro.dogvip.POJO.chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by giannis on 19/7/2017.
 */

public class FetchChatRoomsResponse {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("data")
    @Expose
    private ArrayList<ChatRoom> data;

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public ArrayList<ChatRoom> getData() { return data; }

    public void setData(ArrayList<ChatRoom> data) { this.data = data; }
}
