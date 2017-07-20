package com.tsiro.dogvip;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.rey.material.widget.SnackBar;
import com.tsiro.dogvip.POJO.FcmTokenUpload;
import com.tsiro.dogvip.POJO.logout.LogoutRequest;
import com.tsiro.dogvip.POJO.logout.LogoutResponse;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityDashboardBinding;
import com.tsiro.dogvip.databinding.NavigationHeaderBinding;
import com.tsiro.dogvip.lovematch.LoveMatchActivity;
import com.tsiro.dogvip.mychatrooms.MyChatRoomsActivity;
import com.tsiro.dogvip.mypets.MyPetsActivity;
import com.tsiro.dogvip.retrofit.RetrofitFactory;
import com.tsiro.dogvip.retrofit.ServiceAPI;
import com.tsiro.dogvip.utilities.common.CommonUtls;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.util.concurrent.TimeUnit;

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

public class DashboardActivity extends BaseActivity {

    private static final String degbugTag = DashboardActivity.class.getSimpleName();
    private ActivityDashboardBinding mBinding;
    private ActionBarDrawerToggle mToggle;
    Disposable disp;
    private ServiceAPI serviceAPI;
    private String email, mToken;
    private int id, type;
    private SnackBar mSnackBar;
    private boolean logout, userLoggedInFirstTime;
    private ProgressDialog mProgressDialog;
    private Bundle bundle;
    private CommonUtls mCommonUtls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        mSnackBar = mBinding.snckBr;
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        mCommonUtls = new CommonUtls(this);

        if (getSupportActionBar()!= null){
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.person);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setUpNavigationView();

        if (savedInstanceState != null) {
            userLoggedInFirstTime = savedInstanceState.getBoolean(getResources().getString(R.string.user_logged_in_first_time));
        } else {
            if (getIntent() != null) {
                if (getIntent().getExtras() != null)
                userLoggedInFirstTime = getIntent().getExtras().getBoolean(getResources().getString(R.string.user_logged_in_first_time));
            }
        }
        serviceAPI = RetrofitFactory.getInstance().getServiceAPI();

        email = getMyAccountManager().getAccountDetails().getEmail();
        mToken = getMyAccountManager().getAccountDetails().getToken();
        bundle = new Bundle();

        NavigationHeaderBinding _bind = DataBindingUtil.inflate(getLayoutInflater(), R.layout.navigation_header, mBinding.navigationView, false);
        mBinding.navigationView.addHeaderView(_bind.getRoot());
        _bind.setUseremail(email);
        RxEventBus.createSubject(AppConfig.UPLOAD_FCM_TOKEN, AppConfig.PUBLISH_SUBJ).post(mToken);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.petsLlt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                Intent intent = new Intent(DashboardActivity.this, MyPetsActivity.class);
                bundle.putBoolean(getResources().getString(R.string.edit_ownr), false);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        RxEventBus.add(this, disp);
        Disposable disp1 = RxView.clicks(mBinding.loveLlt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                startActivity(new Intent(DashboardActivity.this, LoveMatchActivity.class));
            }
        });
        RxEventBus.add(this, disp1);
        Disposable disp2 = RxView.clicks(mBinding.sittersLlt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {

            }
        });
        RxEventBus.add(this, disp2);
        Disposable disp3 = RxView.clicks(mBinding.lostLlt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                startActivity(new Intent(DashboardActivity.this, LostFoundActivity.class));
            }
        });
        RxEventBus.add(this, disp3);
        Log.e(degbugTag, userLoggedInFirstTime +" FIRST TIME " + android.os.Build.SERIAL);
        String fcmToken = mCommonUtls.getSharedPrefs().getString(getResources().getString(R.string.fcmtoken), null);
        if (userLoggedInFirstTime) {
            if (fcmToken != null && isNetworkAvailable()) mCommonUtls.uploadTokenToServer(mToken, fcmToken);
        } else {
            if (!mCommonUtls.getSharedPrefs().getBoolean(getResources().getString(R.string.fcmtoken_uploaded), false)) {
                Log.e(degbugTag, "token not uploaded: " + fcmToken);
                if (fcmToken != null && isNetworkAvailable()) mCommonUtls.uploadTokenToServer(mToken, fcmToken);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(getResources().getString(R.string.user_logged_in_first_time), userLoggedInFirstTime);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        initializeMsgMenuItem(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.msg_item:
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeMsgMenuItem(Menu menu) {
        final View notificaitons = menu.findItem(R.id.msg_item).getActionView();
        notificaitons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, MyChatRoomsActivity.class));
            }
        });
    }

    private void setUpNavigationView() {
        mBinding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@android.support.annotation.NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        logout = true;
                        mBinding.drawerLlt.closeDrawers();
                        break;
                }
                return true;
            }
        });
        //user 5 parameters constructor to handle toolbar click(hamburger icon, etc)
        mToggle = new ActionBarDrawerToggle(this, mBinding.drawerLlt, mBinding.incltoolbar.toolbar, R.string.common_open_on_phone, R.string.app_logo) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (logout) {
                    logout = false;
                    logout();
                }
            }
        };
        //Setting the actionbarToggle to drawer layout
        mBinding.drawerLlt.addDrawerListener(mToggle);
        //calling sync state is necessary or else your hamburger icon wont show up
        mToggle.syncState();
    }

    private void logout() {
        if (isNetworkAvailable()) {
            LogoutRequest request = new LogoutRequest();
            request.setAction(getResources().getString(R.string.logout_user));
            request.setAuthtoken(mToken);
            request.setDeviceid(android.os.Build.SERIAL);

            initializeProgressDialog(getResources().getString(R.string.please_wait));
            serviceAPI.logout(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .delay(500, TimeUnit.MILLISECONDS)
                    .onErrorReturn(new Function<Throwable, LogoutResponse>() {
                        @Override
                        public LogoutResponse apply(@NonNull Throwable throwable) throws Exception {
                            return new LogoutResponse();
                        }
                    })
                    .doOnError(new Consumer<Throwable>() {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception {
                            dismissDialog();
                        }
                    })
                    .doOnNext(new Consumer<LogoutResponse>() {
                        @Override
                        public void accept(@NonNull LogoutResponse response) throws Exception {
                            dismissDialog();
                        }
                    }).subscribe(new Consumer<LogoutResponse>() {
                        @Override
                        public void accept(@NonNull LogoutResponse response) throws Exception {
                            getMyAccountManager().removeAccount();
                            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    });
        } else {
            showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.no_internet_connection));
        }
    }

    public void showSnackBar(final int style, final String msg) {
        if (mSnackBar != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSnackBar.applyStyle(style);
                    mSnackBar.text(msg);
                    mSnackBar.show();
                }
            });
        }
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return null;
    }

}
