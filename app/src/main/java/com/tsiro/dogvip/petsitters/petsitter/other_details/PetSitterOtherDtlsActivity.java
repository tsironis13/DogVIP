package com.tsiro.dogvip.petsitters.petsitter.other_details;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.PetSitterOtherDetailsViewPager;
import com.tsiro.dogvip.base.activity.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityPetSitterOtherDetailsBinding;
import com.tsiro.dogvip.petsitters.PetSittersActivity;
import com.tsiro.dogvip.petsitters.petsitter.PetSitterActivity;
import com.tsiro.dogvip.petsitters.petsitter.PetSitterContract;
import com.tsiro.dogvip.petsitters.petsitter.PetSitterViewModel;
import com.tsiro.dogvip.requestmngrlayer.PetSitterRequestManager;

import java.util.ArrayList;

/**
 * Created by giannis on 8/9/2017.
 */

public class PetSitterOtherDtlsActivity extends BaseActivity implements PetSitterOtherDtlsContract.View,PetSitterContract.View, ViewPager.OnPageChangeListener {

    private static final String debugTag = PetSitterOtherDtlsActivity.class.getSimpleName();
    private ActivityPetSitterOtherDetailsBinding mBinding;
    private PetSitterObj petSitterObj;
    private PetSitterViewModel mViewModel;
    private Snackbar mSnackBar;
    private String mToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pet_sitter_other_details);
        setSupportActionBar(mBinding.incltoolbar.toolbar);

        setTitle(getResources().getStringArray(R.array.petsitter_other_details_tabs)[0]);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToken = getMyAccountManager().getAccountDetails().getToken();
        mViewModel = new PetSitterViewModel(PetSitterRequestManager.getInstance());
        mBinding.setPageIndex(0);

        if (savedInstanceState != null) {
            petSitterObj = savedInstanceState.getParcelable(getResources().getString(R.string.parcelable_obj));
        } else {
            if (getIntent() != null) petSitterObj = getIntent().getExtras().getParcelable(getResources().getString(R.string.parcelable_obj));
        }
        PetSitterOtherDetailsViewPager viewPager = new PetSitterOtherDetailsViewPager(getSupportFragmentManager(), petSitterObj);
        mBinding.viewPgr.enablePaging(false);
        mBinding.viewPgr.addOnPageChangeListener(this);
        mBinding.viewPgr.setAdapter(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
//        RxEventBus.unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getResources().getString(R.string.parcelable_obj), petSitterObj);
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
        startActivity(new Intent(this, PetSitterActivity.class));
        finish();
        super.onBackPressed();
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        mBinding.setPageIndex(position);
        setTitle(getResources().getStringArray(R.array.petsitter_other_details_tabs)[position]);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public void updatePetSitterPlaceImages(ArrayList<Image> urls) {
        petSitterObj.setUrls(urls);
    }

    @Override
    public void onOtherDetailsSubmit(PetSitterObj petSitterObj) {
        if (isNetworkAvailable()) {
            petSitterObj.setAuthtoken(mToken);
            petSitterObj.setAction(getResources().getString(R.string.edit_petsitter));
            petSitterObj.setSubaction(getResources().getString(R.string.edit_other_details));
            petSitterObj.setServices(this.petSitterObj.getServices());
            mViewModel.petSitterRelatedActions(petSitterObj);
            initializeProgressDialog(getResources().getString(R.string.please_wait));
        } else {
            showSnackBar(getResources().getString(R.string.no_internet_connection), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close));
        }
    }

    @Override
    public void onNextClick(int position, PetSitterObj petSitterObj) {
        mBinding.viewPgr.setCurrentItem(position);
        this.petSitterObj = petSitterObj;
    }

    @Override
    public void onPreviousClick(int position) {
        mBinding.viewPgr.setCurrentItem(position);
    }

    @Override
    public void onSuccess(PetSitterObj response) {
        dismissDialog();
        startActivity(new Intent(this, PetSittersActivity.class));
        finish();
//        showSnackBar(getResources().getString(R.string.success_action), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close));
    }

    @Override
    public void onError(int resource, boolean msglength) {
        dismissDialog();
        showSnackBar(getResources().getString(resource), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close));
    }

    @Override
    public void onImageActionSuccess(Image image) {}

    @Override
    public void onImageActionError() {}

    private void showSnackBar(final String msg, final String action, final int length, final String actionText) {
        mSnackBar = Snackbar
                        .make(mBinding.coordlt, msg, length)
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
