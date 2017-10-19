package com.tsiro.dogvip.POJO.lovematch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tsiro.dogvip.POJO.BaseRequestObj;

import javax.inject.Inject;

/**
 * Created by giannis on 12/10/2017.
 */

public class LikeDislikeRequest extends BaseRequestObj {

    @SerializedName("subaction")
    @Expose
    private String subaction;
    @SerializedName("p_id")
    @Expose
    private int p_id;
    @SerializedName("liked")
    @Expose
    private int liked;

    @Inject
    public LikeDislikeRequest() {}

    public String getSubaction() { return subaction; }

    public void setSubaction(String subaction) { this.subaction = subaction; }

    public int getP_id() { return p_id; }

    public void setP_id(int p_id) { this.p_id = p_id; }

    public int getLiked() { return liked; }

    public void setLiked(int liked) { this.liked = liked; }
}
