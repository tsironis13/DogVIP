package com.tsiro.dogvip.login.signin;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.TextView;

import com.tsiro.dogvip.POJO.TestImage;
import com.tsiro.dogvip.base.fragment.BaseFragmentModule;
import com.tsiro.dogvip.di.scope.PerActivity;
import com.tsiro.dogvip.di.scope.PerFragment;
import com.tsiro.dogvip.login.LoginActivity;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.Multibinds;

/**
 * Created by giannis on 19/10/2017.
 */
@Module(includes = BaseFragmentModule.class)
public abstract class SignInFragmentModule {

    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(SignInFrgmt signInFrgmt);

}
