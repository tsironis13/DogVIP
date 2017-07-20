package com.tsiro.dogvip.POJO.chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 17/7/2017.
 */

public class Message {

    @SerializedName("id")
    @Expose
    private int id;
    private transient int chat_room_id;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("user_role_id")
    @Expose
    private int user_role_id;
    @SerializedName("image_url")
    @Expose
    private String image_url;
    private transient boolean hide_image;

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getChat_room_id() { return chat_room_id; }

    public void setChat_room_id(int chat_room_id) { this.chat_room_id = chat_room_id; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getCreated_at() { return created_at; }

    public void setCreated_at(String created_at) { this.created_at = created_at; }

    public int getUser_role_id() { return user_role_id; }

    public void setUser_role_id(int user_role_id) { this.user_role_id = user_role_id; }

    public String getImage_url() { return image_url; }

    public void setImage_url(String image_url) { this.image_url = image_url; }

    public boolean isHide_image() { return hide_image; }

    public void setHide_image(boolean hide_image) { this.hide_image = hide_image; }
}
