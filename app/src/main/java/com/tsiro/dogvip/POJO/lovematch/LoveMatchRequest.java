package com.tsiro.dogvip.POJO.lovematch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 2/7/2017.
 */

public class LoveMatchRequest {

    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("subaction")
    @Expose
    private String subaction;
    @SerializedName("authtoken")
    @Expose
    private String authtoken;
    @SerializedName("page")
    @Expose
    private int page;
    @SerializedName("p_id")
    @Expose
    private int p_id;
    @SerializedName("hasfilters")
    @Expose
    private boolean hasfilters;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("race")
    @Expose
    private String race;

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getSubaction() { return subaction; }

    public void setSubaction(String subaction) { this.subaction = subaction; }

    public String getAuthtoken() { return authtoken; }

    public void setAuthtoken(String authtoken) { this.authtoken = authtoken; }

    public int getPage() { return page; }

    public void setPage(int page) { this.page = page; }

    public int getP_id() { return p_id; }

    public void setP_id(int p_id) { this.p_id = p_id; }

    public boolean isHasfilters() { return hasfilters; }

    public void setHasfilters(boolean hasfilters) { this.hasfilters = hasfilters; }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getRace() { return race; }

    public void setRace(String race) { this.race = race; }
}
