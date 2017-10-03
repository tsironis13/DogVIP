package com.tsiro.dogvip.di.modules;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.tsiro.dogvip.di.qualifiers.ActivityContext;
import com.tsiro.dogvip.di.qualifiers.ApplicationContext;
import com.tsiro.dogvip.image_states.ImageUploadViewModel;
import com.tsiro.dogvip.mypets.owner.OwnerActivity;
import com.tsiro.dogvip.mypets.owner.OwnerViewModel;
import com.tsiro.dogvip.requestmngrlayer.MyPetsRequestManager;
import com.tsiro.dogvip.utilities.ImageUtls;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * Created by giannis on 2/10/2017.
 */
@Module
public abstract class OwnerActivityModule {

    private static final String debugTag = OwnerActivityModule.class.getSimpleName();
//    private Context mContext;
//
//    @Provides
//    @ActivityContext
//    abstract Activity bindActivity(OwnerActivity activity);



//    @Provides
//    @ActivityContext
//    Context provideContext(Context context) {
//        Log.e("aaa", "provide context");
//        return context;
//    }

//    @Provides
//    OwnerViewModel provideOwnerViewModel(MyPetsRequestManager myPetsRequestManager) {
//        return new OwnerViewModel(myPetsRequestManager);
//    }
//
//    @Provides
//    ImageUploadViewModel provideLoveMatchViewModel(MyPetsRequestManager myPetsRequestManager, ImageUtls imageUtls, Activity context) {
//        return new ImageUploadViewModel(myPetsRequestManager, imageUtls, context);
//    }

}
