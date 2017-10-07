package com.tsiro.dogvip.POJO.profs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by giannis on 6/10/2017.
 */

public class SearchProfsRequest {

    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("authtoken")
    @Expose
    private String authtoken;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("categories")
    @Expose
    private List<Integer> categories;

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getAuthtoken() { return authtoken; }

    public void setAuthtoken(String authtoken) { this.authtoken = authtoken; }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public List<Integer> getCategories() { return categories; }

    public void setCategories(List<Integer> categories) { this.categories = categories; }
}
