package com.tsiro.dogvip.POJO.lovematch;

import com.tsiro.dogvip.lovematch.viewmodel.LoveMatchViewModel;

/**
 * Created by giannis on 9/10/2017.
 */

public class GetPetsCommand implements Command {

    @Override
    public void execute(LoveMatchViewModel loveMatchViewModel, LoveMatch loveMatch) {
        loveMatchViewModel.onKalase(loveMatch.getPetdata());
//        loveMatch.getPetResponse();
    }
}