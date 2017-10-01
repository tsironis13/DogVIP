package com.tsiro.dogvip.petsitters;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.POJO.petsitter.BookingObj;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsRequest;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.LostFoundViewPager;
import com.tsiro.dogvip.adapters.PetSittersViewPager;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityPetSittersBinding;
import com.tsiro.dogvip.ownerpets.OwnerPetsActivity;
import com.tsiro.dogvip.petprofile.PetProfileActivity;
import com.tsiro.dogvip.petsitters.petsitter.PetSitterActivity;
import com.tsiro.dogvip.petsitters.sitter_assignment.SearchSitterFiltersActivity;
import com.tsiro.dogvip.requestmngrlayer.PetSitterRequestManager;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 3/9/2017.
 */

public class PetSittersActivity extends BaseActivity implements PetSittersContract.View {

    private static final String debugTag = PetSittersActivity.class.getSimpleName();
    private ActivityPetSittersBinding mBinding;
    private PetSittersContract.ViewModel mViewModel;
    private String mToken;
    private MenuItem searchItem;
    private ArrayList<PetObj> petObjList;
    private int userRoleId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pet_sitters);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        mViewModel = new PetSittersViewModel(PetSitterRequestManager.getInstance());
        mToken = getMyAccountManager().getAccountDetails().getToken();

        if (getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getResources().getString(R.string.pet_sitters_title));
        }
        fetchData(mToken);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.retryBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                fetchData(mToken);
            }
        });
        RxEventBus.add(this, disp);
        Disposable disp1 = RxView.clicks(mBinding.editPetSitterFab).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                startActivity(new Intent(PetSittersActivity.this, PetSitterActivity.class));
                finish();
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
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }

    @Override
    public void onBaseViewClick(View view) {}

    @Override
    public void onFragmentRcvItemClick(BookingObj bookingObj, int type) {
        if (type == 1) {//my pet assignments
            if (bookingObj.getCompleted() == 1 && bookingObj.getVote() == 0) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(getResources().getString(R.string.parcelable_obj), bookingObj);
                startActivity(new Intent(this, RateSitterActivity.class).putExtras(bundle));
                finish();
            }
            Log.e(debugTag, bookingObj.getCompleted() + " COMPLETED");
        } else { //sitter assignments
            Bundle bundle = new Bundle();
            if (bookingObj.getCompleted() == -1) { //pending
                bundle.putInt(getResources().getString(R.string.id), bookingObj.getId());
                startActivity(new Intent(this, ManipulateNewSitterBookingActivity.class).putExtras(bundle));
            } else {
                bundle.putInt(getResources().getString(R.string.user_role_id), bookingObj.getOwner_id());
                startActivity(new Intent(this, OwnerPetsActivity.class).putExtras(bundle));
            }
        }
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pet_sitters_menu, menu);
        searchItem = menu.findItem(R.id.search_item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_item:
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(getResources().getString(R.string.pet_list), petObjList);
                bundle.putInt(getResources().getString(R.string.user_role_id), userRoleId);
                startActivity(new Intent(this, SearchSitterFiltersActivity.class).putExtras(bundle));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSuccess(OwnerSitterBookingsResponse response) {
        dismissDialog();
//        Log.e(debugTag, "onSuccess =>" + response.getOwner_bookings() + " "+ response.getSitter_bookings());
        if (response.getCode() == AppConfig.STATUS_OK) {
            searchItem.setVisible(true);
            mBinding.setHaserror(false);
        } else if (response.getCode() == AppConfig.ERROR_NO_OWNER_EXISTS || response.getCode() == AppConfig.ERROR_NO_SITTER_EXISTS || response.getCode() == AppConfig.ERROR_NO_OWNER_AND_SITTER_EXIST){
            if (mBinding.getHaserror()) mBinding.setHaserror(false);
        }
        if (response.getCode() == AppConfig.ERROR_NO_SITTER_EXISTS) searchItem.setVisible(true);
        initializeView(response);
//        if (response.getCode() == AppConfig.ERROR_NO_OWNER_EXISTS || response.getCode() == AppConfig.ERROR_NO_OWNER_AND_SITTER_EXIST) {
//            searchItem.setVisible(false);
//        }
    }

    @Override
    public void onError(int resource) {
        dismissDialog();
        mBinding.setHaserror(true);
        mBinding.setErrortext(getResources().getString(resource));
    }

    private void fetchData(String mToken) {
        if (isNetworkAvailable()) {
            initializeProgressDialog(getResources().getString(R.string.please_wait));
            OwnerSitterBookingsRequest request = new OwnerSitterBookingsRequest();
            request.setAction(getResources().getString(R.string.get_owner_sitter_bookings));
            request.setAuthtoken(mToken);
            mViewModel.getOwnerSitterBookings(request);
        } else {
            mBinding.setHaserror(true);
            mBinding.setErrortext(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void initializeView(OwnerSitterBookingsResponse response) {
        if (response.getOwner_pets() != null) petObjList = response.getOwner_pets();
        userRoleId = response.getId();
        PetSittersViewPager viewPager = new PetSittersViewPager(getSupportFragmentManager(), getResources().getStringArray(R.array.pet_sitters_tabs), response);
        mBinding.viewPgr.enablePaging(false);
        mBinding.viewPgr.setAdapter(viewPager);
        mBinding.tabs.setupWithViewPager(mBinding.viewPgr);
    }
}
