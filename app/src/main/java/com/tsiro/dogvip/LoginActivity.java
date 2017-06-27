package com.tsiro.dogvip;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.rey.material.widget.SnackBar;
import com.rey.material.widget.TextView;
import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
import com.tsiro.dogvip.accountmngr.MyAccountManager;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityLoginBinding;
import com.tsiro.dogvip.splashscreen.SplashFrgmt;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class LoginActivity extends BaseActivity implements Lifecycle.BaseView, GoogleApiClient.OnConnectionFailedListener {

    private static final String debugTag = LoginActivity.class.getSimpleName();
    private static boolean anmtionOnPrgrs;
    private static CompositeDisposable mCompDisp;
    private GoogleApiClient mGoogleApiClient;
    private ActivityLoginBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        if (getMyAccountManager().checkAccountExists()) {
//            getMyAccountManager().getUserData(this);
            logUserIn();
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
//                Log.e("anim", animPrgrs+"");
                anmtionOnPrgrs = animPrgrs;
            }
        });
        RxEventBus.add(this, disp);
        /*
         * initialized here to avoid 'Already managing a GoogleApiClient with id 0' exceptions
         * disconnect and stop managing Google client in onPause()
         */
        initializeGoogleSignUp();
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return null;
    }

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
        if (mGoogleApiClient != null) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }
        //prevent window leaks
        dismissDialog();
    }

    @Override
    public void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public void logUserIn() {
        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.no_internet_connection));
    }

    private void initializeGoogleSignUp() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
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

}
