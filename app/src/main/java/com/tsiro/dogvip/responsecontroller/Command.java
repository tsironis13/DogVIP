package com.tsiro.dogvip.responsecontroller;

import com.tsiro.dogvip.POJO.Response;

/**
 * Created by giannis on 9/10/2017.
 */

public interface Command {

    void executeOnSuccess(Response response);
    void executeOnError(int resource);

}
