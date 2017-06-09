package com.tsiro.dogvip;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;
import com.rey.material.widget.SnackBar;
import com.tsiro.dogvip.POJO.logout.LogoutRequest;
import com.tsiro.dogvip.POJO.logout.LogoutResponse;
import com.tsiro.dogvip.accountmngr.MyAccountManager;
import com.tsiro.dogvip.databinding.ActivityDashboardBinding;
import com.tsiro.dogvip.databinding.NavigationHeaderBinding;
import com.tsiro.dogvip.retrofit.RetrofitFactory;
import com.tsiro.dogvip.retrofit.ServiceAPI;

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

public class DashboardActivity extends AppCompatActivity {

    private static final String degbugTag = DashboardActivity.class.getSimpleName();
    private ActivityDashboardBinding mBinding;
    private ActionBarDrawerToggle mToggle;
    Disposable disp;
    private ServiceAPI serviceAPI;
    private String email, mToken;
    private int id, type;
    private MyAccountManager myAccountManager;
    private SnackBar mSnackBar;
    private boolean logout;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        mSnackBar = mBinding.snckBr;
        setSupportActionBar(mBinding.incltoolbar.toolbar);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeAsUpIndicator(R.drawable.person);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        setUpNavigationView();

        serviceAPI = RetrofitFactory.getInstance().getServiceAPI();
//        Log.e("dashboard", getIntent().getStringExtra("token"));

        email = getIntent().getStringExtra(getResources().getString(R.string.email));

        NavigationHeaderBinding _bind = DataBindingUtil.inflate(getLayoutInflater(), R.layout.navigation_header, mBinding.navigationView, false);
        mBinding.navigationView.addHeaderView(_bind.getRoot());
        _bind.setUseremail(email);

        mToken = getIntent().getStringExtra(getResources().getString(R.string.token));

        RxView.clicks(mBinding.petsLlt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                Intent intent = new Intent(DashboardActivity.this, MyPetsActivity.class);
                intent.putExtra(getResources().getString(R.string.token), mToken);
                startActivity(intent);
            }
        });
        RxView.clicks(mBinding.loveLlt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {

            }
        });
        RxView.clicks(mBinding.sittersLlt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {

            }
        });


    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
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
                            Log.e(degbugTag, "onError");
                            dismissDialog();
                        }
                    })
                    .doOnNext(new Consumer<LogoutResponse>() {
                        @Override
                        public void accept(@NonNull LogoutResponse response) throws Exception {
                            Log.e(degbugTag, response+"");
                            dismissDialog();
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
        } else {
            showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.no_internet_connection));
        }
    }

    //check if network is available
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public ProgressDialog initializeProgressDialog(String msg) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        return mProgressDialog;
    }

    public void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.dismiss();
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

}
