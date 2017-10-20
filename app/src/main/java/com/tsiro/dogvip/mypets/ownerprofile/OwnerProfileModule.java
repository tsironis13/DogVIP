package com.tsiro.dogvip.mypets.ownerprofile;

import android.app.Activity;

import com.tsiro.dogvip.base.activity.BaseActivityModule;
import com.tsiro.dogvip.lovematch.LoveMatchActivity;
import com.tsiro.dogvip.lovematch.LoveMatchActivityModule;

import dagger.Binds;
import dagger.Module;

/**
 * Created by giannis on 19/10/2017.
 */
@Module(includes = BaseActivityModule.class)
public abstract class OwnerProfileModule {

    @Binds
    abstract Activity provideActivity(OwnerProfileActivity ownerProfileActivity);

}
