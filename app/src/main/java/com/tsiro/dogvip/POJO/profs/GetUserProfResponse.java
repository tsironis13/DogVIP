package com.tsiro.dogvip.POJO.profs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by giannis on 30/9/2017.
 */

public class GetUserProfResponse {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("prof")
    @Expose
    private ProfObj prof;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ProfObj getProf() {
        return prof;
    }

    public void setProf(ProfObj prof) {
        this.prof = prof;
    }
}
