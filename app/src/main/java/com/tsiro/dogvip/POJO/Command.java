package com.tsiro.dogvip.POJO;

/**
 * Created by giannis on 9/10/2017.
 */

public interface Command {

    void executeOnSuccess(Response response);
    void executeOnError(int resource);

}
