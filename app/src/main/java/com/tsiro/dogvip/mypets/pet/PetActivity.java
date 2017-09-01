package com.tsiro.dogvip.mypets.pet;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.jakewharton.rxbinding2.view.RxView;
import com.rey.material.widget.SnackBar;
import com.tsiro.dogvip.POJO.DialogActions;
import com.tsiro.dogvip.POJO.Image;
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
import com.tsiro.dogvip.utilities.DialogPicker;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.util.ArrayList;
import java.util.Arrays;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 23/6/2017.
 */

public class PetActivity extends BaseActivity implements PetContract.View {

    private PetContract.ViewModel mPetViewModel;
    private ActivityPetBinding mBinding;
    private boolean addPet; //genre false -> male
    private PetObj petObj;
    private String mToken, mainImageUrl;
    private int userRoleId, index;
    private ArrayList<Image> urls;
    private ArrayAdapter<String> radapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pet);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        mPetViewModel = new PetViewModel(MyPetsRequestManager.getInstance());
        mToken = getMyAccountManager().getAccountDetails().getToken();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_row, getResources().getStringArray(R.array.pet_genres));
        mBinding.petgenreSpnr.setAdapter(adapter);

        ArrayAdapter<String> cadapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, AppConfig.cities);
        mBinding.cityEdt.setAdapter(cadapter);

        radapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, AppConfig.races);
        mBinding.raceEdt.setAdapter(radapter);

        if (savedInstanceState != null) {
            addPet = savedInstanceState.getBoolean(getResources().getString(R.string.add_pet));
            userRoleId = savedInstanceState.getInt(getResources().getString(R.string.user_role_id));
//            hasChanges = savedInstanceState.getBoolean(getResources().getString(R.string.has_changes));
            configureActivity(savedInstanceState);
        } else {
            if (getIntent() != null) {
                addPet = getIntent().getExtras().getBoolean(getResources().getString(R.string.add_pet));
                userRoleId = getIntent().getExtras().getInt(getResources().getString(R.string.user_role_id));
                configureActivity(null);
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
                bundle.putParcelable(getResources().getString(R.string.pet_obj), petObj);
                bundle.putInt(getResources().getString(R.string.index), index);
                bundle.putInt(getResources().getString(R.string.user_role_id), userRoleId);
                startActivityForResult(intent.putExtras(bundle), AppConfig.REFRESH_PET_INFO);
            }
        });
        RxEventBus.add(this, disp);
        Disposable disp2 = RxView.clicks(mBinding.ageEdt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Object o) throws Exception {
                Bundle args = new Bundle();
                args.putInt(getResources().getString(R.string.dialog_type), 0);
                DialogFragment dialogFragment = new DialogPicker();
                dialogFragment.setArguments(args);
                dialogFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        RxEventBus.add(this, disp2);
        Disposable disp1 = RxEventBus.createSubject(AppConfig.DIALOG_ACTION, 0).observeEvents(DialogActions.class).subscribe(new Consumer<DialogActions>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull DialogActions obj) throws Exception {
                if (obj.getAction().equals(getResources().getString(R.string.date_pick_action))) {
                    if (obj.getCode() == AppConfig.STATUS_OK) {
                        petObj.setP_displayage(obj.getDisplay_date());
                        petObj.setAge(obj.getDate());
                    } else {
                        showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.invalid_date), getResources().getString(R.string.close));
                    }
                } else {
                    petObj.setP_displayage("");
                }
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(getResources().getString(R.string.add_pet), addPet);
        outState.putInt(getResources().getString(R.string.user_role_id), userRoleId);
        outState.putInt(getResources().getString(R.string.index), index);
        if (petObj != null) outState.putParcelable(getResources().getString(R.string.pet_obj), petObj);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mPetViewModel;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
           if (requestCode == AppConfig.REFRESH_PET_INFO) {
                PetObj obj = data.getParcelableExtra(getResources().getString(R.string.pet_obj));
                if (obj.getMain_url() != null && !obj.getMain_url().equals("")) {
                    petObj.setMain_url(obj.getMain_url());
                    mainImageUrl = obj.getMain_url();
                }
                if (obj.getUrls() != null) {
                    petObj.setUrls(obj.getUrls());
                    urls = obj.getUrls();
                }
//                hasChanges = true;
            }
        }
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
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        if (addPet) {
            bundle.putBoolean(getResources().getString(R.string.canceled_on_add), true);
            setResult(RESULT_OK, new Intent().putExtras(bundle));
        } else {
            petObj.setMain_url(mainImageUrl);
            petObj.setUrls(urls);
            bundle.putParcelable(getResources().getString(R.string.pet_obj), petObj);
            bundle.putInt(getResources().getString(R.string.index), index);
            setResult(RESULT_OK, new Intent().putExtras(bundle));
        }
        finish();
        super.onBackPressed();
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
            Bundle bundle = new Bundle();
            bundle.putParcelable(getResources().getString(R.string.parcelable_obj), response);
            intent.putExtras(bundle);
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

    private void configureActivity(Bundle saveinstancestate) {
        if (addPet) { //add pet
            setTitle(getResources().getString(R.string.add_owner));
            petObj = new PetObj();
            petObj.setRace(getResources().getString(R.string.unknown));
        } else { //edit pet
            mBinding.setShowimages(true);
            setTitle(getResources().getString(R.string.edit));
            if (saveinstancestate != null) {
                petObj = saveinstancestate.getParcelable(getResources().getString(R.string.pet_obj));
                index = saveinstancestate.getInt(getResources().getString(R.string.index));
            } else {
                petObj = getIntent().getExtras().getParcelable(getResources().getString(R.string.pet_obj));
                index = getIntent().getExtras().getInt(getResources().getString(R.string.index));
            }
            mainImageUrl = petObj.getMain_url();
            urls = petObj.getUrls();
            if (petObj.isNeutered() == 1) mBinding.neuteredChbx.setChecked(true);
            if (petObj.getHalfblood() == 1) mBinding.haldBloodChbx.setChecked(true);
            mBinding.petgenreSpnr.setSelection(petObj.isGenre());
        }
        mBinding.setObj(petObj);
    }

    private void submitForm() {
        hideSoftKeyboard();
        AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mAwesomeValidation.addValidation(mBinding.nameEdt, ".*\\S.*", getResources().getString(R.string.required_field));
        mAwesomeValidation.addValidation(mBinding.raceEdt, ".*\\S.*", getResources().getString(R.string.required_field));
//        mAwesomeValidation.addValidation(mBinding.microchipEdt, "^\\d{15}$", getResources().getString(R.string.not_valid_microchip));
//        mAwesomeValidation.addValidation(mBinding.ageEdt, ".*\\S.*", getResources().getString(R.string.required_field));
        if (mAwesomeValidation.validate()) {
            if (isNetworkAvailable()) {
                if (!Arrays.asList(AppConfig.cities).contains(mBinding.cityEdt.getText().toString())) {
                    showSnackBar(R.style.SnackBarMultiLine, getResources().getString(R.string.city_no_match), getResources().getString(R.string.close));
                } else if (!Arrays.asList(AppConfig.races).contains(mBinding.raceEdt.getText().toString())) {
                    showSnackBar(R.style.SnackBarLongDuration, getResources().getString(R.string.race_not_match), getResources().getString(R.string.close));
                } else if (!mBinding.microchipEdt.getText().toString().isEmpty() && !mBinding.microchipEdt.getText().toString().equals("0") && !mBinding.microchipEdt.getText().toString().matches("^\\d{15}$")) {
                    showSnackBar(R.style.SnackBarMultiLine, getResources().getString(R.string.not_valid_microchip), getResources().getString(R.string.close));
                }
                else {
                    petObj.setAuthtoken(mToken);
                    petObj.setP_name(mBinding.nameEdt.getText().toString());
                    petObj.setUser_role_id(userRoleId);
                    petObj.setRace(mBinding.raceEdt.getText().toString());
                    petObj.setMicroship(mBinding.microchipEdt.getText().toString());
                    petObj.setP_displayage(mBinding.ageEdt.getText().toString());
                    petObj.setCity(mBinding.cityEdt.getText().toString());
                    petObj.setWeight(mBinding.weightEdt.getText().toString());
                    petObj.setCharacter(mBinding.chrctrEdt.getText().toString());
                    petObj.setGenre(mBinding.petgenreSpnr.getSelectedItemPosition());
                    petObj.setNeutered(mBinding.neuteredChbx.isChecked() ? 1 : 0);
                    petObj.setHalfblood(mBinding.haldBloodChbx.isChecked() ? 1 : 0);
                    if (addPet) {
                        petObj.setAction(getResources().getString(R.string.add_pet));
                    } else {
                        petObj.setAction(getResources().getString(R.string.edit_pet));
//                    petObj.setId();
                    }
                    initializeProgressDialog(getResources().getString(R.string.please_wait));
                    mPetViewModel.submitPet(petObj);
                }
            } else {
                showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.no_internet_connection), "");
            }
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
