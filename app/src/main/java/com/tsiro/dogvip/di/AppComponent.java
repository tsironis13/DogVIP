package com.tsiro.dogvip.di;

import android.app.Application;
import com.tsiro.dogvip.app.MyApplication;
import javax.inject.Singleton;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

/**
 * Created by giannis on 24/9/2017.
 */
@Singleton
@Component(modules = {AppModule.class, ActivityBuilder.class})
public interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance Builder application(Application application);
        AppComponent build();
    }

    void inject(MyApplication application);
}
