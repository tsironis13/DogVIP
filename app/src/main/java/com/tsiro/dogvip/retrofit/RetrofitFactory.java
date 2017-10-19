package com.tsiro.dogvip.retrofit;

import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.MOck;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
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
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .addInterceptor(interceptor)
//                .addInterceptor(new MockInterceptor())
                .build();

        return new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

    }

    public static class MockInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {

            addDelay();

            return new Response.Builder()
                    .code(200)
                    .message("OK")
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_0)
                    .body(ResponseBody.create(MediaType.parse("application/json"), "{}"))
                    .build();
        }

        private void addDelay() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
