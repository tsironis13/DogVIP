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
    @SerializedName("totalbookings")
    @Expose
    private int totalbookings;

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public int getTotalunread() { return totalunread; }

    public void setTotalunread(int totalunread) { this.totalunread = totalunread; }

    public int getTotalbookings() { return totalbookings; }

    public void setTotalbookings(int totalbookings) { this.totalbookings = totalbookings; }
}
