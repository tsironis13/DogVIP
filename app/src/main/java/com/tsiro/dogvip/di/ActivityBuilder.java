package com.tsiro.dogvip.di;

import com.tsiro.dogvip.di.modules.LoveMatchActivityModule;
import com.tsiro.dogvip.di.modules.OwnerActivityModule;
import com.tsiro.dogvip.di.qualifiers.ActivityContext;
import com.tsiro.dogvip.lovematch.LoveMatchActivity;
import com.tsiro.dogvip.mypets.owner.OwnerActivity;

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

    @ContributesAndroidInjector(modules = {OwnerActivityModule.class})
    abstract OwnerActivity bindOwnerActivity();

}
