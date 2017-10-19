package com.tsiro.dogvip.lovematch;

import android.app.Activity;

import com.tsiro.dogvip.base.activity.BaseActivityModule;

import dagger.Binds;
import dagger.Module;

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
