package com.tsiro.dogvip.mypets;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;

import com.jakewharton.rxbinding2.view.RxView;
import com.rey.material.widget.SnackBar;
import com.tsiro.dogvip.POJO.mypets.OwnerRequest;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityMypetsBinding;
import com.tsiro.dogvip.mypets.owner.OwnerFrgmt;
import com.tsiro.dogvip.mypets.ownerprofile.OwnerProfileActivity;
import com.tsiro.dogvip.requestmngrlayer.MyPetsRequestManager;
import com.tsiro.dogvip.utilities.common.CommonUtls;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 4/6/2017.
 */

public class MyPetsActivity extends BaseActivity implements GetOwnerContract.View {

    private static final String debugTag = MyPetsActivity.class.getSimpleName();
    private GetOwnerContract.ViewModel mGetOwnerViewModel;
    private OwnerRequest mGetOwnerRequest;
    private ActivityMypetsBinding mBinding;
    private ProgressDialog mProgressDialog;
    private SnackBar mSnackBar;
    private String mToken, imageurl;
    private boolean imageuploading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_mypets);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        if (getSupportActionBar()!= null)getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSnackBar = mBinding.mypetsSnckBr;
        mGetOwnerViewModel = new GetOwnerViewModel(MyPetsRequestManager.getInstance());

        if (savedInstanceState != null) {
//            Log.e(debugTag, "jsd : "+ savedInstanceState.getString(getResources().getString(R.string.token)));
//            mToken = savedInstanceState.getString(getResources().getString(R.string.token));
        }

        if (getIntent() != null && savedInstanceState == null) {
            mToken = getMyAccountManager().getAccountDetails().getToken();
            if (getIntent().getExtras().getBoolean(getResources().getString(R.string.edit_ownr))) { //edit owner
                OwnerObj ownerObj = getIntent().getParcelableExtra(getResources().getString(R.string.parcelable_obj));
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .replace(R.id.myPetsContainer, OwnerFrgmt.newInstance(false, mToken, ownerObj), getResources().getString(R.string.owner_fgmt))
                        .commit();
            } else {
                checkOwnerExists(mToken);
            }
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (imageuploading) {
            showSnackBar(R.style.SnackBarMultiLine, getResources().getString(R.string.image_uploading_on_progress), "").subscribe();
        } else {
            if (getIntent().getExtras().getBoolean(getResources().getString(R.string.edit_ownr)) && imageurl != null) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(getResources().getString(R.string.imageurl), imageurl);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
            super.onBackPressed();
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
        outState.putInt(getResources().getString(R.string.frgmnt_created), 1);
//        outState.putString(getResources().getString(R.string.token), mToken);
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mGetOwnerViewModel;
    }

    @Override
    public void onSuccess(final OwnerObj response) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) dismissDialog();
        if (mBinding.getIsVisible()) mBinding.setIsVisible(false);
        if (!response.isExists()) { //Owner does not exist
            if (getSupportFragmentManager() != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .replace(R.id.myPetsContainer, OwnerFrgmt.newInstance(true, mToken, null), getResources().getString(R.string.owner_fgmt))
                        .commit();
            }
        } else { //Owner exists
            Intent intent = new Intent(this, OwnerProfileActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putParcelable(getResources().getString(R.string.parcelable_obj), response);
            intent.putExtras(mBundle);
            startActivity(intent);
        }
    }

    @Override
    public void onError(int code) {
        Log.e(debugTag, code+"");
        if (mProgressDialog != null && mProgressDialog.isShowing()) dismissDialog();
        mBinding.setIsVisible(true);
        mBinding.setErrorText(getResources().getString(R.string.error));
    }

    public void setImageProfileUrl(String url) {
        this.imageurl = url;
    }

    public String getImageurl() {
        return this.imageurl;
    }

    public void setImageuploading(boolean imageuploading) {
        this.imageuploading = imageuploading;
    }

    private void checkOwnerExists(String token) {
        Log.e(debugTag, token+"");
        if (isNetworkAvailable()) {
            mProgressDialog = initializeProgressDialog(getResources().getString(R.string.please_wait));
            mGetOwnerRequest = new OwnerRequest();
            mGetOwnerRequest.setAction(getResources().getString(R.string.get_owner_details));
            mGetOwnerRequest.setAuthtoken(token);

            mGetOwnerViewModel.getOwnerDetails(mGetOwnerRequest);
        } else {
            mBinding.setIsVisible(true);
            mBinding.setErrorText(getResources().getString(R.string.no_internet_connection));
        }
    }

    public io.reactivex.Observable<String> showSnackBar(final int style, final String msg, final String action) {
        return io.reactivex.Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<String> subscriber) throws Exception {
                if (mSnackBar != null) {
                    mSnackBar.applyStyle(style);
                    mSnackBar.text(msg);
                    mSnackBar.actionClickListener(new SnackBar.OnActionClickListener() {
                        @Override
                        public void onActionClick(SnackBar sb, int actionId) {
                            subscriber.onNext(action);
                        }
                    });
                    mSnackBar.show();
                }
            }
        });
    }

    public void dismissSnackBar() {
        if (mSnackBar != null && mSnackBar.isShown()) mSnackBar.dismiss();
    }

}
