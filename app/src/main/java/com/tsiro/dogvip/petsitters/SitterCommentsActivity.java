package com.tsiro.dogvip.petsitters;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.POJO.petsitter.BookingObj;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsRequest;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.RecyclerViewAdapter;
import com.tsiro.dogvip.base.activity.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivitySitterCommentsBinding;
import com.tsiro.dogvip.requestmngrlayer.PetSitterRequestManager;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 22/9/2017.
 */

public class SitterCommentsActivity extends BaseActivity implements PetSittersContract.View {

    private ActivitySitterCommentsBinding mBinding;
    private int id;//sitter id
    private String mToken, nameSurname;
    private PetSittersContract.ViewModel mViewModel;
    private RecyclerViewAdapter rcvAdapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sitter_comments);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        mViewModel = new PetSittersViewModel(PetSitterRequestManager.getInstance());
        mToken = getMyAccountManager().getAccountDetails().getToken();

        if (savedInstanceState != null) {
            id = savedInstanceState.getInt(getResources().getString(R.string.id));
            nameSurname = savedInstanceState.getString(getResources().getString(R.string.name_surname));
        } else {
            if (getIntent().getExtras() != null) {
                id = getIntent().getExtras().getInt(getResources().getString(R.string.id));
                nameSurname = getIntent().getExtras().getString(getResources().getString(R.string.name_surname));
            }
        }
        if (getSupportActionBar()!= null) {
            setTitle(nameSurname);
        }
        initializeView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.retryBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                initializeView();
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
        outState.putInt(getResources().getString(R.string.id), id);
        outState.putString(getResources().getString(R.string.name_surname), nameSurname);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onBaseViewClick(View view) {}

    @Override
    public void onFragmentRcvItemClick(BookingObj bookingObj, int type) {}

    @Override
    public void onSuccess(final OwnerSitterBookingsResponse response) {
        dismissDialog();
        if (mBinding.getHaserror()) mBinding.setHaserror(false);
        if (!response.getComment_data().isEmpty()) {
            linearLayoutManager = new LinearLayoutManager(this);
            rcvAdapter = new RecyclerViewAdapter(R.layout.sitter_comments_rcv_row) {
                @Override
                protected Object getObjForPosition(int position, ViewDataBinding mBinding) {
                    return response.getComment_data().get(position);
                }
                @Override
                protected int getLayoutIdForPosition(int position) {
                    return R.layout.sitter_comments_rcv_row;

                }
                @Override
                protected int getTotalItems() {
                    return response.getComment_data().size();
                }

                @Override
                protected Object getClickListenerObject() {
                    return null;
                }
            };
            mBinding.rcv.setLayoutManager(linearLayoutManager);
            mBinding.rcv.setNestedScrollingEnabled(false);
            mBinding.rcv.setAdapter(rcvAdapter);
        } else {
            mBinding.setHaserror(true);
            mBinding.setErrortext(getResources().getString(R.string.no_data));
        }
    }

    @Override
    public void onError(int resource) {
        dismissDialog();
        mBinding.setHaserror(true);
        mBinding.setErrortext(getResources().getString(R.string.error));
    }

    private void initializeView() {
        if (isNetworkAvailable()) {
            initializeProgressDialog(getResources().getString(R.string.please_wait));
            OwnerSitterBookingsRequest request = new OwnerSitterBookingsRequest();
            request.setAuthtoken(mToken);
            request.setAction(getResources().getString(R.string.get_sitter_comments));
            request.setId(id);
            mViewModel.getSitterComments(request);
        } else {
            mBinding.setHaserror(true);
            mBinding.setErrortext(getResources().getString(R.string.no_internet_connection));
        }
    }
}
