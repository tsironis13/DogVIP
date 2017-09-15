package com.tsiro.dogvip.petsitters.sitter_assignment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import com.tsiro.dogvip.POJO.petsitter.SearchedSittersResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivitySearchedSittersListBinding;

import java.util.List;

/**
 * Created by thomatou on 9/15/17.
 */

public class SearchedSittersListActivity extends BaseActivity {

    private static final String debugTag = SearchedSittersListActivity.class.getSimpleName();
    private SearchedSittersResponse data;//list sitters and sitter booking details
    private ActivitySearchedSittersListBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_searched_sitters_list);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            data = savedInstanceState.getParcelable(getResources().getString(R.string.parcelable_obj));
        } else {
            if (getIntent().getExtras() != null) {
                data = getIntent().getExtras().getParcelable(getResources().getString(R.string.parcelable_obj));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getResources().getString(R.string.parcelable_obj), data);
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return null;
    }



}
