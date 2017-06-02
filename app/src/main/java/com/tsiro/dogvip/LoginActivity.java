package com.tsiro.dogvip;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityLoginBinding;
import com.tsiro.dogvip.splashscreen.SplashFrgmt;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class LoginActivity extends AppCompatActivity implements Lifecycle.BaseView, GoogleApiClient.OnConnectionFailedListener {

    private static final String debugTag = LoginActivity.class.getSimpleName();
    private static boolean anmtionOnPrgrs;
    private static CompositeDisposable mCompDisp;
    private SnackBar mSnackBar;
    private ProgressDialog mProgressDialog;
    private MyAccountManager mAccountManager;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        mAccountManager = new MyAccountManager(this);
        if (mAccountManager.checkAccountExists()) {
            mAccountManager.getUserData(this);
        } else {
            mSnackBar = mBinding.loginSnckBr;

            if (getSupportFragmentManager() != null) {
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
        if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }

    @Override
    public void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void logUserIn(AuthenticationResponse response) {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
//                intent.putExtra("email", data.getEmail());
//                intent.putExtra("id", data.getUserid());
//                intent.putExtra("type", data.getRegtype());
        intent.putExtra("token", response.getAuthtoken());
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.no_internet_connection));
    }

    //check if network is available
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
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
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null) mProgressDialog.dismiss();
            }
        }, 500);
    }

    public void showSnackBar(int style, String msg) {
        if (mSnackBar != null) {
            mSnackBar.applyStyle(style);
            mSnackBar.text(msg);
            mSnackBar.show();
        }
    }

    public MyAccountManager getMyAccountManager() {
        return this.mAccountManager;
    }

}
