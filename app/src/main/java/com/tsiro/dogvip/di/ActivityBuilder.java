package com.tsiro.dogvip.di;

import com.tsiro.dogvip.di.modules.LoveMatchActivityModule;
import com.tsiro.dogvip.lovematch.LoveMatchActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by giannis on 24/9/2017.
 */
@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = {LoveMatchActivityModule.class})
    abstract LoveMatchActivity bindLoveMatchActivity();

}
