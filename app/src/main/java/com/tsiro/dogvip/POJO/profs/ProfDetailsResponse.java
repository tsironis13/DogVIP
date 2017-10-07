package com.tsiro.dogvip.POJO.profs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tsiro.dogvip.POJO.BaseResponseObj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giannis on 30/9/2017.
 */

public class ProfDetailsResponse extends BaseResponseObj {

    @SerializedName("prof")
    @Expose
    private ProfObj prof;
    @SerializedName("data")
    @Expose
    private ArrayList<ProfObj> data;
    public ProfObj getProf() {
        return prof;
    }

    public void setProf(ProfObj prof) {
        this.prof = prof;
    }

    public ArrayList<ProfObj> getProfs() { return data; }

    public void setProfs(ArrayList<ProfObj> data) { this.data = data; }
}
