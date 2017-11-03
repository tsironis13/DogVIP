package com.tsiro.dogvip.POJO;

import com.tsiro.dogvip.POJO.login.LoginResponse;
import com.tsiro.dogvip.POJO.login.forgotpass.ForgotPassResponse;
import com.tsiro.dogvip.POJO.lovematch.GetPetsResponse;
import com.tsiro.dogvip.POJO.lovematch.LikeDislikeResponse;

/**
 * Created by giannis on 16/10/2017.
 */

public class Response extends BaseResponseObj {

    private LoginResponse login;
    private ForgotPassResponse forgotPass;
    private GetPetsResponse petdata;
    private LikeDislikeResponse likedata;

    public LoginResponse getLogin() { return login; }

    public void setLogin(LoginResponse login) { this.login = login; }

    public ForgotPassResponse getForgotPass() { return forgotPass; }

    public void setForgotPass(ForgotPassResponse forgotPass) { this.forgotPass = forgotPass; }

    public GetPetsResponse getPetdata() {
        return petdata;
    }

    public void setPetdata(GetPetsResponse petdata) {
        this.petdata = petdata;
    }

    public LikeDislikeResponse getLikeDislikeResponse() { return likedata; }

    public void setLikeDislikeResponse(LikeDislikeResponse likedata) { this.likedata = likedata; }

}
