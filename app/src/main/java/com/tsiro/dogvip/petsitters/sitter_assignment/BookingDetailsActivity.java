package com.tsiro.dogvip.petsitters.sitter_assignment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.POJO.petsitter.BookingObj;
import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.POJO.petsitter.SearchedSittersResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.PickPetSpnrAdapter;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityBookingDetailsBinding;
import com.tsiro.dogvip.requestmngrlayer.SitterAssignmentRequestManager;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.util.ArrayList;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 16/9/2017.
 */

public class BookingDetailsActivity extends BaseActivity implements SitterAssignmentContract.View, AdapterView.OnItemSelectedListener {

    private static final String debugTag = BookingDetailsActivity.class.getSimpleName();
    private ActivityBookingDetailsBinding mBinding;
    private SearchedSittersResponse data;
    private PetSitterObj petSitterObj;
    private ArrayList<PetObj> petObjList;
    private PickPetSpnrAdapter mAdapter;
    private int pet_id;
    private Snackbar mSnackBar;
    private SitterAssignmentContract.ViewModel mViewModel;
    private String mToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_booking_details);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        mViewModel = new SitterAssignmentViewModel(SitterAssignmentRequestManager.getInstance());
        mToken = getMyAccountManager().getAccountDetails().getToken();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getResources().getString(R.string.booking_details_title));
        }
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
        mBinding.setObj(petSitterObj);
        mBinding.setBooking(data);
        initializeView();
        Log.e(debugTag, petObjList.get(0).getP_name()+"Services =? " + data.getLocation());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.bookSitterBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                sendBooking();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getResources().getString(R.string.data), data);
        outState.putParcelable(getResources().getString(R.string.parcelable_obj), petSitterObj);
        outState.putParcelableArrayList(getResources().getString(R.string.pet_list), petObjList);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(getResources().getString(R.string.data), data);
        bundle.putParcelable(getResources().getString(R.string.parcelable_obj), petSitterObj);
        bundle.putParcelableArrayList(getResources().getString(R.string.pet_list), petObjList);
        startActivity(new Intent(this, SitterProfileActivity.class).putExtras(bundle));
        finish();
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        pet_id = petObjList.get(position).getId();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    @Override
    public void onSuccess(SearchedSittersResponse response) {
        dismissDialog();
        showSnackBar(getResources().getString(R.string.success_action), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close));
    }

    @Override
    public void onError(int resource) {
        dismissDialog();
        showSnackBar(getResources().getString(resource), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close));
    }

    @Override
    public void onServiceCheckBoxClick(View view) {}

    @Override
    public void onPhoneImageViewClick(View view) {}

    @Override
    public void onBaseViewClick(View view) {}

    private void sendBooking() {
        if (isNetworkAvailable()) {
            Log.e(debugTag, "SIITER ID => "+petSitterObj.getId() + "\n" + " USER ROLE ID => "+data.getId() + "\n"  + " PET ID => " + pet_id + " \n" +  " SERVICES => "+ data.getServices()
                    + " \n" + " START DATE => "+data.getStartDate() + " \n" + " END DATE => "+data.getEndDate());
            BookingObj bookingObj = new BookingObj();
            bookingObj.setAuthtoken(mToken);
            bookingObj.setAction(getResources().getString(R.string.send_sitter_booking));
            bookingObj.setSitter_id(petSitterObj.getId());//sitter id
            bookingObj.setOwner_id(data.getId());
            bookingObj.setPet_id(pet_id);
            bookingObj.setLong_start_date(data.getStartDate());
            bookingObj.setLong_end_date(data.getEndDate());
            bookingObj.setServices(data.getServices());
            mViewModel.sendBooking(bookingObj);
            initializeProgressDialog(getResources().getString(R.string.please_wait));
        } else {
            showSnackBar(getResources().getString(R.string.no_internet_connection), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close));
        }
    }

    private void initializeView() {
        mAdapter = new PickPetSpnrAdapter(this, R.layout.pick_pet_spinner_row, petObjList);
        mBinding.mypetsSpnr.setAdapter(mAdapter);
        mBinding.mypetsSpnr.setOnItemSelectedListener(this);

        if (data.getServices().isEmpty()) {
            TextView textView = new TextView(this);
            textView.setText(getResources().getString(R.string.no_searched_services));
            textView.setTextColor(ContextCompat.getColor(this, R.color.color_white));
            mBinding.servicesLlt.addView(textView);
        } else {
            String[] services = getResources().getStringArray(R.array.user_perspective_services);
            for (Integer service : data.getServices()) {
                TextView textView = new TextView(this);
                textView.setText(services[service-1]);
                textView.setTextColor(ContextCompat.getColor(this, R.color.color_white));
                mBinding.servicesLlt.addView(textView);
            }
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

}
