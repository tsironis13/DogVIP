package com.tsiro.dogvip.petsitters;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.POJO.chat.ChatRoom;
import com.tsiro.dogvip.POJO.petsitter.BookingObj;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsRequest;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.RecyclerViewAdapter;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityPendingSitterBookingsBinding;
import com.tsiro.dogvip.requestmngrlayer.PetSitterRequestManager;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by thomatou on 9/19/17.
 */

public class PendingSitterBookingsActivity extends BaseActivity implements PetSittersContract.View {

    private static final String debugTag = PendingSitterBookingsActivity.class.getSimpleName();
    private ActivityPendingSitterBookingsBinding mBinding;
    private String mToken;
    private PetSittersContract.ViewModel mViewModel;
    private List<BookingObj> data;
    private RecyclerViewAdapter rcvAdapter;
    private PetSittersPresenter mPetSittersPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pending_sitter_bookings);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        mToken = getMyAccountManager().getAccountDetails().getToken();
        mViewModel = new PetSittersViewModel(PetSitterRequestManager.getInstance());
        mPetSittersPresenter = new PetSittersPresenter(this);

        if (getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getResources().getString(R.string.pending_sitter_bookings_title));
        }
        fetchPendingBookings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.retryBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                fetchPendingBookings();
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
    public Lifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onBaseViewClick(View view) {
        Log.e(debugTag, view.getTag()  + " index");
        Bundle bundle = new Bundle();
        bundle.putInt(getResources().getString(R.string.id), data.get((int)view.getTag()).getId());
        Intent intent = new Intent(this, ManipulateNewSitterBookingActivity.class).putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onFragmentRcvItemClick(BookingObj bookingObj, int type) {}

    @Override
    public void onSuccess(OwnerSitterBookingsResponse response) {
        dismissDialog();
        if (response.getData().isEmpty()) {
            mBinding.setHasError(true);
            mBinding.setErrorText(getResources().getString(R.string.no_data));
        } else {
            data = response.getData();
            mBinding.setHasError(false);
            initializeRcView(data);
        }
    }

    @Override
    public void onError(int resource) {
        dismissDialog();
        mBinding.setHasError(true);
        mBinding.setErrorText(getResources().getString(R.string.error));
    }

    private void initializeRcView(final List<BookingObj> data) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvAdapter = new RecyclerViewAdapter(R.layout.pending_sitter_bookings_rcv_row) {
            @Override
            protected Object getObjForPosition(int position, ViewDataBinding mBinding) {
                return data.get(position);
            }

            @Override
            protected int getLayoutIdForPosition(int position) {
                return R.layout.pending_sitter_bookings_rcv_row;
            }

            @Override
            protected int getTotalItems() {
                return data.size();
            }

            @Override
            protected Object getClickListenerObject() {
                return mPetSittersPresenter;
            }
        };
        mBinding.rcv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mBinding.rcv.setLayoutManager(linearLayoutManager);
        mBinding.rcv.setNestedScrollingEnabled(false);
        mBinding.rcv.setAdapter(rcvAdapter);
    }

    private void fetchPendingBookings() {
        mBinding.setHasError(false);
        if (isNetworkAvailable()) {
            OwnerSitterBookingsRequest request = new OwnerSitterBookingsRequest();
            request.setAuthtoken(mToken);
            request.setAction(getResources().getString(R.string.get_pending_sitter_bookings));
            mViewModel.getPendingBookings(request);
            initializeProgressDialog(getResources().getString(R.string.please_wait));
        } else {
            mBinding.setHasError(true);
            mBinding.setErrorText(getResources().getString(R.string.no_internet_connection));
//            showSnackBar(getResources().getString(R.string.no_internet_connection), getResources().getString(R.string.retry), Snackbar.LENGTH_INDEFINITE);
        }
    }
}
