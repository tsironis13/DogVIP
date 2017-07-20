package com.tsiro.dogvip.lostfound.manipulatelostpet;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.POJO.DialogActions;
import com.tsiro.dogvip.POJO.lostfound.LostFoundObj;
import com.tsiro.dogvip.POJO.lostfound.ManipulateLostFoundPet;
import com.tsiro.dogvip.POJO.lostfound.ManipulateLostFoundPetResponse;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.PickPetSpnrAdapter;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityManipulateLostPetBinding;
import com.tsiro.dogvip.lostfound.LostActivity;
import com.tsiro.dogvip.requestmngrlayer.ManipulateLostFoundRequestManager;
import com.tsiro.dogvip.utilities.DialogPicker;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 10/7/2017.
 */

public class ManipulateLostPetActivity extends BaseActivity implements ManipulateLostPetContract.View, AdapterView.OnItemSelectedListener {

    private static final String debugTag = ManipulateLostPetActivity.class.getSimpleName();
    private ActivityManipulateLostPetBinding mBinding;
    private ManipulateLostPetContract.ViewModel mViewModel;
    private String mToken, action;
    private ArrayList<PetObj> petObjs;
    private ManipulateLostFoundPet baseObj;
    private LostFoundObj lostFoundObj;
    private PickPetSpnrAdapter adapter;
    private int id, pet_id;
    private Bundle bundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_manipulate_lost_pet);
        mToken = getMyAccountManager().getAccountDetails().getToken();
        mViewModel = new ManipulateLostPetViewModel(ManipulateLostFoundRequestManager.getInstance());
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        if (savedInstanceState != null) {
            petObjs = savedInstanceState.getParcelableArrayList(getResources().getString(R.string.mypets));
            id = savedInstanceState.getInt(getResources().getString(R.string.id));
            action = savedInstanceState.getString(getResources().getString(R.string.action));
        } else {
            if (getIntent() != null) {
                petObjs = getIntent().getExtras().getParcelableArrayList(getResources().getString(R.string.mypets));
                id = getIntent().getExtras().getInt(getResources().getString(R.string.id));
                action = getIntent().getExtras().getString(getResources().getString(R.string.action));
            }
        }
        if (getSupportActionBar()!= null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        configureActivity(savedInstanceState);
//        Log.e(debugTag, petObjs.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Bundle args = new Bundle();
        Disposable disp = RxView.clicks(mBinding.dateLostEdt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Object o) throws Exception {
                args.putInt(getResources().getString(R.string.dialog_type), 0);
                DialogFragment dialogFragment = new DialogPicker();
                dialogFragment.setArguments(args);
                dialogFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
        RxEventBus.add(this, disp);
        Disposable disp1 = RxView.clicks(mBinding.timeLostEdt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Object o) throws Exception {
                args.putInt(getResources().getString(R.string.dialog_type), 1);
                DialogFragment dialogFragment = new DialogPicker();
                dialogFragment.setArguments(args);
                dialogFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        RxEventBus.add(this, disp1);
        Disposable disp2 = RxEventBus.createSubject(AppConfig.DIALOG_ACTION, 0).observeEvents(DialogActions.class).subscribe(new Consumer<DialogActions>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull DialogActions obj) throws Exception {
                if (obj.getAction().equals(getResources().getString(R.string.date_pick_action))) {
                    Log.e(debugTag, obj.getDisplay_date());
                    lostFoundObj.setDisplaydate(obj.getDisplay_date());
                    lostFoundObj.setDate_lost(obj.getDate());
                } else if (obj.getAction().equals(getResources().getString(R.string.time_pick_action))) {
//                    Log.e(debugTag, obj.getDisplay_date());
                    lostFoundObj.setTime_lost(obj.getDisplay_date());
                } else if (obj.getAction().equals(getResources().getString(R.string.dialog_cancel_date_action))) {
                    lostFoundObj.setDisplaydate("");
                } else {
                    lostFoundObj.setTime_lost("");
                }
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
        outState.putParcelableArrayList(getResources().getString(R.string.mypets), petObjs);
        outState.putParcelable(getResources().getString(R.string.parcelable_obj), lostFoundObj);
        outState.putInt(getResources().getString(R.string.id), id);
        outState.putString(getResources().getString(R.string.action), action);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        bundle = new Bundle();
        bundle.putInt(getResources().getString(R.string.type), 0);
        startActivity(new Intent(this, LostActivity.class).putExtras(bundle));
        super.onBackPressed();
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.submit_form_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_item:
                submitForm();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSuccess(ManipulateLostFoundPetResponse response) {
        dismissDialog();
        bundle = new Bundle();
        bundle.putInt(getResources().getString(R.string.type), 0);
        startActivity(new Intent(this, LostActivity.class).putExtras(bundle));
        finish();
    }

    @Override
    public void onError(int resource) {
        dismissDialog();
        showSnackBar(R.style.SnackBarSingleLine, getResources().getString(resource), "");
    }

    @Override
    public void onBaseViewClick(View view) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        Log.e(debugTag, petObjs.get(position).getId() +" SDKJSDJK");
        pet_id = petObjs.get(position).getId();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void configureActivity(Bundle savedInstanceState) {
        adapter = new PickPetSpnrAdapter(this, R.layout.pick_pet_spinner_row, petObjs);
        mBinding.mypetsSpnr.setAdapter(adapter);
        mBinding.mypetsSpnr.setOnItemSelectedListener(this);
        baseObj = new ManipulateLostFoundPet();
        if (action.equals(getResources().getString(R.string.add_ownr))) {
            setTitle(getResources().getString(R.string.add_owner));
            lostFoundObj = new LostFoundObj();
            baseObj.setAction(getResources().getString(R.string.add_lost_pet));
            baseObj.setLostfoundobj(lostFoundObj);
        } else {
            setTitle(getResources().getString(R.string.edit));
            baseObj.setAction(getResources().getString(R.string.edit_lost_pet));
            if (savedInstanceState != null) {
                lostFoundObj = savedInstanceState.getParcelable(getResources().getString(R.string.parcelable_obj));
            } else {
                lostFoundObj = getIntent().getExtras().getParcelable(getResources().getString(R.string.parcelable_obj));
//                Log.e(debugTag, lostFoundObj.getDisplaydate()+"sds");
            }
            int index = -1;
            for (PetObj obj : petObjs) {
                if (obj.getId() == lostFoundObj.getP_id()) index = petObjs.indexOf(obj);
            }
//            Log.e(debugTag, index+" a");
            if (index != -1)mBinding.mypetsSpnr.setSelection(index);
            baseObj.setLostfoundobj(lostFoundObj);
        }
        mBinding.setObj(baseObj);
    }

    private void submitForm() {
        hideSoftKeyboard();
        AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mAwesomeValidation.addValidation(mBinding.dateLostEdt, ".*\\S.*", getResources().getString(R.string.required_field));
        mAwesomeValidation.addValidation(mBinding.timeLostEdt, ".*\\S.*", getResources().getString(R.string.required_field));
        mAwesomeValidation.addValidation(mBinding.locationEdt, ".*\\S.*", getResources().getString(R.string.required_field));
        mAwesomeValidation.addValidation(mBinding.contactPhoneEdt, ".*\\S.*", getResources().getString(R.string.required_field));
        if (mAwesomeValidation.validate()) {
            if (isNetworkAvailable()) {
                baseObj.setAuthtoken(mToken);
                lostFoundObj.setLocation(mBinding.locationEdt.getText().toString());
                lostFoundObj.setPhone(mBinding.contactPhoneEdt.getText().toString());
                lostFoundObj.setInfo(mBinding.moreInfoEdt.getText().toString());
                lostFoundObj.setUser_id(id);
                lostFoundObj.setP_id(pet_id);
                mViewModel.manipulateLostPet(baseObj);
                initializeProgressDialog(getResources().getString(R.string.please_wait));
            } else {
                showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.no_internet_connection), "");
            }
        }
    }

    private void showSnackBar(final int style, final String msg, final String action) {
        if (mBinding.snckBr != null) {
            mBinding.snckBr.applyStyle(style);
            mBinding.snckBr.text(msg);
            mBinding.snckBr.show();
        }
    }

}
