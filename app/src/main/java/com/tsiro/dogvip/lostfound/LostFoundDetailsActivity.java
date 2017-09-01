package com.tsiro.dogvip.lostfound;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.jakewharton.rxbinding2.view.RxView;
import com.rey.material.widget.SnackBar;
import com.tsiro.dogvip.ImageViewPagerActivity;
import com.tsiro.dogvip.POJO.lostfound.LostFoundObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.databinding.ActivityLostFoundDetailsBinding;
import com.tsiro.dogvip.petprofile.PetProfileActivity;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 15/7/2017.
 */

public class LostFoundDetailsActivity extends AppCompatActivity {

    private ActivityLostFoundDetailsBinding mBinding;
    private LostFoundObj lostFoundObj;
    private int type;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_lost_found_details);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        if (getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getResources().getString(R.string.info_title));
        }

        if (savedInstanceState != null) {
            lostFoundObj = savedInstanceState.getParcelable(getResources().getString(R.string.parcelable_obj));
            type = savedInstanceState.getInt(getResources().getString(R.string.type));
        } else {
            if (getIntent() != null) {
                lostFoundObj = getIntent().getExtras().getParcelable(getResources().getString(R.string.parcelable_obj));
                type = getIntent().getExtras().getInt(getResources().getString(R.string.type));
            }
        }
        mBinding.setType(type);
        mBinding.setObj(lostFoundObj);

        callbackManager = CallbackManager.Factory.create();
        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.success_action), getResources().getString(R.string.close));
            }

            @Override
            public void onError(FacebookException error) {
                showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.error), getResources().getString(R.string.close));
            }

            @Override
            public void onCancel() {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.petImgv).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                if (lostFoundObj.getMain_image() != null) {
                    Intent intent = new Intent(LostFoundDetailsActivity.this, ImageViewPagerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArray(getResources().getString(R.string.urls), new String[] {lostFoundObj.getMain_image()});
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
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
        outState.putParcelable(getResources().getString(R.string.parcelable_obj), lostFoundObj);
        outState.putInt(getResources().getString(R.string.type), type);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lost_found_pet_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_item:
                shareLostFoundPet();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareLostFoundPet() {
        String image = lostFoundObj.getThumb_image();
        String quoteText;
        if (image == null) image = "http://dogvip.votingsystem.gr/dev/api/images/logo.png";
        if (type == 0) {
            quoteText = getResources().getString(R.string.lost_pet_lbl);
            if (!lostFoundObj.getRace().equals("")) quoteText += " " + lostFoundObj.getRace() + " ";
        } else {
            quoteText = getResources().getString(R.string.found_pet_lbl);
            if (!lostFoundObj.getInfo().equals("")) quoteText += " " + lostFoundObj.getInfo() + " ";
        }
        quoteText += " στην περιοχή " + lostFoundObj.getLocation() + " στις: " + lostFoundObj.getDisplaydate() + " και ώρα: " + lostFoundObj.getTime_lost();

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(image))
                    .setQuote(quoteText)
                    .build();
            ShareDialog shareDialog = new ShareDialog(this);
            shareDialog.show(linkContent);
        }
    }

    private void showSnackBar(final int style, final String msg, final String action) {
        if (mBinding.mypetsSnckBr != null) {
            mBinding.mypetsSnckBr.applyStyle(style);
            mBinding.mypetsSnckBr.text(msg);
            mBinding.mypetsSnckBr.show();
        }
    }
}
