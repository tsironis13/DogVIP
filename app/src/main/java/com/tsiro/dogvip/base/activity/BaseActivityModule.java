package com.tsiro.dogvip.base.activity;

import android.app.Activity;
import android.content.Context;

import com.tsiro.dogvip.di.scope.PerActivity;

import dagger.Binds;
import dagger.Module;

/**
 * Created by giannis on 15/10/2017.
 */
@Module
public abstract class BaseActivityModule {

    @Binds
    abstract Context provideActivityContext(Activity activity);

}
