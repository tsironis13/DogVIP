package com.tsiro.dogvip.di.modules;

import android.content.Context;
import android.util.Log;

import com.tsiro.dogvip.di.qualifiers.ApplicationContext;
import com.tsiro.dogvip.lovematch.LoveMatchViewModel;
import com.tsiro.dogvip.networklayer.LoveMatchAPIService;
import com.tsiro.dogvip.requestmngrlayer.LoveMatchRequestManager;
import com.tsiro.dogvip.retrofit.ServiceAPI;

import dagger.Module;
import dagger.Provides;

/**
 * Created by giannis on 24/9/2017.
 */
@Module
public class LoveMatchActivityModule {

    private static final String debugTag = LoveMatchActivityModule.class.getSimpleName();

    @Provides
    LoveMatchViewModel provideLoveMatchViewModel(LoveMatchRequestManager loveMatchRequestManager, @ApplicationContext Context context) {
        Log.e(debugTag, "provideLoveMatchViewModel");
        return new LoveMatchViewModel(loveMatchRequestManager, context);
    }
}
