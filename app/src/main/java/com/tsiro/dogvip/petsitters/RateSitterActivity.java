package com.tsiro.dogvip.petsitters;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.POJO.petsitter.BookingObj;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsResponse;
import com.tsiro.dogvip.POJO.petsitter.RateBookingRequest;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.dashboard.DashboardActivity;
import com.tsiro.dogvip.databinding.ActivityRateSitterBinding;
import com.tsiro.dogvip.petsitters.petsitter.PetSitterActivity;
import com.tsiro.dogvip.petsitters.sitter_assignment.SearchSitterFiltersActivity;
import com.tsiro.dogvip.requestmngrlayer.PetSitterRequestManager;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by thomatou on 9/20/17.
 */

public class RateSitterActivity extends BaseActivity implements PetSittersContract.View{

    private static final String debugTag = RateSitterActivity.class.getSimpleName();
    private ActivityRateSitterBinding mBinding;
    private BookingObj bookingObj;
    private Snackbar mSnackBar;
    private PetSittersContract.ViewModel mViewModel;
    private String mToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_rate_sitter);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        if (getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getResources().getString(R.string.rate_sitter_title));
        }
        mViewModel = new PetSittersViewModel(PetSitterRequestManager.getInstance());
        mToken = getMyAccountManager().getAccountDetails().getToken();
        if (savedInstanceState != null) {
            bookingObj = savedInstanceState.getParcelable(getResources().getString(R.string.parcelable_obj));
        } else {
            if (getIntent().getExtras() != null) {
                bookingObj = getIntent().getExtras().getParcelable(getResources().getString(R.string.parcelable_obj));
            }
        }
        mBinding.setObj(bookingObj);
        if (bookingObj.getServices() != null) {
            if (bookingObj.getServices().isEmpty()) {
                TextView textView = new TextView(this);
                textView.setText(getResources().getString(R.string.no_searched_services));
//                    textView.setTextColor(ContextCompat.getColor(this, R.color.color_white));
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                mBinding.servicesLlt.addView(textView);
            } else {
                String[] services = getResources().getStringArray(R.array.user_perspective_services);
                for (Integer service : bookingObj.getServices()) {
                    TextView textView = new TextView(this);
                    textView.setText(services[service-1]);
                    textView.setTypeface(Typeface.DEFAULT_BOLD);
//                        textView.setTextColor(ContextCompat.getColor(this, R.color.color_white));
                    mBinding.servicesLlt.addView(textView);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        RxView.clicks(mBinding.addCommentEdt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                }, 200);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getResources().getString(R.string.parcelable_obj), bookingObj);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, PetSittersActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.submit_comment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submit_item:
                submitRating();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        startActivity(new Intent(this, PetSittersActivity.class));
        finish();
    }

    @Override
    public void onError(int resource) {
        dismissDialog();
        showSnackBar(getResources().getString(R.string.error), "", Snackbar.LENGTH_SHORT, getResources().getString(R.string.close));
    }

    private void submitRating() {
        if (isNetworkAvailable()) {
            if (mBinding.rating.getRating() != 0) {
                initializeProgressDialog(getResources().getString(R.string.please_wait));
                RateBookingRequest request = new RateBookingRequest();
                request.setAction(getResources().getString(R.string.rate_sitter_booking));
                request.setAuthtoken(mToken);
                request.setId(bookingObj.getId());
                request.setSitter_id(bookingObj.getSitter_id());
                request.setOwner_id(bookingObj.getOwner_id());
                request.setRating(mBinding.rating.getRating());
                request.setComment(mBinding.addCommentEdt.getText().toString());
                mViewModel.rateSitterBoooking(request);
//                Log.e(debugTag, mBinding.rating.getRating() + " RATING");
            } else {
                showSnackBar(getResources().getString(R.string.rating_not_valid), "", Snackbar.LENGTH_SHORT, getResources().getString(R.string.close));
            }
        } else {
            showSnackBar(getResources().getString(R.string.no_internet_connection), "", Snackbar.LENGTH_SHORT, getResources().getString(R.string.close));
        }
    }

    private void showSnackBar(final String msg, final String action, final int length, final String actionText) {
        mSnackBar = Snackbar
                .make(mBinding.scrollView, msg, length)
                .setAction(actionText, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {}
                });
        mSnackBar.setActionTextColor(ContextCompat.getColor(this, android.R.color.black));
        View sbView = mSnackBar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        sbView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        textView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
        mSnackBar.show();
    }


}
