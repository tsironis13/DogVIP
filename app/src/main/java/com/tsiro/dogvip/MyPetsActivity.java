package com.tsiro.dogvip;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jakewharton.rxbinding2.view.RxView;
import com.rey.material.widget.SnackBar;
import com.tsiro.dogvip.POJO.mypets.GetOwnerRequest;
import com.tsiro.dogvip.POJO.mypets.GetOwnerResponse;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityMypetsBinding;
import com.tsiro.dogvip.mypets.GetOwnerContract;
import com.tsiro.dogvip.mypets.GetOwnerViewModel;
import com.tsiro.dogvip.mypets.owner.OwnerFrgmt;
import com.tsiro.dogvip.requestmngrlayer.MyPetsRequestManager;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 4/6/2017.
 */

public class MyPetsActivity extends BaseActivity implements GetOwnerContract.View {

    private static final String debugTag = MyPetsActivity.class.getSimpleName();
    private GetOwnerContract.ViewModel mGetOwnerViewModel;
    private GetOwnerRequest mGetOwnerRequest;
    private ActivityMypetsBinding mBinding;
    private ProgressDialog mProgressDialog;
    private SnackBar mSnackBar;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_mypets);
        setSupportActionBar(mBinding.incltoolbar.toolbar);

        mSnackBar = mBinding.mypetsSnckBr;
        mGetOwnerViewModel = new GetOwnerViewModel(MyPetsRequestManager.getInstance());

        Intent intent = getIntent();
        if (intent != null) {
            mToken = intent.getStringExtra(getResources().getString(R.string.token));
            checkOwnerExists(mToken);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.retryBtn).subscribe(
                new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        checkOwnerExists(mToken);
                    }
                }
        );
        RxEventBus.add(this, disp);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mGetOwnerViewModel;
    }

    @Override
    public void onSuccess(final GetOwnerResponse response) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) dismissDialog();
        Log.e(debugTag, "onDismiss");
        if (mBinding.getIsVisible()) mBinding.setIsVisible(false);
        if (!response.isExists()) { //Owner does not exist
            if (getSupportFragmentManager() != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .replace(R.id.myPetsContainer, new OwnerFrgmt(), getResources().getString(R.string.owner_fgmt))
                        .commit();
            }
        } else { //Owner exists

        }
    }

    @Override
    public void onError(int code) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) dismissDialog();
        mBinding.setIsVisible(true);
        mBinding.setErrorText(getResources().getString(R.string.error));
    }

    private void checkOwnerExists(String token) {
        if (isNetworkAvailable()) {
            mProgressDialog = initializeProgressDialog(getResources().getString(R.string.please_wait));
            mGetOwnerRequest = new GetOwnerRequest();
            mGetOwnerRequest.setAction(getResources().getString(R.string.get_owner_details));
            mGetOwnerRequest.setAuthtoken(token);

            mGetOwnerViewModel.getOwnerDetails(mGetOwnerRequest);
        } else {
            mBinding.setIsVisible(true);
            mBinding.setErrorText(getResources().getString(R.string.no_internet_connection));
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

    public void showSnackBar(int style, String msg) {
        if (mSnackBar != null) {
            mSnackBar.applyStyle(style);
            mSnackBar.text(msg);
            mSnackBar.show();
        }
    }

    public void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }
}
