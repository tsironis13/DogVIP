package com.tsiro.dogvip;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.POJO.logout.LogoutRequest;
import com.tsiro.dogvip.POJO.logout.LogoutResponse;
import com.tsiro.dogvip.accountmngr.MyAccountManager;
import com.tsiro.dogvip.databinding.ActivityDashboardBinding;
import com.tsiro.dogvip.retrofit.RetrofitFactory;
import com.tsiro.dogvip.retrofit.ServiceAPI;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by giannis on 27/5/2017.
 */

public class DashboardActivity extends AppCompatActivity {

    private static final String degbugTag = DashboardActivity.class.getSimpleName();
    ActivityDashboardBinding binding;
    Disposable disp;
    private ServiceAPI serviceAPI;
    String email;
    int id;
    String token;
    int type;
    MyAccountManager myAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);

        serviceAPI = RetrofitFactory.getInstance().getServiceAPI();
        Log.e("dashboard", getIntent().getStringExtra("token"));

        email = getIntent().getStringExtra("email");
        id = getIntent().getIntExtra("id", 0);
        token = getIntent().getStringExtra("token");
        type = getIntent().getIntExtra("type", 0);

        RxView.clicks(binding.logout).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                final LogoutRequest request = new LogoutRequest();
                request.setAction("logout_user");
                request.setAuthtoken(token);

                serviceAPI.logout(request)
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(@NonNull Disposable subscription) throws Exception {
                                Log.e(degbugTag, "onSub");
                            }
                        })
                        .doOnComplete(new Action() {
                            @Override
                            public void run() throws Exception {
                                Log.e(degbugTag, "onCompl");
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorReturn(new Function<Throwable, LogoutResponse>() {
                            @Override
                            public LogoutResponse apply(@NonNull Throwable throwable) throws Exception {
                                return new LogoutResponse();
                            }
                        })
                        .doOnError(new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                Log.e(degbugTag, "onError");
//                                registrationViewModel.setRequestState(AppConfig.REQUEST_FAILED);
                            }
                        })
                        .doOnNext(new Consumer<LogoutResponse>() {
                            @Override
                            public void accept(@NonNull LogoutResponse response) throws Exception {
                                Log.e(degbugTag, response+"");
//                                registrationViewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                            }
                        }).subscribe(new Consumer<LogoutResponse>() {
                            @Override
                            public void accept(@NonNull LogoutResponse response) throws Exception {
                                Log.e(degbugTag, response.getCode()+"");
                                myAccountManager = new MyAccountManager(DashboardActivity.this);
                                myAccountManager.removeAccount();
                                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                                finish();
                                startActivity(intent);
                            }
                });
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        disp.dispose();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        moveTaskToBack(true);
    }
}
