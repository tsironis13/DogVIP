package com.tsiro.dogvip.dashboard;

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
import com.tsiro.dogvip.LoginActivity;
import com.tsiro.dogvip.LostFoundActivity;
import com.tsiro.dogvip.POJO.chat.Message;
import com.tsiro.dogvip.POJO.chat.SendMessageRequest;
import com.tsiro.dogvip.POJO.dashboard.DashboardRequest;
import com.tsiro.dogvip.POJO.dashboard.DashboardResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityDashboardBinding;
import com.tsiro.dogvip.databinding.NavigationHeaderBinding;
import com.tsiro.dogvip.lovematch.LoveMatchActivity;
import com.tsiro.dogvip.mychatrooms.MyChatRoomsActivity;
import com.tsiro.dogvip.mypets.MyPetsActivity;
import com.tsiro.dogvip.petsitters.PendingSitterBookingsActivity;
import com.tsiro.dogvip.petsitters.PetSittersActivity;
import com.tsiro.dogvip.profs.ProfProfileActivity;
import com.tsiro.dogvip.requestmngrlayer.DashboardRequestManager;
import com.tsiro.dogvip.retrofit.RetrofitFactory;
import com.tsiro.dogvip.retrofit.ServiceAPI;
import com.tsiro.dogvip.utilities.common.CommonUtls;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by giannis on 27/5/2017.
 */

public class DashboardActivity extends BaseActivity implements DashboardContract.View{

    private ActivityDashboardBinding mBinding;
    private ActionBarDrawerToggle mToggle;
    Disposable disp;
    private ServiceAPI serviceAPI;
    private String email, mToken;
    private int id, type, totalUnreadMsgs, totalBookings;
    private SnackBar mSnackBar;
    private boolean logout, userLoggedInFirstTime;
    private Bundle bundle;
    private CommonUtls mCommonUtls;
    private DashboardContract.ViewModel mViewModel;
    private TextView unreadMsgsBadgeTtv, pendingBookingsBadgeTtv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        mSnackBar = mBinding.snckBr;
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        mCommonUtls = new CommonUtls(this);
        mViewModel = new DashboardViewModel(DashboardRequestManager.getInstance());

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
        getTotalUnreadMsgs();
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
        Disposable disp4 = RxEventBus.createSubject(AppConfig.PUBLISH_MSG_NOTFCTS, AppConfig.PUBLISH_SUBJ).observeEvents(Message.class).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Message>() {
            @Override
            public void accept(@NonNull Message message) throws Exception {
                if (unreadMsgsBadgeTtv.getVisibility() == View.GONE) unreadMsgsBadgeTtv.setVisibility(View.VISIBLE);
                totalUnreadMsgs++;
                unreadMsgsBadgeTtv.setText(String.valueOf(totalUnreadMsgs));
            }
        });
        RxEventBus.add(this, disp4);
        Disposable disp5 = RxView.clicks(mBinding.sittersLlt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                startActivity(new Intent(DashboardActivity.this, PetSittersActivity.class));
            }
        });
        RxEventBus.add(this, disp5);
        Disposable disp6 = RxView.clicks(mBinding.vetLlt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                sectionNotAvailableYet();
            }
        });
        RxEventBus.add(this, disp6);
        Disposable disp7 = RxView.clicks(mBinding.profsLlt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                startActivity(new Intent(DashboardActivity.this, ProfProfileActivity.class));
//                sectionNotAvailableYet();
            }
        });
        RxEventBus.add(this, disp7);
        Disposable disp8 = RxView.clicks(mBinding.offersLlt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                sectionNotAvailableYet();
            }
        });
        RxEventBus.add(this, disp8);
        Disposable disp9 = RxView.clicks(mBinding.moreLlt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                sectionNotAvailableYet();
            }
        });
        RxEventBus.add(this, disp9);
        String fcmToken = mCommonUtls.getSharedPrefs().getString(getResources().getString(R.string.fcmtoken), null);
        if (userLoggedInFirstTime) {
            if (fcmToken != null && isNetworkAvailable()) mCommonUtls.uploadTokenToServer(mToken, fcmToken);
        } else {
            if (!mCommonUtls.getSharedPrefs().getBoolean(getResources().getString(R.string.fcmtoken_uploaded), false)) {
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
        initializeSitterPendingBookingsMenuItem(menu);
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
        final View notifications = menu.findItem(R.id.msg_item).getActionView();
        unreadMsgsBadgeTtv = (TextView) notifications.findViewById(R.id.unreadMsgsBadgeTtv);
                notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, MyChatRoomsActivity.class));
            }
        });
    }

    private void initializeSitterPendingBookingsMenuItem(Menu menu) {
        final View notifications = menu.findItem(R.id.sitter_item).getActionView();
        pendingBookingsBadgeTtv = (TextView) notifications.findViewById(R.id.pendingBookingsBadgeTtv);
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, PendingSitterBookingsActivity.class));
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

    private void sectionNotAvailableYet() {
        showSnackBar(R.style.SnackBarMultiLine, getResources().getString(R.string.section_not_available_yet));
    }

    private void getTotalUnreadMsgs() {
        if (isNetworkAvailable()) {
            DashboardRequest request = new DashboardRequest();
            request.setAction(getResources().getString(R.string.get_user_total_unread_msgs_and_pending_booking));
            request.setAuthtoken(mToken);
            mViewModel.getTotelUnreadMsgs(request);
        }
    }

    private void logout() {
        if (isNetworkAvailable()) {
            DashboardRequest request = new DashboardRequest();
            request.setAction(getResources().getString(R.string.logout_user));
            request.setAuthtoken(mToken);
            request.setDeviceid(android.os.Build.SERIAL);

            initializeProgressDialog(getResources().getString(R.string.please_wait));
            mViewModel.logoutUser(request);
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
        return mViewModel;
    }

    @Override
    public void onSuccess(DashboardResponse response) {
        dismissDialog();
        if (response.getAction().equals(getResources().getString(R.string.logout_user))) {
            getMyAccountManager().removeAccount();
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            finish();
            startActivity(intent);
        } else {
            if (response.getTotalunread() != 0) {
                totalUnreadMsgs = response.getTotalunread();
                unreadMsgsBadgeTtv.setVisibility(View.VISIBLE);
                unreadMsgsBadgeTtv.setText(String.valueOf(response.getTotalunread()));
            }
            if (response.getTotalbookings() != 0) {
                totalBookings = response.getTotalbookings();
                pendingBookingsBadgeTtv.setVisibility(View.VISIBLE);
                pendingBookingsBadgeTtv.setText(String.valueOf(response.getTotalbookings()));
            }
        }
    }

    @Override
    public void onError(int resource) {
        dismissDialog();
        showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.error));
    }
}
