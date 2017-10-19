package com.tsiro.dogvip.login;

import android.app.Activity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tsiro.dogvip.POJO.TestImage;
import com.tsiro.dogvip.base.activity.BaseActivityModule;
import com.tsiro.dogvip.di.scope.PerActivity;
import com.tsiro.dogvip.login.signin.SignInFrgmt;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by giannis on 19/10/2017.
 */
@Module(includes = BaseActivityModule.class)
public abstract class LoginActivityModule {

    @Binds
    @PerActivity
    abstract Activity activity(LoginActivity loginActivity);

    @Provides
    static GoogleSignInOptions provideGoogleSignInOptions() {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
    }

    @Provides
    static GoogleApiClient provideGoogleApiClient(GoogleSignInOptions googleSignInOptions, LoginActivity loginActivity) {
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        return new GoogleApiClient.Builder(loginActivity)
//                .enableAutoManage(loginActivity /* FragmentActivity */, loginActivity /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }
}