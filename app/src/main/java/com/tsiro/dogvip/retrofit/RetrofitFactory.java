package com.tsiro.dogvip.retrofit;

import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.MOck;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;
//import okhttp3.logging.HttpLoggingInterceptor;
//import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by giannis on 23/5/2017.
 */

public class RetrofitFactory {

    private ServiceAPI serviceAPI;
    private static RetrofitFactory retrofitFactory;

    private RetrofitFactory() {
        serviceAPI = getAdapter().create(ServiceAPI.class);
    }

    public static RetrofitFactory getInstance() {
        if (retrofitFactory == null) retrofitFactory = new RetrofitFactory();
        return retrofitFactory;
    }

    public ServiceAPI getServiceAPI() { return  serviceAPI; }

    private static Retrofit getAdapter() {
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
//                .addInterceptor(interceptor)
//                .addInterceptor(new MOck())
                .build();

        return new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

    }

}
