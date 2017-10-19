package com.tsiro.dogvip.profs;

import android.app.Activity;

import com.tsiro.dogvip.base.activity.BaseActivityModule;
import com.tsiro.dogvip.di.scope.PerActivity;
import com.tsiro.dogvip.login.LoginActivity;
import com.tsiro.dogvip.profs.prof.ProfActivity;

import dagger.Binds;
import dagger.Module;

/**
 * Created by giannis on 19/10/2017.
 */
@Module(includes = BaseActivityModule.class)
public abstract class ProfModule {

    @Binds
    @PerActivity
    abstract Activity activity(ProfActivity profActivity);

}
