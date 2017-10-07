package com.tsiro.dogvip.petsitters.sitter_assignment;

import android.Manifest;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.ImageViewPagerActivity;
import com.tsiro.dogvip.POJO.DialogActions;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.POJO.petsitter.SearchedSittersResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.chatroom.ChatRoomActivity;
import com.tsiro.dogvip.databinding.ActivitySitterProfileBinding;
import com.tsiro.dogvip.petsitters.SitterCommentsActivity;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 16/9/2017.
 */

public class SitterProfileActivity extends BaseActivity {

    private static final String debugTag = SitterProfileActivity.class.getSimpleName();
    private SearchedSittersResponse data;
    private PetSitterObj petSitterObj;
    private ActivitySitterProfileBinding mBinding;
    private Snackbar mSnackBar;
    private boolean callPhone;
    private ArrayList<PetObj> petObjList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sitter_profile);
        setSupportActionBar(mBinding.toolbar);
        mBinding.colTlbrLyt.setExpandedTitleColor(Color.parseColor("#00FFFFFF"));

        if (savedInstanceState != null) {
            data = savedInstanceState.getParcelable(getResources().getString(R.string.data));
            petSitterObj = savedInstanceState.getParcelable(getResources().getString(R.string.parcelable_obj));
            petObjList = savedInstanceState.getParcelableArrayList(getResources().getString(R.string.pet_list));
        } else {
            if (getIntent().getExtras() != null) {
                data = getIntent().getExtras().getParcelable(getResources().getString(R.string.data));
                petSitterObj = getIntent().getExtras().getParcelable(getResources().getString(R.string.parcelable_obj));
                petObjList = getIntent().getExtras().getParcelableArrayList(getResources().getString(R.string.pet_list));
            }
        }
        if (petSitterObj != null) {
            mBinding.setSitter(petSitterObj);
            if (getSupportActionBar()!= null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                setTitle(petSitterObj.getName() + " " + petSitterObj.getSurname());
            }
            initializeView();
            if (callPhone) {
                makeCall(petSitterObj.getPhone());
                callPhone = false;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.showPlaceImagesBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                if (petSitterObj.getStrurls()!= null && !petSitterObj.getStrurls().isEmpty()) {
                    String[] urls = new String[petSitterObj.getStrurls().size()];
                    for (int i =0; i < petSitterObj.getStrurls().size(); i++) {
                        urls[i] = petSitterObj.getStrurls().get(i);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putStringArray(getResources().getString(R.string.urls), urls);
                    startActivity(new Intent(SitterProfileActivity.this, ImageViewPagerActivity.class).putExtras(bundle));
                }
            }
        });
        RxEventBus.add(this, disp);
        Disposable disp1 = RxView.clicks(mBinding.addBookingFlbtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                if (petObjList != null && !petObjList.isEmpty()) {
                    initializeBookingDialog(getResources().getString(R.string.add_booking_dialog), getResources().getString(R.string.add_booking_desc), getResources().getString(R.string.add_booking_title), getResources().getString(R.string.cancel), getResources().getString(R.string.ok));
                } else {
                    showSnackBar(getResources().getString(R.string.no_available_pets_for_booking), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close));
                }
            }
        });
        RxEventBus.add(this, disp1);
        Disposable disp2 = RxView.clicks(mBinding.showCommentsBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                Bundle bundle = new Bundle();
                bundle.putInt(getResources().getString(R.string.id), petSitterObj.getId());
                bundle.putString(getResources().getString(R.string.name_surname), petSitterObj.getName() + " "+petSitterObj.getSurname());
                startActivity(new Intent(SitterProfileActivity.this, SitterCommentsActivity.class).putExtras(bundle));
            }
        });
        RxEventBus.add(this, disp2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getResources().getString(R.string.data), data);
        outState.putParcelable(getResources().getString(R.string.parcelable_obj), petSitterObj);
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
        Bundle bundle = new Bundle();
        bundle.putParcelable(getResources().getString(R.string.parcelable_obj), data);
        bundle.putParcelableArrayList(getResources().getString(R.string.pet_list), petObjList);
        startActivity(new Intent(this, SearchedSittersListActivity.class).putExtras(bundle));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sitter_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.msgItem:
                sendMsg();
                return true;
            case R.id.phoneItem:
                getPermissionToMakePhoneCall(petSitterObj.getPhone());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return null;
    }

    private void requestUserPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                showSnackBar(getResources().getString(R.string.grant_permission), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close));
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, AppConfig.CALL_PHONE_PERMISSION_RESULT_CODE);
            }
        } else {
            makeCall(petSitterObj.getPhone());
        }
    }

    private void initializeView() {
        if (!petSitterObj.getServices().isEmpty()) {
            for (Integer service : petSitterObj.getServices()) {
                ViewGroup container = mBinding.servicesConstLlt;
                for (int i = 0; i < container.getChildCount(); i++) {
                    if (Integer.parseInt(container.getChildAt(i).getTag().toString()) == service) {
                        View view = container.getChildAt(i);
                        if (view instanceof CheckBox) {
                            view.setVisibility(View.VISIBLE);
                            ((CheckBox) view).setChecked(true);
                        }
                        if (view instanceof TextView) view.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
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

    private void sendMsg() {
        Bundle bundle = new Bundle();
        bundle.putInt(getResources().getString(R.string.role), 1);
        bundle.putInt(getResources().getString(R.string.user_role_id), petSitterObj.getId());
        bundle.putString(getResources().getString(R.string.action), getResources().getString(R.string.get_chat_rooom_msgs_by_participants));
        bundle.putString(getResources().getString(R.string.receiver), petSitterObj.getName());
        bundle.putString(getResources().getString(R.string.receiver_surname), petSitterObj.getSurname());
        startActivity(new Intent(this, ChatRoomActivity.class).putExtras(bundle));
    }


    private void initializeBookingDialog(String action, final String desc, String title, String positiveBtnTxt, String negativeBtnTxt) {
        Disposable dialogDisp = initializeGenericDialog(action, desc, title, positiveBtnTxt, negativeBtnTxt).subscribe(new Consumer<DialogActions>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull DialogActions obj) {
                if (obj.getAction().equals(getResources().getString(R.string.add_booking_dialog))) {
                    if (obj.getSelected_action() == 1) {//positive action
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(getResources().getString(R.string.parcelable_obj), petSitterObj);
                        bundle.putParcelable(getResources().getString(R.string.data), data);
                        bundle.putParcelableArrayList(getResources().getString(R.string.pet_list), petObjList);
                        startActivity(new Intent(SitterProfileActivity.this, BookingDetailsActivity.class).putExtras(bundle));
                        finish();
                    }
                }
            }
        });
        RxEventBus.add(this, dialogDisp);
    }

    private void showSnackBar(final String msg, final String action, final int length, final String actionText) {
        mSnackBar = Snackbar
                .make(mBinding.containerConstrLlt, msg, length)
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
