package com.tsiro.dogvip.login;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tsiro.dogvip.POJO.TestImage;
import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.accountmngr.MyAccountManager;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.base.activity.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.dashboard.DashboardActivity;
import com.tsiro.dogvip.databinding.ActivityLoginBinding;
import com.tsiro.dogvip.splashscreen.SplashFrgmt;
import com.tsiro.dogvip.utilities.UIUtls;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasFragmentInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class LoginActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    private static final String debugTag = LoginActivity.class.getSimpleName();
    private static boolean anmtionOnPrgrs;
    private static CompositeDisposable mCompDisp;
//    private GoogleApiClient mGoogleApiClient;
    private ActivityLoginBinding mBinding;
    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;
    @Inject
    GoogleApiClient mGoogleApiClient;
    @Inject
    MyAccountManager mAccountManager;
    @Inject
    UIUtls uiUtls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        Log.e(debugTag, uiUtls + " uiutls");

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        if (mAccountManager.checkAccountExists()) {
//            getMyAccountManager().getUserData(this);
            logUserIn(false);
        } else {
            if (savedInstanceState == null && getSupportFragmentManager() != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .replace(R.id.loginContainer, SplashFrgmt.newInstance(), getResources().getString(R.string.splash_fgmt))
                        .commit();
            }
        }
        Disposable disp = RxEventBus.createSubject(AppConfig.FRAGMENT_ANIMATION, AppConfig.PUBLISH_SUBJ).observeEvents(Boolean.class).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean animPrgrs) throws Exception {
                anmtionOnPrgrs = animPrgrs;
            }
        });
        RxEventBus.add(this, disp);
        /*
         * initialized here to avoid 'Already managing a GoogleApiClient with id 0' exceptions
         * disconnect and stop managing Google client in onPause()
         */
        mGoogleApiClient.connect();
    }

//    @Override
//    public Lifecycle.ViewModel getViewModel() {
//        return null;
//    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (anmtionOnPrgrs) return true;
            if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
                getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(getResources().getString(R.string.frgmnt_created), 1);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxEventBus.unregister(this);
        Log.e(debugTag, mGoogleApiClient + "onDestroy");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
//            mGoogleApiClient.stopAutoManage(this);

        }
        //prevent window leaks
//        dismissDialog();
    }

//    @Override
    public void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void addAccount(AuthenticationResponse response) {
        if (mAccountManager.addAccount(response.getEmail(), response.getAuthtoken())) {
            logUserIn(true);
        } else {
            showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.error));
        }
    }

    private void logUserIn(boolean userLoggedInFirstTime) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(getResources().getString(R.string.user_logged_in_first_time), userLoggedInFirstTime);
        startActivity(new Intent(LoginActivity.this, DashboardActivity.class).putExtras(bundle));
        finish();
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void showSnackBar(final int style, final String msg) {
        if (mBinding.snckBr != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBinding.snckBr.applyStyle(style);
                    mBinding.snckBr.text(msg);
                    mBinding.snckBr.show();
                }
            });
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }
}
