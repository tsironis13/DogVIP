package com.tsiro.dogvip.profs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.POJO.profs.ProfDetailsResponse;
import com.tsiro.dogvip.POJO.profs.ProfObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.RecyclerViewAdapter;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.chatroom.ChatRoomActivity;
import com.tsiro.dogvip.databinding.ActivitySearchedProfsBinding;
import com.tsiro.dogvip.profs.profprofile.ProfProfileContract;
import com.tsiro.dogvip.profs.profprofile.ProfProfileViewModel;
import com.tsiro.dogvip.requestmngrlayer.ProfRequestManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giannis on 7/10/2017.
 */

public class SearchedProfsActivity extends BaseActivity implements ProfProfileContract.View {

    private ActivitySearchedProfsBinding mBinding;
    private ArrayList<ProfObj> data;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapter rcvAdapter;
    private ProfProfileContract.ViewModel viewModel;
    private String phone;
    private boolean callPhone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_searched_profs);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        viewModel = new ProfProfileViewModel(ProfRequestManager.getInstance());
        if (getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getResources().getString(R.string.prof_profile_title));
        }
        if (savedInstanceState != null) {
            data = savedInstanceState.getParcelableArrayList(getResources().getString(R.string.data));
        } else {
            if (getIntent().getExtras() != null) {
                data = getIntent().getExtras().getParcelableArrayList(getResources().getString(R.string.data));
            }
        }
        if (callPhone) {
            makeCall(phone);
            callPhone = false;
        }
        initializeView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(getResources().getString(R.string.data), data);
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
    public Lifecycle.ViewModel getViewModel() {
        return viewModel;
    }

    private void initializeView() {
        linearLayoutManager = new LinearLayoutManager(this);
        if (rcvAdapter == null)
            mBinding.rcv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            rcvAdapter = new RecyclerViewAdapter(R.layout.searched_profs_rcv_row) {
                @Override
                protected Object getObjForPosition(int position, ViewDataBinding mBinding) {
                    return data.get(position);
                }

                @Override
                protected int getLayoutIdForPosition(int position) {
                    return R.layout.searched_profs_rcv_row;
                }

                @Override
                protected int getTotalItems() {
                    return data.size();
                }

                @Override
                protected Object getClickListenerObject() {
                    return viewModel;
                }
        };
        mBinding.rcv.setLayoutManager(linearLayoutManager);
        mBinding.rcv.setNestedScrollingEnabled(false);
        mBinding.rcv.setAdapter(rcvAdapter);
    }

    @Override
    public void onSuccess(ProfDetailsResponse response) {}

    @Override
    public void onError(int resource) {}

    @Override
    public void onPhoneIconViewClick(View view) {
        ProfObj profObj = data.get((int) view.getTag());
        phone = profObj.getMobile_number();
        getPermissionToMakePhoneCall(phone);
    }

    @Override
    public void onMessageIconClick(View view) {
        ProfObj obj = data.get((int)view.getTag());
        Bundle bundle = new Bundle();
        bundle.putInt(getResources().getString(R.string.role), 1);
        bundle.putInt(getResources().getString(R.string.user_role_id), obj.getId());
        bundle.putString(getResources().getString(R.string.action), getResources().getString(R.string.get_chat_rooom_msgs_by_participants));
        bundle.putString(getResources().getString(R.string.receiver), obj.getName());
        startActivity(new Intent(this, ChatRoomActivity.class).putExtras(bundle));
    }

    @Override
    public void onViewClick(View view) {
        ProfObj obj = data.get((int)view.getTag());
        Bundle bundle = new Bundle();
        bundle.putParcelable(getResources().getString(R.string.parcelable_obj), obj);
        startActivity(new Intent(this, ProfDetailsActivity.class).putExtras(bundle));
    }

    private void getPermissionToMakePhoneCall(String phone) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            makeCall(phone);
        } else {
            requestUserPermission();
        }
    }

    private void requestUserPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
//                showSnackBar(getResources().getString(R.string.grant_permission), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close));
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, AppConfig.CALL_PHONE_PERMISSION_RESULT_CODE);
            }
        } else {
            makeCall(phone);
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
}
