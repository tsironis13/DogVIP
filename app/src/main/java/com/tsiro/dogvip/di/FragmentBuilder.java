package com.tsiro.dogvip.di;

import com.tsiro.dogvip.di.scope.PerActivity;
import com.tsiro.dogvip.di.scope.PerFragment;
import com.tsiro.dogvip.login.LoginActivity;
import com.tsiro.dogvip.login.LoginActivityModule;
import com.tsiro.dogvip.login.signin.SignInFragmentModule;
import com.tsiro.dogvip.login.signin.SignInFrgmt;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by giannis on 19/10/2017.
 */
@Module
abstract class FragmentBuilder {

    @PerFragment
    @ContributesAndroidInjector(modules = SignInFragmentModule.class)
    abstract SignInFrgmt bindSignInFrgmt();
}
