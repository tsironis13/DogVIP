package com.tsiro.dogvip.POJO.lovematch;

import com.tsiro.dogvip.POJO.BaseResponseObj;

/**
 * Created by giannis on 9/10/2017.
 */

public class LoveMatch extends BaseResponseObj {

    private GetPetsResponse petdata;
    private LikeDislikeResponse likeDislikeResponse;

    public GetPetsResponse getPetdata() {
        return petdata;
    }

    public void setPetdata(GetPetsResponse petdata) {
        this.petdata = petdata;
    }

    //    public GetPetsResponse getPetResponse() {
//        return getPetsResponse;
//    }
//
//    public LikeDislikeResponse getLikeDislikeResponse() {
//        return likeDislikeResponse;
//    }
}
