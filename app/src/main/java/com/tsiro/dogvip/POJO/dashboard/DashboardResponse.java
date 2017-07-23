package com.tsiro.dogvip.POJO.dashboard;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 27/5/2017.
 */

public class DashboardResponse {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("totalunread")
    @Expose
    private int totalunread;

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public int getTotalunread() { return totalunread; }

    public void setTotalunread(int totalunread) { this.totalunread = totalunread; }
}
