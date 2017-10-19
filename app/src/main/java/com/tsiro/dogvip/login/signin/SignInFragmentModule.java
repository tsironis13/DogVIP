package com.tsiro.dogvip.login.signin;

import android.app.Fragment;

import com.tsiro.dogvip.base.fragment.BaseFragmentModule;
import com.tsiro.dogvip.di.scope.PerFragment;

import dagger.Module;

/**
 * Created by giannis on 19/10/2017.
 */
@Module(includes = BaseFragmentModule.class)
public abstract class SignInFragmentModule {

    @PerFragment
    abstract Fragment fragment(SignInFrgmt signInFrgmt);

}
