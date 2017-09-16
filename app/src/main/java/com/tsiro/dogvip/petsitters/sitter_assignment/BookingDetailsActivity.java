package com.tsiro.dogvip.petsitters.sitter_assignment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.POJO.petsitter.SearchedSittersResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityBookingDetailsBinding;

/**
 * Created by giannis on 16/9/2017.
 */

public class BookingDetailsActivity extends BaseActivity {

    private ActivityBookingDetailsBinding mBinding;
    private SearchedSittersResponse data;
    private PetSitterObj petSitterObj;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_booking_details);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        if (savedInstanceState != null) {
            data = savedInstanceState.getParcelable(getResources().getString(R.string.data));
            petSitterObj = savedInstanceState.getParcelable(getResources().getString(R.string.parcelable_obj));
        } else {
            if (getIntent().getExtras() != null) {
                data = getIntent().getExtras().getParcelable(getResources().getString(R.string.data));
                petSitterObj = getIntent().getExtras().getParcelable(getResources().getString(R.string.parcelable_obj));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getResources().getString(R.string.data), data);
        outState.putParcelable(getResources().getString(R.string.parcelable_obj), petSitterObj);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(getResources().getString(R.string.data), data);
        bundle.putParcelable(getResources().getString(R.string.parcelable_obj), petSitterObj);
        startActivity(new Intent(this, SitterProfileActivity.class).putExtras(bundle));
        finish();
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return null;
    }
}
