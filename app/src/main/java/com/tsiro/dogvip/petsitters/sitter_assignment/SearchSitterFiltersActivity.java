package com.tsiro.dogvip.petsitters.sitter_assignment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.POJO.DialogActions;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.POJO.petsitter.SearchedSittersResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivitySearchSitterFiltersBinding;
import com.tsiro.dogvip.petsitters.PetSittersActivity;
import com.tsiro.dogvip.petsitters.petsitter.PetSitterActivity;
import com.tsiro.dogvip.requestmngrlayer.SitterAssignmentRequestManager;
import com.tsiro.dogvip.utilities.DialogPicker;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by thomatou on 9/12/17.
 */

public class SearchSitterFiltersActivity extends BaseActivity implements SitterAssignmentContract.View {

    private static final String debugTag = SearchSitterFiltersActivity.class.getSimpleName();
    private ActivitySearchSitterFiltersBinding mBinding;
    private int pickerSelected, userRoleId;
    private SitterAssignmentPresenter sitterAssignmentPresenter;
    private ArrayList<Integer> services;
    private SparseArray<CheckBox> servicesCheckBoxList = new SparseArray<>();
    private Snackbar mSnackBar;
    private PetSitterObj petSitterObj;
    private SitterAssignmentContract.ViewModel mViewModel;
    private long startDate, endDate;
    private String mToken;
    private ArrayList<PetObj> petObjList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_sitter_filters);
        setSupportActionBar(mBinding.incltoolbar.toolbar);

        if (getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getResources().getString(R.string.search_sitter_title));
        }
        mBinding.scrollView.smoothScrollTo(0, 0);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, AppConfig.cities);
        mBinding.cityEdt.setAdapter(adapter);
        mViewModel = new SitterAssignmentViewModel(SitterAssignmentRequestManager.getInstance());
        sitterAssignmentPresenter = new SitterAssignmentPresenter(this);
        mBinding.setPresenter(sitterAssignmentPresenter);
        mToken = getMyAccountManager().getAccountDetails().getToken();

        if (savedInstanceState != null) {
            pickerSelected = savedInstanceState.getInt(getResources().getString(R.string.picker_selected));
            startDate = savedInstanceState.getLong(getResources().getString(R.string.start_date));
            endDate = savedInstanceState.getLong(getResources().getString(R.string.end_date));
            services = savedInstanceState.getIntegerArrayList(getResources().getString(R.string.parcelable_list));
            petObjList = savedInstanceState.getParcelableArrayList(getResources().getString(R.string.pet_list));
            userRoleId = savedInstanceState.getInt(getResources().getString(R.string.user_role_id));
        } else {
            services = new ArrayList<>();
            if (getIntent().getExtras() != null) {
                petObjList = getIntent().getExtras().getParcelableArrayList(getResources().getString(R.string.pet_list));
                userRoleId = getIntent().getExtras().getInt(getResources().getString(R.string.user_role_id));
            }
        }
        Log.e(debugTag, " USER ROLE ID => "+userRoleId);
        petSitterObj = new PetSitterObj();
        servicesCheckBoxList.append(1, mBinding.service1ChckBx);
        servicesCheckBoxList.append(2, mBinding.service2ChckBx);
        servicesCheckBoxList.append(3, mBinding.service3ChckBx);
        servicesCheckBoxList.append(4, mBinding.service4ChckBx);
        servicesCheckBoxList.append(5, mBinding.service5ChckBx);
        servicesCheckBoxList.append(6, mBinding.service6ChckBx);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.startDateEdt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Object o) throws Exception {
                pickerSelected = 1;
                Bundle args = new Bundle();
                args.putInt(getResources().getString(R.string.dialog_type), 0);
                DialogFragment dialogFragment = new DialogPicker();
                dialogFragment.setArguments(args);
                dialogFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        RxEventBus.add(this, disp);
        Disposable disp1 = RxView.clicks(mBinding.endDateEdt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                pickerSelected = 2;
                Bundle args = new Bundle();
                args.putInt(getResources().getString(R.string.dialog_type), 0);
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
                    Log.e(debugTag, obj.getDisplay_date() + " DISPLAY DATE");
                    if (pickerSelected == 1) {//start date picker
                        mBinding.startDateEdt.setText(obj.getDisplay_date());
                        startDate = obj.getDate();
//                            petSitterObj.setStart_date(obj.getDate());
                    } else if (pickerSelected == 2) {//end date picker
                        mBinding.endDateEdt.setText(obj.getDisplay_date());
                        endDate = obj.getDate();
//                            petSitterObj.setEnd_date(obj.getDate());
                    }
                } else {
//                    petSitterObj.setDisplayage("");
                }
            }
        });
        RxEventBus.add(this, disp2);
        Disposable disp3 = RxView.clicks(mBinding.searchSitterBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                searchSitters();
            }
        });
        RxEventBus.add(this, disp3);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, PetSittersActivity.class));
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(getResources().getString(R.string.picker_selected), pickerSelected);
        outState.putIntegerArrayList(getResources().getString(R.string.parcelable_list), services);
        outState.putLong(getResources().getString(R.string.start_date), startDate);
        outState.putLong(getResources().getString(R.string.end_date), endDate);
        outState.putParcelableArrayList(getResources().getString(R.string.pet_list), petObjList);
        outState.putInt(getResources().getString(R.string.user_role_id), userRoleId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissSnackBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_sitter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_filters_item:
                clearFilters();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onServiceCheckBoxClick(View view) {
        Log.e(debugTag, "view checked unckedked");
        if (view instanceof CheckBox) {
            CheckBox checkBox = (CheckBox) view;
            if (checkBox.isChecked()) {
                services.add(Integer.valueOf(view.getTag().toString()));
            } else {
                if (services.contains(Integer.valueOf(view.getTag().toString()))) services.remove(Integer.valueOf(view.getTag().toString()));
            }
//            Log.e(debugTag, view.getTag() + " TAG, "+ checkBox.isChecked());
        }
    }

    @Override
    public void onBaseViewClick(View view) {}

    @Override
    public void onPhoneImageViewClick(View view) {}

    @Override
    public void onSuccess(SearchedSittersResponse response) {
        dismissDialog();
        if (response.getData().isEmpty()) {
            showSnackBar(getResources().getString(R.string.no_items), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close));
        } else {
            Log.e(debugTag, services + " SERV");
            Bundle bundle = new Bundle();
            if (services != null) response.setServices(services);
            response.setId(response.getId());
            response.setStartDate(startDate);
            response.setEndDate(endDate);
            response.setDiplayStartDate(mBinding.startDateEdt.getText().toString());
            response.setDisplayEndDate(mBinding.endDateEdt.getText().toString());
            response.setLocation(mBinding.cityEdt.getText().toString());
            response.setId(userRoleId);
            bundle.putParcelable(getResources().getString(R.string.parcelable_obj), response);
            bundle.putParcelableArrayList(getResources().getString(R.string.pet_list), petObjList);
            Intent intent = new Intent(this, SearchedSittersListActivity.class);
            startActivity(intent.putExtras(bundle));
            finish();
        }
    }

    @Override
    public void onError(int resource) {
        dismissDialog();
        showSnackBar(getResources().getString(resource), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close));
    }

    private void searchSitters() {
        hideSoftKeyboard();
        petSitterObj.setStart_date(startDate);
        petSitterObj.setEnd_date(endDate);
        Log.e(debugTag, petSitterObj.getStart_date() + " START DATE");
        Log.e(debugTag, petSitterObj.getEnd_date() + " END DATE");
        if (petSitterObj.getStart_date() == 0 || petSitterObj.getEnd_date() == 0) {
            showSnackBar(getResources().getString(R.string.dates_filter_required), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close));
        } else if (petSitterObj.getEnd_date() < petSitterObj.getStart_date()) {
            showSnackBar(getResources().getString(R.string.end_date_smaller_than_start_date), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close));
        } else if (!Arrays.asList(AppConfig.cities).contains(mBinding.cityEdt.getText().toString())) {
            showSnackBar(getResources().getString(R.string.city_no_match), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close));
        } else {
            if (isNetworkAvailable()) {
//                Log.e(debugTag, services + " SERVICES");
                Log.e(debugTag, mBinding.cityEdt.getText().toString() + " LOCATION");
                petSitterObj.setAuthtoken(mToken);
                petSitterObj.setAction(getResources().getString(R.string.search_pet_sitters));
                petSitterObj.setServices(services);
                String location = mBinding.cityEdt.getText().toString().isEmpty() ? "" : mBinding.cityEdt.getText().toString();
                petSitterObj.setCity(location);
                initializeProgressDialog(getResources().getString(R.string.please_wait));
                mViewModel.searchSitters(petSitterObj);
            } else {
                showSnackBar(getResources().getString(R.string.no_internet_connection), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close));
            }
        }
    }

    private void clearFilters() {
        hideSoftKeyboard();
        //dummy clear out
        ViewGroup innerConstrntLlt = mBinding.innerConstrntLlt;
        for (Integer service: services) {
            servicesCheckBoxList.get(service).setChecked(false);
//            for (int i =0; i < innerConstrntLlt.getChildCount(); i++) {
//                Log.e(debugTag, service + " TAG " + innerConstrntLlt.getChildAt(i).getTag());
//            }
//            CheckBox checkBox = (CheckBox) innerConstrntLlt.findViewWithTag(service);
//            checkBox.setChecked(false);
        }
        if (!mBinding.locationFilterTtv.getText().toString().isEmpty()) mBinding.cityEdt.setText("");
        if (!mBinding.startDateEdt.getText().toString().isEmpty()) {
            mBinding.startDateEdt.setText("");
            startDate = 0;
        }
        if (!mBinding.endDateEdt.getText().toString().isEmpty()) {
            mBinding.endDateEdt.setText("");
            endDate = 0;
        }
    }

    public void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

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

    private void dismissSnackBar() {
        if (mSnackBar != null && mSnackBar.isShown()) mSnackBar.dismiss();
    }

}
