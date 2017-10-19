package com.tsiro.dogvip.POJO.lovematch;

import com.tsiro.dogvip.POJO.BaseResponseObj;
import com.tsiro.dogvip.POJO.Response;
import com.tsiro.dogvip.lovematch.LoveMatchActivity;
import com.tsiro.dogvip.lovematch.LoveMatchContract;

import javax.inject.Inject;

/**
 * Created by giannis on 9/10/2017.
 */

public class LikeDislikeCommand implements Command {

    private LoveMatchContract.View mViewCallback;

    @Inject
    public LikeDislikeCommand() {}

    public void setViewCallback(LoveMatchContract.View mViewCallback) {
        this.mViewCallback = mViewCallback;
    }

    @Override
    public void executeOnSuccess(Response response) {
        mViewCallback.onLikeDislikeSuccess(response.getLikeDislikeResponse());
    }

    @Override
    public void executeOnError(int code) {

    }
}
