package com.tsiro.dogvip.petprofile;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.ImageViewPagerActivity;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.databinding.ActivityPetProfileBinding;
import com.tsiro.dogvip.ownerpets.OwnerPetsActivity;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 4/7/2017.
 */

public class PetProfileActivity extends AppCompatActivity implements PetProfileContract.View {

    private static final String debugTag = PetProfileActivity.class.getSimpleName();
    private ActivityPetProfileBinding mBinding;
    private String[] petUrls;
    private PetObj petObj;
    private PetProfilePresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pet_profile);
        setSupportActionBar(mBinding.toolbar);
        mBinding.colTlbrLyt.setExpandedTitleColor(Color.parseColor("#00FFFFFF"));
        mPresenter = new PetProfilePresenter(this);
        if (savedInstanceState != null) {
            petObj = savedInstanceState.getParcelable(getResources().getString(R.string.pet_obj));
            petUrls = savedInstanceState.getStringArray(getResources().getString(R.string.urls));
        } else {
            if (getIntent() != null) {
                petObj = getIntent().getExtras().getParcelable(getResources().getString(R.string.pet_obj));
                petUrls = getIntent().getExtras().getStringArray(getResources().getString(R.string.urls));
//                Log.e(debugTag, petUrls+"");
//                Log.e(debugTag, petObj.getTotal_likes()+"");
            }
        }
        if (getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(petObj.getP_name());
        }
        mBinding.setPetobj(petObj);
        mBinding.setPresenter(mPresenter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(debugTag, "onResume");
//        Disposable disp = RxView.clicks(mBinding.petImgv).subscribe(new Consumer<Object>() {
//            @Override
//            public void accept(@NonNull Object o) throws Exception {
//
//            }
//        });
//        RxEventBus.add(this, disp);
        Disposable disp1 = RxView.clicks(mBinding.profileImgv).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                Log.e(debugTag, petObj.getUser_role_id()+"");
                Intent intent = new Intent(PetProfileActivity.this, OwnerPetsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(getResources().getString(R.string.user_role_id), petObj.getUser_role_id());
                startActivity(intent.putExtras(bundle));
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getResources().getString(R.string.pet_obj), petObj);
        outState.putStringArray(getResources().getString(R.string.urls), petUrls);
    }

    @Override
    public void onPetImgClick() {
        Log.e(debugTag, petUrls.length+"");
        for (String u : petUrls) {
            Log.e(debugTag, u+" URL");
        }
        if (petUrls.length > 0) {
            Intent intent = new Intent(PetProfileActivity.this, ImageViewPagerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putStringArray(getResources().getString(R.string.urls), petUrls);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
