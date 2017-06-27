package com.tsiro.dogvip.mypets.pet;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.jakewharton.rxbinding2.view.RxView;
import com.rey.material.widget.SnackBar;
import com.tsiro.dogvip.uploadimagecontrol.ImageUploadControlActivity;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityPetBinding;
import com.tsiro.dogvip.mypets.ownerprofile.OwnerProfileActivity;
import com.tsiro.dogvip.requestmngrlayer.MyPetsRequestManager;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 23/6/2017.
 */

public class PetActivity extends BaseActivity implements PetContract.View {

    private static final String debugTag = PetActivity.class.getSimpleName();
    private PetContract.ViewModel mPetViewModel;
    private ActivityPetBinding mBinding;
    private boolean addPet, genre; //genre false -> male
    private SnackBar mSnackBar;
    private PetObj petObj;
    private String mToken;
    private int userRoleId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pet);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        mPetViewModel = new PetViewModel(MyPetsRequestManager.getInstance());
        mSnackBar = mBinding.mypetsSnckBr;
        mToken = getMyAccountManager().getAccountDetails().getToken();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_row, getResources().getStringArray(R.array.pet_genres));
//        adapter1.setDropDownViewResource(R.layout.spinner_row);
        mBinding.petgenreSpnr.setAdapter(adapter);
        if (getIntent() != null) {
            addPet = getIntent().getExtras().getBoolean(getResources().getString(R.string.add_pet));
            userRoleId = getIntent().getExtras().getInt(getResources().getString(R.string.user_role_id));
            Log.e(debugTag, addPet+"");
            if (addPet) { //add pet
                setTitle(getResources().getString(R.string.add_owner));
                petObj = new PetObj();
            } else { //edit pet
                mBinding.setShowimages(true);
                setTitle(getResources().getString(R.string.edit));
                petObj = getIntent().getExtras().getParcelable(getResources().getString(R.string.parcelable_obj));
                if (petObj.isNeutered() == 1) mBinding.neuteredChbx.setChecked(true);
                mBinding.petgenreSpnr.setSelection(petObj.isGenre());
                mBinding.setObj(petObj);
            }
        }
        if (getSupportActionBar()!= null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.showImagesBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                Intent intent = new Intent(PetActivity.this, ImageUploadControlActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(getResources().getString(R.string.urls), petObj.getUrls());
                bundle.putInt(getResources().getString(R.string.pet_id), petObj.getId());
                bundle.putInt(getResources().getString(R.string.user_role_id), userRoleId);
                startActivity(intent.putExtras(bundle));
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
    public Lifecycle.ViewModel getViewModel() {
        return mPetViewModel;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.submit_form_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
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
    public void onSuccess(OwnerObj response) {
        dismissDialog();
        if (response.getCode() == AppConfig.STATUS_OK) {
            Intent intent = new Intent(PetActivity.this, OwnerProfileActivity.class);
            intent.putExtra(getResources().getString(R.string.parcelable_obj), response);
            startActivity(intent);
        } else {
            showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.error), "");
        }
    }

    @Override
    public void onError(int resource, boolean msglength) {
        dismissDialog();
        int style = R.style.SnackBarSingleLine;
        if (msglength) style = R.style.SnackBarMultiLine;
        showSnackBar(style, getResources().getString(resource), "");
    }

    private void submitForm() {
        hideSoftKeyboard();
        AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mAwesomeValidation.addValidation(mBinding.nameEdt, ".*\\S.*", getResources().getString(R.string.required_field));
        mAwesomeValidation.addValidation(mBinding.raceEdt, ".*\\S.*", getResources().getString(R.string.required_field));
        mAwesomeValidation.addValidation(mBinding.microchipEdt, ".*\\S.*", getResources().getString(R.string.required_field));
        mAwesomeValidation.addValidation(mBinding.ageEdt, ".*\\S.*", getResources().getString(R.string.required_field));
        if (mAwesomeValidation.validate()) {
            if (isNetworkAvailable()) {
                petObj.setAuthtoken(mToken);
                petObj.setName(mBinding.nameEdt.getText().toString());
                petObj.setUser_role_id(userRoleId);
                petObj.setRace(mBinding.raceEdt.getText().toString());
                petObj.setMicroship(mBinding.microchipEdt.getText().toString());
                petObj.setAge(mBinding.ageEdt.getText().toString());
                petObj.setCity(mBinding.cityEdt.getText().toString());
                petObj.setWeight(mBinding.weightEdt.getText().toString());
                petObj.setCharacter(mBinding.chrctrEdt.getText().toString());
                petObj.setGenre(mBinding.petgenreSpnr.getSelectedItemPosition());
                petObj.setNeutered(mBinding.neuteredChbx.isChecked() ? 1 : 0);
                if (addPet) {
                    petObj.setAction(getResources().getString(R.string.add_pet));
                } else {
                    petObj.setAction(getResources().getString(R.string.edit_pet));
//                    petObj.setId();
                }
                initializeProgressDialog(getResources().getString(R.string.please_wait));
                mPetViewModel.submitPet(petObj);
            } else {
                showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.no_internet_connection), "");
            }
        }
    }

    public void showSnackBar(final int style, final String msg, final String action) {
        if (mSnackBar != null) {
            mSnackBar.applyStyle(style);
            mSnackBar.text(msg);
            mSnackBar.show();
        }
    }
}
