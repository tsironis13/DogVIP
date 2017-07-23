package com.tsiro.dogvip.lostfound;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.LostFoundActivity;
import com.tsiro.dogvip.POJO.lostfound.LostFoundObj;
import com.tsiro.dogvip.POJO.lostfound.LostFoundRequest;
import com.tsiro.dogvip.POJO.lostfound.LostFoundResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.LostFoundViewPager;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityLostBinding;
import com.tsiro.dogvip.requestmngrlayer.LostFoundRequestManager;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 10/7/2017.
 */

public class LostActivity extends BaseActivity implements LostFoundContract.View {

    private static final String debugTag = LostActivity.class.getSimpleName();
    private ActivityLostBinding mBinding;
    private String mToken, subaction;
    private LostFoundContract.ViewModel mViewModel;
    private int type;//0 lost, 1 found

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_lost);
        mToken = getMyAccountManager().getAccountDetails().getToken();
        mViewModel = new LostFoundViewModel(LostFoundRequestManager.getInstance());
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        if (savedInstanceState != null) {
            type = savedInstanceState.getInt(getResources().getString(R.string.type));
        } else {
            if (getIntent() != null) {
                type = getIntent().getExtras().getInt(getResources().getString(R.string.type));
            }
        }
        if (getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (type == 0) {
                setTitle(getResources().getString(R.string.lost_title));
                subaction = getResources().getString(R.string.lost_subaction);
            } else {
                setTitle(getResources().getString(R.string.found_title));
                subaction = getResources().getString(R.string.found_subaction);
            }
        }
        fetchData(mToken);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.retryBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                fetchData(mToken);
            }
        });
        RxEventBus.add(this, disp);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(getResources().getString(R.string.type), type);
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LostFoundActivity.class));
        super.onBackPressed();
    }

    @Override
    public void onSuccess(LostFoundResponse response) {
        dismissDialog();
        mBinding.setHaserror(false);
        LostFoundViewPager viewPager = new LostFoundViewPager(getSupportFragmentManager(), getResources().getStringArray(R.array.lost_found_tabs), type, response);
        mBinding.viewPgr.enablePaging(false);
        mBinding.viewPgr.setAdapter(viewPager);
        mBinding.tabs.setupWithViewPager(mBinding.viewPgr);
    }

    @Override
    public void onError(int resource) {
        dismissDialog();
        mBinding.setHaserror(true);
        mBinding.setErrortext(getResources().getString(resource));
    }

    @Override
    public void onBaseViewClick(LostFoundObj lostFoundObj, int type) {
        if (lostFoundObj != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(getResources().getString(R.string.parcelable_obj), lostFoundObj);
            bundle.putInt(getResources().getString(R.string.type), type);
            startActivity(new Intent(this, LostFoundDetailsActivity.class).putExtras(bundle));
        }
    }

    public String getmToken() {
        return mToken;
    }

    private void fetchData(String token) {
        if (isNetworkAvailable()) {
            initializeProgressDialog(getResources().getString(R.string.please_wait));
            LostFoundRequest request = new LostFoundRequest();
            request.setAction(getResources().getString(R.string.get_lost_found_entries));
            request.setSubaction(subaction);
            request.setAuthtoken(token);
            mViewModel.getLostPets(request);
        } else {
            mBinding.setHaserror(true);
            mBinding.setErrortext(getResources().getString(R.string.no_internet_connection));
        }
    }
}
