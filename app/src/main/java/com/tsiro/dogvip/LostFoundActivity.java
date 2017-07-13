package com.tsiro.dogvip;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityLostFoundBinding;
import com.tsiro.dogvip.lostfound.LostActivity;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 10/7/2017.
 */

public class LostFoundActivity extends AppCompatActivity {

    private ActivityLostFoundBinding mBinding;
    private Bundle bundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_lost_found);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        if (getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getResources().getString(R.string.lostfound_title));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bundle = new Bundle();
        Disposable disp = RxView.clicks(mBinding.lostLlt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                bundle.putInt(getResources().getString(R.string.type), 0);
                startActivity(new Intent(LostFoundActivity.this, LostActivity.class).putExtras(bundle));
            }
        });
        RxEventBus.add(this, disp);
        Disposable disp1 = RxView.clicks(mBinding.foundLlt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                bundle.putInt(getResources().getString(R.string.type), 1);
                startActivity(new Intent(LostFoundActivity.this, LostActivity.class).putExtras(bundle));
            }
        });
        RxEventBus.add(this, disp1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, DashboardActivity.class));
        super.onBackPressed();
    }
}
