package com.tsiro.dogvip.di;

import com.tsiro.dogvip.login.LoginActivityModule;
import com.tsiro.dogvip.login.signin.SignInFragmentModule;
import com.tsiro.dogvip.lovematch.LoveMatchActivityModule;
import com.tsiro.dogvip.di.scope.PerActivity;
import com.tsiro.dogvip.login.LoginActivity;
import com.tsiro.dogvip.lovematch.LoveMatchActivity;
import com.tsiro.dogvip.profs.ProfModule;
import com.tsiro.dogvip.profs.prof.ProfActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by giannis on 24/9/2017.
 */
@Module
abstract class ActivityBuilder {

    @PerActivity
    @ContributesAndroidInjector(modules = {LoginActivityModule.class})
    abstract LoginActivity bindLoginActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = {LoveMatchActivityModule.class})
    abstract LoveMatchActivity bindLoveMatchActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = {ProfModule.class})
    abstract ProfActivity bindProfActivity();
}
