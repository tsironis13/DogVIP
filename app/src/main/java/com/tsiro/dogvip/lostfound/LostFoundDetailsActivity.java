package com.tsiro.dogvip.lostfound;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.ImageViewPagerActivity;
import com.tsiro.dogvip.POJO.lostfound.LostFoundObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.databinding.ActivityLostFoundDetailsBinding;
import com.tsiro.dogvip.petprofile.PetProfileActivity;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 15/7/2017.
 */

public class LostFoundDetailsActivity extends AppCompatActivity {

    private static final String debugTag = LostFoundDetailsActivity.class.getSimpleName();
    private ActivityLostFoundDetailsBinding mBinding;
    private LostFoundObj lostFoundObj;
    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_lost_found_details);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        if (getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getResources().getString(R.string.info_title));
        }

        if (savedInstanceState != null) {
            lostFoundObj = savedInstanceState.getParcelable(getResources().getString(R.string.parcelable_obj));
            type = savedInstanceState.getInt(getResources().getString(R.string.type));
        } else {
            if (getIntent() != null) {
                lostFoundObj = getIntent().getExtras().getParcelable(getResources().getString(R.string.parcelable_obj));
                type = getIntent().getExtras().getInt(getResources().getString(R.string.type));
            }
        }
        mBinding.setType(type);
        mBinding.setObj(lostFoundObj);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.petImgv).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                if (lostFoundObj.getMain_image() != null) {
                    Intent intent = new Intent(LostFoundDetailsActivity.this, ImageViewPagerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArray(getResources().getString(R.string.urls), new String[] {lostFoundObj.getMain_image()});
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
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
        outState.putParcelable(getResources().getString(R.string.parcelable_obj), lostFoundObj);
        outState.putInt(getResources().getString(R.string.type), type);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
