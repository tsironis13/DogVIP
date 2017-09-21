package com.tsiro.dogvip.POJO.petsitter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 21/9/2017.
 */

public class RateBookingRequest {

    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("authtoken")
    @Expose
    private String authtoken;
    @SerializedName("id")//booking id
    @Expose
    private int id;
    @SerializedName("sitter_id")
    @Expose
    private int sitter_id;
    @SerializedName("owner_id")
    @Expose
    private int owner_id;
    @SerializedName("rating")
    @Expose
    private float rating;
    @SerializedName("comment")
    @Expose
    private String comment;

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getAuthtoken() { return authtoken; }

    public void setAuthtoken(String authtoken) { this.authtoken = authtoken; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getSitter_id() { return sitter_id; }

    public void setSitter_id(int sitter_id) { this.sitter_id = sitter_id; }

    public int getOwner_id() { return owner_id; }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public float getRating() { return rating; }

    public void setRating(float rating) { this.rating = rating; }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }
}
