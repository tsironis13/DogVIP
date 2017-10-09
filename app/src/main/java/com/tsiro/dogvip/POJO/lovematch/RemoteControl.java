package com.tsiro.dogvip.POJO.lovematch;

import android.util.Log;

import com.tsiro.dogvip.lovematch.viewmodel.LoveMatchViewModel;

/**
 * Created by giannis on 9/10/2017.
 */

public class RemoteControl {

    private Command command;

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public void pressButton(LoveMatchViewModel loveMatchViewModel, LoveMatch loveMatch) {
        Log.e("button pressed", "pressed");
        command.execute(loveMatchViewModel, loveMatch);
    }
}
