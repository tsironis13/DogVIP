package com.tsiro.dogvip;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.tsiro.dogvip.adapters.ImagePageAdapter;
import com.tsiro.dogvip.databinding.ActivityImageViewFlipperBinding;


/**
 * Created by giannis on 4/7/2017.
 */

public class ImageViewPagerActivity extends AppCompatActivity{

    private String[] urls;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityImageViewFlipperBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_view_flipper);
        if (savedInstanceState != null) {
            urls = savedInstanceState.getStringArray(getResources().getString(R.string.urls));
        } else {
            if (getIntent() != null) {
                urls = getIntent().getExtras().getStringArray(getResources().getString(R.string.urls));
            }
        }
        ImagePageAdapter imagePageAdapter = new ImagePageAdapter(this, urls);
        mBinding.vwPgr.setAdapter(imagePageAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(getResources().getString(R.string.urls), urls);
    }


}
