package com.tsiro.dogvip.mypets.owner;

import android.app.Activity;

import com.tsiro.dogvip.base.activity.BaseActivityModule;
import com.tsiro.dogvip.mypets.ownerprofile.OwnerProfileActivity;

import dagger.Binds;
import dagger.Module;

/**
 * Created by giannis on 19/10/2017.
 */
@Module(includes = BaseActivityModule.class)
public abstract class OwnerModule {

    @Binds
    abstract Activity provideActivity(OwnerActivity ownerActivity);

}
