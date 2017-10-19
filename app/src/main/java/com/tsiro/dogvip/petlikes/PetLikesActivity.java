package com.tsiro.dogvip.petlikes;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.POJO.petlikes.PetLikesRequest;
import com.tsiro.dogvip.POJO.petlikes.PetLikesResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.RecyclerViewAdapter;
import com.tsiro.dogvip.base.activity.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.chatroom.ChatRoomActivity;
import com.tsiro.dogvip.databinding.ActivityPetLikesBinding;
import com.tsiro.dogvip.ownerpets.OwnerPetsActivity;
import com.tsiro.dogvip.requestmngrlayer.PetLikesRequestManager;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.util.ArrayList;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 5/7/2017.
 */

public class PetLikesActivity extends BaseActivity implements PetLikesContract.View {

    private PetObj petObj;
    private PetLikesContract.ViewModel mViewModel;
    private ActivityPetLikesBinding mBinding;
    private String mToken;
    private PetLikesPresenter mPresenter;
    private ArrayList<OwnerObj> ownerObjs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new PetLikesViewModel(PetLikesRequestManager.getInstance());
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pet_likes);
        setSupportActionBar(mBinding.toolbar);
        mBinding.colTlbrLyt.setExpandedTitleColor(Color.parseColor("#00FFFFFF"));
        mToken = getMyAccountManager().getAccountDetails().getToken();
        mPresenter = new PetLikesPresenter(this);

        if (savedInstanceState != null) {
            petObj = savedInstanceState.getParcelable(getResources().getString(R.string.pet_obj));
        } else {
            if (getIntent() != null) {
                petObj = getIntent().getExtras().getParcelable(getResources().getString(R.string.pet_obj));
            }
        }
        if (getSupportActionBar()!= null && !mBinding.getHaserror()) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(petObj.getP_name());
        }
        fetchData(petObj);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.retryBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                fetchData(petObj);
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
        outState.putParcelable(getResources().getString(R.string.pet_obj), petObj);
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onPetImageClick(View view) {
        int position = (int) view.getTag();
        Intent intent = new Intent(PetLikesActivity.this, OwnerPetsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(getResources().getString(R.string.user_role_id), ownerObjs.get(position).getId());
        startActivity(intent.putExtras(bundle));
    }

    @Override
    public void onMessageIconClick(View view) {
        OwnerObj ownerObj = ownerObjs.get((int)view.getTag());
        Bundle bundle = new Bundle();
        bundle.putInt(getResources().getString(R.string.role), 1);
        bundle.putInt(getResources().getString(R.string.user_role_id), ownerObj.getId());
        bundle.putInt(getResources().getString(R.string.pet_id), petObj.getId());
        bundle.putString(getResources().getString(R.string.action), getResources().getString(R.string.get_chat_rooom_msgs_by_participants));
        bundle.putString(getResources().getString(R.string.receiver), ownerObj.getName());
        bundle.putString(getResources().getString(R.string.receiver_surname), ownerObj.getSurname());
        bundle.putString(getResources().getString(R.string.pet_name), petObj.getP_name());
        startActivity(new Intent(this, ChatRoomActivity.class).putExtras(bundle));
    }

    @Override
    public void onSuccess(final PetLikesResponse response) {
        dismissDialog();
        if (mBinding.getHaserror())mBinding.setHaserror(false);
        mBinding.setProcessing(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.rcv.setNestedScrollingEnabled(false);
        ownerObjs = response.getData();
        RecyclerViewAdapter rcvAdapter = new RecyclerViewAdapter(R.layout.pet_likes_rcv_row) {
            @Override
            protected Object getObjForPosition(int position, ViewDataBinding mBinding) {
                return response.getData().get(position);
            }

            @Override
            protected int getLayoutIdForPosition(int position) {
                return R.layout.pet_likes_rcv_row;
            }

            @Override
            protected int getTotalItems() {
                return response.getData().size();
            }

            @Override
            protected Object getClickListenerObject() {
                return mPresenter;
            }
        };
        mBinding.rcv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mBinding.rcv.setLayoutManager(linearLayoutManager);
        mBinding.rcv.setNestedScrollingEnabled(false);
        mBinding.rcv.setAdapter(rcvAdapter);
    }

    @Override
    public void onError(int resource) {
        dismissDialog();
        mBinding.setProcessing(false);
        if (!mBinding.getHaserror()) {
            mBinding.setHaserror(true);
            mBinding.setErrortext(getResources().getString(R.string.error));
        }
    }

    private void fetchData(PetObj petObj) {
        if (isNetworkAvailable()) {
            mBinding.setProcessing(true);
            mBinding.setPetobj(petObj);
            PetLikesRequest request = new PetLikesRequest();
            request.setAction(getResources().getString(R.string.get_pet_likes));
            request.setAuthtoken(mToken);
            request.setPet_id(petObj.getId());
            mViewModel.getPetLikes(request);
            initializeProgressDialog(getResources().getString(R.string.please_wait));
        } else {
            mBinding.setHaserror(true);
            mBinding.setErrortext(getResources().getString(R.string.no_internet_connection));
        }
    }

}
