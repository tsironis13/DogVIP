package com.tsiro.dogvip.POJO.lovematch;

import com.tsiro.dogvip.lovematch.viewmodel.LoveMatchViewModel;

/**
 * Created by giannis on 9/10/2017.
 */

public interface Command {

    void execute(LoveMatchViewModel loveMatchViewModel, LoveMatch loveMatch);

}
