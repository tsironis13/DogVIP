package com.tsiro.dogvip.petsitters;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.POJO.petsitter.BookingObj;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsRequest;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.dashboard.DashboardActivity;
import com.tsiro.dogvip.databinding.ActivityManipulateNewSitterBookingBinding;
import com.tsiro.dogvip.requestmngrlayer.PetSitterRequestManager;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by thomatou on 9/19/17.
 */

public class ManipulateNewSitterBookingActivity extends BaseActivity implements PetSittersContract.View {

    private static final String debugTag = ManipulateNewSitterBookingActivity.class.getSimpleName();
    private PetSittersContract.ViewModel mViewModel;
    private ActivityManipulateNewSitterBookingBinding mBinding;
    private String mToken, action;
    private int bookingId;
    private Snackbar mSnackBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_manipulate_new_sitter_booking);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        mToken = getMyAccountManager().getAccountDetails().getToken();
        mViewModel = new PetSittersViewModel(PetSitterRequestManager.getInstance());
        if (getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getResources().getString(R.string.manipulate_new_sitter_booking_title));
        }
        if (savedInstanceState != null) {
            bookingId = savedInstanceState.getInt(getResources().getString(R.string.id));
        } else {
            if (getIntent().getExtras() != null) {
                bookingId = getIntent().getExtras().getInt(getResources().getString(R.string.id));
            }
        }
        getBookingDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.approveBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                manipulateBooking(getResources().getString(R.string.approve_booking));
            }
        });
        RxEventBus.add(this, disp);
        Disposable disp1 = RxView.clicks(mBinding.rejectBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                manipulateBooking(getResources().getString(R.string.reject_booking));
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
        outState.putInt(getResources().getString(R.string.id), bookingId);
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
    public void onSuccess(OwnerSitterBookingsResponse response) {
        dismissDialog();
        if (action.equals(getResources().getString(R.string.get_booking_details))) {
            mBinding.setObj(response.getBooking());
            Log.e(debugTag, response.getBooking().getServices() + " services");
            if (response.getBooking().getServices() != null) {
                if (response.getBooking().getServices().isEmpty()) {
                    TextView textView = new TextView(this);
                    textView.setText(getResources().getString(R.string.no_searched_services));
//                    textView.setTextColor(ContextCompat.getColor(this, R.color.color_white));
                    textView.setTypeface(Typeface.DEFAULT_BOLD);
                    mBinding.servicesLlt.addView(textView);
                } else {
                    String[] services = getResources().getStringArray(R.array.sitter_perspective_services);
                    for (Integer service : response.getBooking().getServices()) {
                        TextView textView = new TextView(this);
                        textView.setText(services[service-1]);
                        textView.setTypeface(Typeface.DEFAULT_BOLD);
//                        textView.setTextColor(ContextCompat.getColor(this, R.color.color_white));
                        mBinding.servicesLlt.addView(textView);
                    }
                }
            }
        } else {
            startActivity(new Intent(this, DashboardActivity.class));
        }
    }

    @Override
    public void onError(int resource) {
        dismissDialog();
        showSnackBar(getResources().getString(R.string.error), action, Snackbar.LENGTH_INDEFINITE, getResources().getString(R.string.retry));
    }

    private void manipulateBooking(String action) {
        this.action = action;
        if (isNetworkAvailable()) {
            OwnerSitterBookingsRequest request = new OwnerSitterBookingsRequest();
            request.setAuthtoken(mToken);
            request.setAction(action);
            request.setId(bookingId);
            mViewModel.getBookingDetails(request);
            initializeProgressDialog(getResources().getString(R.string.please_wait));
        } else {
            showSnackBar(getResources().getString(R.string.no_internet_connection), action, Snackbar.LENGTH_INDEFINITE, getResources().getString(R.string.retry));
        }
    }

    private void getBookingDetails() {
        action = getResources().getString(R.string.get_booking_details);
        if (isNetworkAvailable()) {
            OwnerSitterBookingsRequest request = new OwnerSitterBookingsRequest();
            request.setAuthtoken(mToken);
            request.setAction(action);
            request.setId(bookingId);
            mViewModel.getBookingDetails(request);
            initializeProgressDialog(getResources().getString(R.string.please_wait));
        } else {
            showSnackBar(getResources().getString(R.string.no_internet_connection), action, Snackbar.LENGTH_INDEFINITE, getResources().getString(R.string.retry));
        }
    }

    private void showSnackBar(final String msg, final String action, final int length, final String actionText) {
        mSnackBar = Snackbar
                .make(mBinding.scrollView, msg, length)
                .setAction(actionText, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (action.equals(getResources().getString(R.string.get_booking_details))) {
                            getBookingDetails();
                        }
                    }
                });
        mSnackBar.setActionTextColor(ContextCompat.getColor(this, android.R.color.black));
        View sbView = mSnackBar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        sbView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        textView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
        mSnackBar.show();
    }
}
