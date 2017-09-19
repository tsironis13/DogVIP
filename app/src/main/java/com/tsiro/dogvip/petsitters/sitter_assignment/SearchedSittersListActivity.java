package com.tsiro.dogvip.petsitters.sitter_assignment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.POJO.petsitter.SearchedSittersResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.RecyclerViewAdapter;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivitySearchedSittersListBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomatou on 9/15/17.
 */

public class SearchedSittersListActivity extends BaseActivity implements SitterAssignmentContract.View {

    private static final String debugTag = SearchedSittersListActivity.class.getSimpleName();
    private SearchedSittersResponse data;//list sitters and sitter booking details
    private ActivitySearchedSittersListBinding mBinding;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapter rcvAdapter;
    private SitterAssignmentPresenter sitterAssignmentPresenter;
    private boolean callPhone;
    private String phone;
    private Snackbar mSnackBar;
    private ArrayList<PetObj> petObjList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_searched_sitters_list);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        sitterAssignmentPresenter = new SitterAssignmentPresenter(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getResources().getString(R.string.pet_sitters_title));
        }
        if (savedInstanceState != null) {
            data = savedInstanceState.getParcelable(getResources().getString(R.string.parcelable_obj));
            petObjList = savedInstanceState.getParcelableArrayList(getResources().getString(R.string.pet_list));
        } else {
            if (getIntent().getExtras() != null) {
                data = getIntent().getExtras().getParcelable(getResources().getString(R.string.parcelable_obj));
                petObjList = getIntent().getExtras().getParcelableArrayList(getResources().getString(R.string.pet_list));
            }
        }
        Log.e(debugTag, " USER ROLE ID => "+data.getId());
        initializeView();
        if (callPhone) {
            makeCall(phone);
            callPhone = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getResources().getString(R.string.parcelable_obj), data);
        outState.putParcelableArrayList(getResources().getString(R.string.pet_list), petObjList);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @android.support.annotation.NonNull String[] permissions, @android.support.annotation.NonNull int[] grantResults) {
        switch (requestCode) {
            case AppConfig.CALL_PHONE_PERMISSION_RESULT_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    callPhone = true;
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(getResources().getString(R.string.pet_list), petObjList);
        bundle.putInt(getResources().getString(R.string.user_role_id), data.getId());
        startActivity(new Intent(this, SearchSitterFiltersActivity.class).putExtras(bundle));
        finish();
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return null;
    }

    @Override
    public void onSuccess(SearchedSittersResponse response) {
    }

    @Override
    public void onError(int resource) {
    }

    @Override
    public void onServiceCheckBoxClick(View view) {
    }

    @Override
    public void onPhoneImageViewClick(View view) {
        PetSitterObj petSitterObj = data.getData().get((int) view.getTag());
        phone = petSitterObj.getPhone();
        getPermissionToMakePhoneCall(phone);
    }

    @Override
    public void onBaseViewClick(View view) {
        PetSitterObj petSitterObj = data.getData().get((int) view.getTag());
        Bundle bundle = new Bundle();
        bundle.putParcelable(getResources().getString(R.string.data), data);
        bundle.putParcelable(getResources().getString(R.string.parcelable_obj), petSitterObj);
        bundle.putParcelableArrayList(getResources().getString(R.string.pet_list), petObjList);
        startActivity(new Intent(this, SitterProfileActivity.class).putExtras(bundle));
        finish();
        Log.e(debugTag, "base virew" + data.getData().get((int) view.getTag()).getYearsexpr());
    }


    private void requestUserPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                showSnackBar(getResources().getString(R.string.grant_permission), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close));
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, AppConfig.CALL_PHONE_PERMISSION_RESULT_CODE);
            }
        } else {
            makeCall(phone);
        }
    }

    private void initializeView() {
        linearLayoutManager = new LinearLayoutManager(this);
        if (rcvAdapter == null)
            mBinding.rcv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rcvAdapter = new RecyclerViewAdapter(R.layout.searched_sitters_list_rcv_row) {
            @Override
            protected Object getObjForPosition(int position, ViewDataBinding mBinding) {
//                Log.e(debugTag, data.getData().get(position).getPhone() + "phone");
                return data.getData().get(position);
            }

            @Override
            protected int getLayoutIdForPosition(int position) {
                return R.layout.searched_sitters_list_rcv_row;
            }

            @Override
            protected int getTotalItems() {
                return data.getData().size();
            }

            @Override
            protected Object getClickListenerObject() {
                return sitterAssignmentPresenter;
            }
        };
        mBinding.rcv.setLayoutManager(linearLayoutManager);
        mBinding.rcv.setNestedScrollingEnabled(false);
        mBinding.rcv.setAdapter(rcvAdapter);
    }

    private void getPermissionToMakePhoneCall(String phone) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            makeCall(phone);
        } else {
            requestUserPermission();
        }
    }

    private void makeCall(String phone) {
        if (phone != null && !phone.isEmpty()) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+phone));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) return;
            startActivity(callIntent);
        }
    }

    private void showSnackBar(final String msg, final String action, final int length, final String actionText) {
        mSnackBar = Snackbar
                .make(mBinding.containerLlt, msg, length)
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
