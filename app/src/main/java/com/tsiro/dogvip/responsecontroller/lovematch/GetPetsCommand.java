package com.tsiro.dogvip.responsecontroller.lovematch;

import android.util.Log;

import com.tsiro.dogvip.POJO.BaseResponseObj;
import com.tsiro.dogvip.POJO.Response;
import com.tsiro.dogvip.di.scope.PerActivity;
import com.tsiro.dogvip.lovematch.LoveMatchActivity;
import com.tsiro.dogvip.lovematch.LoveMatchContract;
import com.tsiro.dogvip.responsecontroller.Command;

import javax.inject.Inject;

/**
 * Created by giannis on 9/10/2017.
 */

public class GetPetsCommand implements Command {

    private LoveMatchContract.View mViewCallback;

    @Inject
    public GetPetsCommand() {}

    public void setViewCallback(LoveMatchContract.View mViewCallback) {
        this.mViewCallback = mViewCallback;
    }

    @Override
    public void executeOnSuccess(Response response) {
//        Log.e("executeOnSuccess", "EXECUTE ONS SUCCESS: "+loveMatchActivity);
        mViewCallback.onPetDataSuccess(response.getPetdata());
    }

    @Override
    public void executeOnError(int resource) {
        mViewCallback.onError(resource);
    }

    //Implementor public interface TV{public void on();public void off(); public void tuneChannel(int channel);}
    //Concrete Implementor public class Sony implements TV{public void on(){//Sony specific on}public void off(){//Sony specific off}
    // public void tuneChannel(int channel);{//Sony specific tuneChannel}}//Concrete Implementor public class Philips implements TV{public void on(){//Philips specific on}public void off(){//Philips specific off}public void tuneChannel(int channel);{//Philips specific tuneChannel}}

    //Abstractionpublic abstract class RemoteControl{   private TV implementor;          public void on()   {      implementor.on();   }   public void off()   {      implementor.off();   }
    //    public void setChannel(int channel)   {   implementor.tuneChannel(channel);   }}

    //Refined abstractionpublic class ConcreteRemote extends RemoteControl{   private int currentChannel;       public void nextChannel()   {       currentChannel++;
    // setChannel(currentChannel);   }      public void prevChannel()   {       currentChannel--;   setChannel(currentChannel);   }      }

}
