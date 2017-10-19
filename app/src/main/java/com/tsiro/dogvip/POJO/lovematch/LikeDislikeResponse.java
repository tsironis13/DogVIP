package com.tsiro.dogvip.POJO.lovematch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 9/10/2017.
 */

public class LikeDislikeResponse {

    @SerializedName("liked")
    @Expose
    private int liked;

    public int getLiked() { return liked; }

    public void setLiked(int liked) { this.liked = liked; }
}
