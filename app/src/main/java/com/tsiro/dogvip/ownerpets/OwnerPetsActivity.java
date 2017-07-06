package com.tsiro.dogvip.ownerpets;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.POJO.mypets.OwnerRequest;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.petprofile.PetProfileActivity;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.RecyclerViewAdapter;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityOwnerPetsBinding;
import com.tsiro.dogvip.requestmngrlayer.MyPetsRequestManager;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 6/7/2017.
 */

public class OwnerPetsActivity extends BaseActivity implements OwnerPetsContract.View {

    private static final String debugTag = OwnerPetsActivity.class.getSimpleName();
    private OwnerPetsContract.ViewModel mViewModel;
    private ActivityOwnerPetsBinding mBinding;
    private String mToken;
    private OwnerPetsPresenter mPresenter;
    private OwnerObj ownerObj;
    private LinearLayoutManager linearLayoutManager;
    private int userRoleId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new OwnerPetsViewModel(MyPetsRequestManager.getInstance());
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_owner_pets);
        setSupportActionBar(mBinding.toolbar);
        mBinding.colTlbrLyt.setExpandedTitleColor(Color.parseColor("#00FFFFFF"));
        mToken = getMyAccountManager().getAccountDetails().getToken();
        mPresenter = new OwnerPetsPresenter(this);
        if (savedInstanceState != null) {
            userRoleId = savedInstanceState.getInt(getResources().getString(R.string.user_role_id));
        } else {
            if (getIntent() != null) {
                userRoleId = getIntent().getExtras().getInt(getResources().getString(R.string.user_role_id));
            }
        }
        fetchData(userRoleId);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.retryBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                fetchData(userRoleId);
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
        outState.putInt(getResources().getString(R.string.user_role_id), userRoleId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onPetImageClick(View view) {
        int position = (int) view.getTag();
        Intent intent = new Intent(this, PetProfileActivity.class);
        Bundle bundle = new Bundle();
        String[] urls = {};
        if (ownerObj.getPets().get(position).getStrurls() != null) urls = ownerObj.getPets().get(position).getStrurls().replace("[", "").replace("]", "").split(",");
        bundle.putParcelable(getResources().getString(R.string.pet_obj), ownerObj.getPets().get(position));
        bundle.putStringArray(getResources().getString(R.string.urls), urls);
        startActivity(intent.putExtras(bundle));
    }

    @Override
    public void onSuccess(final OwnerObj response) {
        dismissDialog();
        if (mBinding.getHaserror())mBinding.setHaserror(false);
        mBinding.setProcessing(false);
        mBinding.setOwner(response);
        ownerObj = response;
        linearLayoutManager = new LinearLayoutManager(this);
        RecyclerViewAdapter rcvAdapter = new RecyclerViewAdapter(R.layout.owner_pets_rcv_row) {
            @Override
            protected Object getObjForPosition(int position, ViewDataBinding mBinding) {
                return response.getPets().get(position);
            }

            @Override
            protected int getLayoutIdForPosition(int position) {
                return R.layout.owner_pets_rcv_row;
            }

            @Override
            protected int getTotalItems() {
                return response.getPets().size();
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

    private void fetchData(int userRoleId) {
        if (isNetworkAvailable()) {
            mBinding.setProcessing(true);
            OwnerRequest request = new OwnerRequest();
            request.setAction(getResources().getString(R.string.get_owner_pets));
            request.setAuthtoken(mToken);
            request.setId(userRoleId);
            mViewModel.getOwnerPets(request);
            initializeProgressDialog(getResources().getString(R.string.please_wait));
        } else {
            mBinding.setHaserror(true);
            mBinding.setErrortext(getResources().getString(R.string.no_internet_connection));
        }
    }
}
