package com.tsiro.dogvip.di;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.di.qualifiers.ActivityContext;
import com.tsiro.dogvip.di.qualifiers.ApplicationContext;
import com.tsiro.dogvip.retrofit.ServiceAPI;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

import javax.inject.Scope;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.android.AndroidInjectionModule;
import io.reactivex.processors.AsyncProcessor;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by giannis on 24/9/2017.
 */
@Module(includes = AndroidInjectionModule.class)
public class AppModule {

    private static final String debugTag = AppModule.class.getSimpleName();

    @Provides
    @ApplicationContext
    Context provideContext(Application application) {
        return application;
    }

//    @Provides
//    Gson provideGson() {
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
//        return gsonBuilder.create();
//    }
    @Provides
    @Singleton
    HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(HttpLoggingInterceptor httpLoggingInterceptor) {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .addInterceptor(httpLoggingInterceptor)
//                .addInterceptor(new MOck())
                .build();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    ServiceAPI provideServiceAPI(Retrofit retrofit) {
        return retrofit.create(ServiceAPI.class);
    }

}
