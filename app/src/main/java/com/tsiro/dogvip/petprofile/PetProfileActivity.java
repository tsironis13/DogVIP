package com.tsiro.dogvip.petprofile;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.ImageViewPagerActivity;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.chatroom.ChatRoomActivity;
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
    //use this option to hide message icon and disable owner profile photo click when current activity is called from OwnerProfileActivity
    private int viewFrom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pet_profile);
        setSupportActionBar(mBinding.toolbar);
        mBinding.colTlbrLyt.setExpandedTitleColor(Color.parseColor("#00FFFFFF"));
        PetProfilePresenter mPresenter = new PetProfilePresenter(this);
        if (savedInstanceState != null) {
            petObj = savedInstanceState.getParcelable(getResources().getString(R.string.pet_obj));
            petUrls = savedInstanceState.getStringArray(getResources().getString(R.string.urls));
            viewFrom = savedInstanceState.getInt(getResources().getString(R.string.view_from));
        } else {
            if (getIntent() != null) {
                if (getIntent().getExtras().getInt(getResources().getString(R.string.view_from)) != 0) viewFrom = 2020;
                petObj = getIntent().getExtras().getParcelable(getResources().getString(R.string.pet_obj));
                petUrls = getIntent().getExtras().getStringArray(getResources().getString(R.string.urls));
//                Log.e(debugTag, petUrls+"");
//                Log.e(debugTag, petObj.getTotal_likes()+"");
            }
        }
        if (getSupportActionBar()!= null) {
            if (viewFrom != 2020) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(petObj.getP_name());
        }
        mBinding.setPetobj(petObj);
        mBinding.setPresenter(mPresenter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(debugTag, "onResume" + petObj.getTotal_likes() +" TOTAL LIKES");
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
                if (viewFrom != 2020) {
                    Intent intent = new Intent(PetProfileActivity.this, OwnerPetsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt(getResources().getString(R.string.user_role_id), petObj.getUser_role_id());
                    startActivity(intent.putExtras(bundle));
                }
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
        outState.putInt(getResources().getString(R.string.view_from), viewFrom);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (viewFrom == 2020) return false;
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chatItem:
                sendMsg();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    private void sendMsg() {
        Bundle bundle = new Bundle();
        bundle.putInt(getResources().getString(R.string.role), 1);
        bundle.putInt(getResources().getString(R.string.user_role_id), petObj.getUser_role_id());
        bundle.putInt(getResources().getString(R.string.pet_id), petObj.getId());
        bundle.putString(getResources().getString(R.string.action), getResources().getString(R.string.get_chat_rooom_msgs_by_participants));
        bundle.putString(getResources().getString(R.string.receiver), petObj.getOwnername());
        bundle.putString(getResources().getString(R.string.receiver_surname), petObj.getSurname());
        bundle.putString(getResources().getString(R.string.pet_name), petObj.getP_name());
        startActivity(new Intent(this, ChatRoomActivity.class).putExtras(bundle));
    }
}
