package com.tsiro.dogvip.POJO.petsitter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by thomatou on 9/19/17.
 */

public class BookingNotification {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("message")
    @Expose
    private String message;

}
