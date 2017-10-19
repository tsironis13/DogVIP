package com.tsiro.dogvip.POJO.lovematch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tsiro.dogvip.POJO.BaseRequestObj;

import javax.inject.Inject;

/**
 * Created by giannis on 2/7/2017.
 */

public class GetPetsByFilterRequest extends BaseRequestObj {

    @SerializedName("page")
    @Expose
    private int page;
    @SerializedName("hasfilters")
    @Expose
    private boolean hasfilters;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("race")
    @Expose
    private String race;

    @Inject
    public GetPetsByFilterRequest() {}

    public int getPage() { return page; }

    public void setPage(int page) { this.page = page; }

    public boolean isHasfilters() { return hasfilters; }

    public void setHasfilters(boolean hasfilters) { this.hasfilters = hasfilters; }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getRace() { return race; }

    public void setRace(String race) { this.race = race; }
}
