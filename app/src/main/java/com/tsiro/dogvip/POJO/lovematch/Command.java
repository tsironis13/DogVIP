package com.tsiro.dogvip.POJO.lovematch;

import com.tsiro.dogvip.POJO.BaseResponseObj;
import com.tsiro.dogvip.POJO.Response;
import com.tsiro.dogvip.lovematch.LoveMatchContract;

/**
 * Created by giannis on 9/10/2017.
 */

public interface Command {

    void executeOnSuccess(Response response);
    void executeOnError(int resource);

}
