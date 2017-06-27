package com.tsiro.dogvip.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 27/6/2017.
 */

public class ImagePathIndex {

    @SerializedName("index")
    @Expose
    private int index;
    @SerializedName("path")
    @Expose
    private String path;

    public int getIndex() { return index; }

    public void setIndex(int index) { this.index = index; }

    public String getPath() { return path; }

    public void setPath(String path) { this.path = path; }
}
