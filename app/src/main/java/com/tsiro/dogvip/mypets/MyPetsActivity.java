package com.tsiro.dogvip.mypets;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.POJO.mypets.OwnerRequest;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityMypetsBinding;
import com.tsiro.dogvip.mypets.owner.OwnerActivityOld;
//import com.tsiro.dogvip.mypets.owner.OwnerFrgmt;
import com.tsiro.dogvip.mypets.ownerprofile.OwnerProfileActivity;
import com.tsiro.dogvip.requestmngrlayer.MyPetsRequestManager;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 4/6/2017.
 */

public class MyPetsActivity extends BaseActivity implements GetOwnerContract.View {

    private GetOwnerContract.ViewModel mGetOwnerViewModel;
    private ActivityMypetsBinding mBinding;
    private ProgressDialog mProgressDialog;
    private String mToken;
    private boolean editOwner;
    private OwnerObj ownerObj;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_mypets);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        if (getSupportActionBar()!= null)getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGetOwnerViewModel = new GetOwnerViewModel(MyPetsRequestManager.getInstance());
        mToken = getMyAccountManager().getAccountDetails().getToken();
        if (savedInstanceState != null) {
            editOwner = savedInstanceState.getBoolean(getResources().getString(R.string.edit_ownr));
            configureActivity(savedInstanceState);
        } else {
            if (getIntent() != null) {
                editOwner = getIntent().getExtras().getBoolean(getResources().getString(R.string.edit_ownr));
                configureActivity(null);
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
    protected void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(getResources().getString(R.string.frgmnt_created), 1);
        outState.putBoolean(getResources().getString(R.string.edit_ownr), editOwner);
        outState.putParcelable(getResources().getString(R.string.parcelable_obj), ownerObj);
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
            bundle = new Bundle();
            bundle.putBoolean(getResources().getString(R.string.add_ownr), true);
            startActivity(new Intent(this, OwnerActivityOld.class).putExtras(bundle));
        } else { //Owner exists
            Intent intent = new Intent(this, OwnerProfileActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putParcelable(getResources().getString(R.string.parcelable_obj), response);
            startActivity(intent.putExtras(mBundle));
        }
    }

    @Override
    public void onError(int code) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) dismissDialog();
        mBinding.setIsVisible(true);
        mBinding.setErrorText(getResources().getString(R.string.error));
    }

    private void configureActivity(Bundle saveinstancestate) {
        if (editOwner) { //edit owner
            if (saveinstancestate != null) {
                ownerObj = saveinstancestate.getParcelable(getResources().getString(R.string.parcelable_obj));
            } else {
                ownerObj = getIntent().getExtras().getParcelable(getResources().getString(R.string.parcelable_obj));
            }
            bundle = new Bundle();
            bundle.putBoolean(getResources().getString(R.string.add_ownr), false);
            bundle.putParcelable(getResources().getString(R.string.parcelable_obj), ownerObj);
            startActivity(new Intent(this, OwnerActivityOld.class).putExtras(bundle));
        } else {
            checkOwnerExists(mToken);
        }
    }

    private void checkOwnerExists(String token) {
        if (isNetworkAvailable()) {
            mProgressDialog = initializeProgressDialog(getResources().getString(R.string.please_wait));
            OwnerRequest mGetOwnerRequest = new OwnerRequest();
            mGetOwnerRequest.setAction(getResources().getString(R.string.get_owner_details));
            mGetOwnerRequest.setAuthtoken(token);

            mGetOwnerViewModel.getOwnerDetails(mGetOwnerRequest);
        } else {
            mBinding.setIsVisible(true);
            mBinding.setErrorText(getResources().getString(R.string.no_internet_connection));
        }
    }

}
