package com.tsiro.dogvip.POJO.chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 19/7/2017.
 */

public class ChatRoom {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("from_id")
    @Expose
    private int from_id;
    @SerializedName("to_id")
    @Expose
    private int to_id;
    @SerializedName("pet_id")
    @Expose
    private int pet_id;
    @SerializedName("name") //other user name
    @Expose
    private String name;
    @SerializedName("surname") //other user surname
    @Expose
    private String surname;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("image_url") //other user image
    @Expose
    private String image_url;
    @SerializedName("p_name")
    @Expose
    private String p_name;
    @SerializedName("main_image")
    @Expose
    private String main_image; //pet image
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("total") //total unread
    @Expose
    private int total;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFrom_id() {
        return from_id;
    }

    public void setFrom_id(int from_id) {
        this.from_id = from_id;
    }

    public int getTo_id() {
        return to_id;
    }

    public void setTo_id(int to_id) {
        this.to_id = to_id;
    }

    public int getPet_id() {
        return pet_id;
    }

    public void setPet_id(int pet_id) {
        this.pet_id = pet_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPet_name() { return p_name; }

    public void setPet_name(String p_name) { this.p_name = p_name; }

    public String getSurname() { return surname; }

    public void setSurname(String surname) { this.surname = surname; }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getPet_image() {
        return main_image;
    }

    public void setPet_image(String main_image) {
        this.main_image = main_image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getTotal() { return total; }

    public void setTotal(int total) { this.total = total; }
}
