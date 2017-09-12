package com.tsiro.dogvip.petsitters.sitter_assignment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.POJO.DialogActions;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivitySearchSitterFiltersBinding;
import com.tsiro.dogvip.utilities.DialogPicker;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by thomatou on 9/12/17.
 */

public class SearchSitterFiltersActivity extends BaseActivity implements SitterAssignmentContract.View {

    private static final String debugTag = SearchSitterFiltersActivity.class.getSimpleName();
    private ActivitySearchSitterFiltersBinding mBinding;
    private int pickerSelected;
    private SitterAssignmentPresenter sitterAssignmentPresenter;
    private List<Integer> services = new ArrayList<>();
    private SparseArray<CheckBox> servicesCheckBoxList = new SparseArray<>();

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
        sitterAssignmentPresenter = new SitterAssignmentPresenter(this);
        mBinding.setPresenter(sitterAssignmentPresenter);

        if (savedInstanceState != null) {
            pickerSelected = savedInstanceState.getInt(getResources().getString(R.string.picker_selected));
        }
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
                    if (obj.getCode() == AppConfig.STATUS_OK) {
                        if (pickerSelected == 1) {//start date picker
                            mBinding.startDateEdt.setText(obj.getDisplay_date());
                            Log.e(debugTag, "1");
                        } else if (pickerSelected == 2) {//end date picker
                            Log.e(debugTag, "2");
                            mBinding.endDateEdt.setText(obj.getDisplay_date());
                        }
//                        Log.e(debugTag, obj.getDisplay_date() + " DATE");
//                        mBinding.getPetsitter().setDisplayage(obj.getDisplay_date());
//                        mBinding.getPetsitter().setAge(obj.getDate());
                    } else {
                        if (pickerSelected == 1) {//start date picker
                            Log.e(debugTag, "1");
                        } else if (pickerSelected == 2) {//end date picker
                            Log.e(debugTag, "2");
                        }
//                        showSnackBar(getResources().getString(R.string.invalid_date), "", Snackbar.LENGTH_LONG,  getResources().getString(R.string.close)).subscribe();
                    }
                } else {
//                    petSitterObj.setDisplayage("");
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
        outState.putInt(getResources().getString(R.string.picker_selected), pickerSelected);
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
        return null;
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

    private void clearFilters() {
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
        if (!mBinding.startDateEdt.getText().toString().isEmpty()) mBinding.startDateEdt.setText("");
        if (!mBinding.endDateEdt.getText().toString().isEmpty()) mBinding.endDateEdt.setText("");
    }

}
