package com.tsiro.dogvip.di.modules;

import android.app.Activity;
import android.content.Context;

import com.tsiro.dogvip.POJO.lovematch.GetPetsCommand;
import com.tsiro.dogvip.POJO.lovematch.ResponseController;
import com.tsiro.dogvip.di.scope.PerActivity;
import com.tsiro.dogvip.lovematch.LoveMatchActivity;
import com.tsiro.dogvip.lovematch.LoveMatchContract;
import com.tsiro.dogvip.requestmngrlayer.LoveMatchRequestManager;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * Created by giannis on 24/9/2017.
 */
@Module(includes = BaseActivityModule.class)
public abstract class LoveMatchActivityModule {

    private static final String debugTag = LoveMatchActivityModule.class.getSimpleName();

    @Binds
    abstract Activity provideActivity(LoveMatchActivity mainActivity);

//    @Provides
//    LoveMatchViewModel provideLoveMatchViewModel(LoveMatchRequestManager loveMatchRequestManager, ResponseController responseController, GetPetsCommand getPetsCommand) {
//        return new LoveMatchViewModel(loveMatchRequestManager, responseController, getPetsCommand);
//    }
}
